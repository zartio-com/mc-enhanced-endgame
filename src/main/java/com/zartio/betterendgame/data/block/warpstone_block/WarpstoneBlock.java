package com.zartio.betterendgame.data.block.warpstone_block;

import com.mojang.serialization.MapCodec;
import com.zartio.betterendgame.BetterEndgame;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.zartio.betterendgame.data.registry.BlockEntityTypes;
import com.zartio.betterendgame.data.registry.Potions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WarpstoneBlock extends BlockWithEntity implements Portal {
    public static final MapCodec<WarpstoneBlock> CODEC = createCodec(WarpstoneBlock::new);

    public static final BooleanProperty CHARGED = BooleanProperty.of("charged");

    private Random random = Random.create();

    public WarpstoneBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CHARGED, false));
    }

    @Override
    public void onPlaced(@NotNull World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        WarpstoneBlockEntity entity = (WarpstoneBlockEntity) world.getBlockEntity(pos);
        if (entity == null) {
            return;
        }

        entity.randomizeDestination();
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WarpstoneBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHARGED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1.0f);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combine(
                VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1.0f),
                VoxelShapes.cuboid(0.0625f, 0.5f, 0.0625f, 0.9375f, 1.0f, 0.9375f),
                BooleanBiFunction.OR
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }

        return validateTicker(type, BlockEntityTypes.WARPSTONE_BLOCK_ENTITY, new WarpstoneBlockEntityTicker());
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(Items.POTION)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        PotionContentsComponent potionContents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContents == null || (!potionContents.matches(Potions.CHORUS_ENERGY) && !potionContents.matches(Potions.CHORUS_ENERGY_1))) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        WarpstoneBlockEntity blockEntity = (WarpstoneBlockEntity) world.getBlockEntity(pos);
        assert blockEntity != null;

        if (blockEntity.getCharge() >= WarpstoneBlockEntity.MAX_CHARGES) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        boolean wasNotActive = blockEntity.isCharged();

        blockEntity.addCharge(potionContents.matches(Potions.CHORUS_ENERGY_1) ? 2 : 1);
        stack.decrementUnlessCreative(1, player);

        if (!wasNotActive) {
            LightningEntity e = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            e.setPosition(pos.toCenterPos());
            world.spawnEntity(e);
        }

        return ItemActionResult.SUCCESS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (entity.canUsePortals(true)) {
            entity.tryUsePortal(this, pos);
        }
    }

    @Nullable
    @Override
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        if (world == null) {
            return null;
        }

        WarpstoneBlockEntity blockEntity = (WarpstoneBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null || !blockEntity.isCharged() || !blockEntity.hasDestination()) {
            return null;
        }

        ServerWorld serverWorld = world.getServer().getWorld(blockEntity.getDestWorld());
        if (serverWorld == null) {
            return null;
        }

        BlockPos destination = blockEntity.getDestination();
        if (blockEntity.isDestinationRandom()) {
            BlockPos groundDestinationPos = findRandomTeleportTarget(serverWorld, destination);
            if (groundDestinationPos == null) {
                return null;
            }

            blockEntity.removeCharge(1);
            return getTeleportTarget(serverWorld, groundDestinationPos);
        }

        BlockPos teleportTarget = findSuitableTeleportTarget(serverWorld, destination);
        if (teleportTarget == null) {
            return null;
        }

        blockEntity.removeCharge(1);
        return getTeleportTarget(serverWorld, teleportTarget);
    }

    @Nullable
    private BlockPos findRandomTeleportTarget(ServerWorld world, BlockPos pos) {
        if (world == null) {
            return null;
        }

        if (world.getRegistryKey() == World.NETHER) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos current = pos.add(x, 0, z);
                    world.getChunk(current);

                    world.setBlockState(current, Blocks.NETHERRACK.getDefaultState());
                    for (int y = 1; y <= 5; y++) {
                        if (Math.abs(x) == 4 || Math.abs(z) == 4 || Math.abs(y) == 5) {
                            world.setBlockState(current.up(y), Blocks.GLASS.getDefaultState());
                        } else {
                            world.setBlockState(current.up(y), Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }

            return pos.up(1);
        } else if (world.getRegistryKey() == World.END) {
            return null;
        }

        world.getChunk(pos);
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
    }

    @Nullable
    private BlockPos findSuitableTeleportTarget(ServerWorld world, BlockPos pos) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (Math.abs(x) <= 2 && Math.abs(z) <= 2) {
                    continue;
                }

                positions.add(pos.add(x, 0, z));
            }
        }

        Collections.shuffle(positions);
        for (BlockPos position : positions) {
            world.getChunk(position);
            boolean isSuitable = true;
            for (int y = 0; y <= 1; y++) {
                BlockState checkBlockState = world.getBlockState(position.up(y));
                if (!checkBlockState.isAir()) {
                    isSuitable = false;
                    break;
                }
            }

            if (isSuitable) {
                return position;
            }
        }

        return null;
    }

    private TeleportTarget getTeleportTarget(ServerWorld world, BlockPos pos) {
        return new TeleportTarget(
                world,
                pos.toCenterPos(),
                new Vec3d(0, 0, 0),
                0,
                0,
                TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
        );
    }

    @Override
    public Effect getPortalEffect() {
        return Portal.super.getPortalEffect();
    }
}
