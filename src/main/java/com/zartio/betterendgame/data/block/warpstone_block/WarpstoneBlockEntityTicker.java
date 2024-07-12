package com.zartio.betterendgame.data.block.warpstone_block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import com.zartio.betterendgame.data.entity.leeched_experience_orb.LeechedExperienceOrbEntity;

import java.util.ArrayList;
import java.util.List;

public class WarpstoneBlockEntityTicker implements BlockEntityTicker<WarpstoneBlockEntity> {
    private static final int MAXIMUM_EFFECT_DISTANCE = 1;
    private static final int EFFECTIVE_RADIUS = 3;

    private final List<Entity> targets = new ArrayList<>();
    private int age = 0;
    private final Random random = Random.create();

    public WarpstoneBlockEntityTicker() {

    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, WarpstoneBlockEntity blockEntity) {
        if (blockEntity.isCharged()) {
            this.tickCharged(world, pos, state, blockEntity);
        }

        age++;
    }

    private void tickCharged(World world, BlockPos pos, BlockState state, WarpstoneBlockEntity blockEntity) {
        if (age % 20 == 0) {
            this.expensiveUpdate(world, pos, state, blockEntity);
        }

        if (age % 2 != 0) {
            return;
        }

        List<Entity> toRemove = new ArrayList<>();
        for (Entity e : targets) {
            if (e == null || !e.isAlive() || e.getPos().distanceTo(pos.toCenterPos()) > EFFECTIVE_RADIUS) {
                toRemove.add(e);
                continue;
            }

            processEntity(e, world, pos, state, blockEntity);
        }

        targets.removeAll(toRemove);
    }

    private void processEntity(Entity e, World world, BlockPos pos, BlockState state, WarpstoneBlockEntity blockEntity) {
        double distanceToCenter = e.getPos().distanceTo(pos.toCenterPos());
        if (distanceToCenter <= MAXIMUM_EFFECT_DISTANCE) {
            if (e instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60));
                if (!(e instanceof PlayerEntity)) {
                    livingEntity.kill();
                }
            } else if (e instanceof ExperienceOrbEntity experienceOrb) {
                experienceOrb.discard();
                return;
            } else {
                e.discard();
            }
        }

        if (e instanceof PlayerEntity player) {
            leechExperienceFromPlayer(player, world, pos);
        }

        attractEntity(e, world, pos);
    }

    private void leechExperienceFromPlayer(PlayerEntity e, World world, BlockPos pos)
    {
        if (e.totalExperience <= 0) {
            return;
        }

        int leechedExperience = Math.max(e.totalExperience / 100, 10);
        if (leechedExperience > e.totalExperience) {
            leechedExperience = e.totalExperience;
        }

        e.addExperience(-leechedExperience);

        if (e.getPos().distanceTo(pos.up().toCenterPos()) <= MAXIMUM_EFFECT_DISTANCE) {
            return;
        }

        Vec3d expPos = e.getPos().add(random.nextGaussian() * 0.6, random.nextGaussian() * 0.6 + 1, random.nextGaussian() * 0.6);

        Entity exp = new LeechedExperienceOrbEntity(e.getWorld(), expPos.getX(), expPos.getY(), expPos.getZ());

        attractEntity(exp, world, pos);
        world.spawnEntity(exp);
    }

    private void attractEntity(Entity e, World world, BlockPos pos)
    {
        if (e instanceof PlayerEntity playerEntity) {
            if (playerEntity.isInCreativeMode() || playerEntity.isSpectator()) {
                return;
            }
        }

        Vec3d toCenter = pos.up().toCenterPos().subtract(e.getPos());
        double distance = toCenter.length();

        if (distance <= MAXIMUM_EFFECT_DISTANCE) {
            if (e instanceof LeechedExperienceOrbEntity) {
                e.discard();
                return;
            }

            if (e instanceof PlayerEntity playerEntity) {
                if (playerEntity.getVelocity() != Vec3d.ZERO) {
                    playerEntity.teleport(pos.toBottomCenterPos().getX(), pos.up().toBottomCenterPos().getY(), pos.toBottomCenterPos().getZ(), false);
                    playerEntity.setVelocity(Vec3d.ZERO);
                    playerEntity.velocityModified = true;
                }
            }
            return;
        }

        Vec3d direction = toCenter.normalize();
        double d = toCenter.lengthSquared();
        double ef = Math.max(1.0 - Math.sqrt(d) / 8.0, e instanceof LeechedExperienceOrbEntity ? 0.45 : 0.0);
        Vec3d force = direction.multiply(ef * ef * 0.5);

        e.addVelocity(force);
        e.velocityModified = true;
    }

    private void expensiveUpdate(World world, BlockPos pos, BlockState state, WarpstoneBlockEntity blockEntity) {
        Box radiusBox = new Box(
                pos.getX() - EFFECTIVE_RADIUS,
                pos.getY() - EFFECTIVE_RADIUS,
                pos.getZ() - EFFECTIVE_RADIUS,
                pos.getX() + EFFECTIVE_RADIUS,
                pos.getY() + EFFECTIVE_RADIUS,
                pos.getZ() + EFFECTIVE_RADIUS
        );

        targets.addAll(world.getOtherEntities(
                null, radiusBox, e -> !targets.contains(e) && e.getPos().distanceTo(pos.toCenterPos()) <= EFFECTIVE_RADIUS));
    }
}
