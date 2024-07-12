package com.zartio.betterendgame.data.status_effect.displaced;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DisplacedStatusEffect extends StatusEffect {

    public DisplacedStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x98D982);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % (10 / (amplifier + 1)) == 0;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        Iterable<BlockPos> targets = BlockPos.iterateRandomly(
                entity.getRandom(), 1, entity.getBlockPos(), 24 * (amplifier + 1));
        World world = entity.getWorld();

        for (BlockPos target : targets) {
//            target.
//            entity.teleport(target.getX(), target.getY(), target.getZ(), true);
            while (!world.getBlockState(target.up()).isAir() || !world.getBlockState(target.up(2)).isAir()) {
                target = target.up(3);
            }

            entity.setPosition(target.getX(), target.getY(), target.getZ());
            entity.getEntityWorld().playSound(
                    null, target, SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
            break;
        }

        return true;
    }
}
