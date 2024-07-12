package com.zartio.betterendgame.data.block.temporary_warp_block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Portal;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import com.zartio.betterendgame.BetterEndgame;

public class TemporaryWarpBlock extends BlockWithEntity implements Portal {
    public static final MapCodec<TemporaryWarpBlock> CODEC = createCodec(TemporaryWarpBlock::new);

    public TemporaryWarpBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//        TestPortalBlockEntity portalEntity = (TestPortalBlockEntity) world.getBlockEntity(pos);
//        if (portalEntity == null) {
//            return;
//        }
//
//        portalEntity.randomizeDestination();
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        BetterEndgame.LOGGER.info("Stepped on {} at {}", pos, entity);
        if (entity.canUsePortals(true)) {
            BetterEndgame.LOGGER.info("Can use portal");
            entity.tryUsePortal(this, pos);
        }
    }

    @Override
    public int getPortalDelay(ServerWorld world, Entity entity) {
        return Portal.super.getPortalDelay(world, entity);
    }

    @Nullable
    @Override
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
//        TestPortalBlockEntity portalEntity = (TestPortalBlockEntity) world.getBlockEntity(pos);
//        if (portalEntity == null) {
//            return null;
//        }
//
//        RegistryKey<World> registryKey = World.OVERWORLD;
//        ServerWorld serverWorld = world.getServer().getWorld(registryKey);
//        if (serverWorld == null) {
//            return null;
//        }
//
//        return new TeleportTarget(
//                world,
//                portalEntity.getDestination().toBottomCenterPos(),
//                new Vec3d(10, 0, 10),
//                0,
//                0,
//                TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(entityx -> {
//                    if (entityx instanceof LivingEntity livingEntity) {
//                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 20 * 6));
//                    }
//
//                    TeleportTarget.ADD_PORTAL_CHUNK_TICKET.onTransition(entityx);
//                })
//        );
        return null;
    }

    @Override
    public Effect getPortalEffect() {
        return Portal.super.getPortalEffect();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
//        return new TestPortalBlockEntity(pos, state);
        return null;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
