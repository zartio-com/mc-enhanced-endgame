package com.zartio.betterendgame.data.registry;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.block.warpstone_block.WarpstoneBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class BlockEntityTypes {
    public static final BlockEntityType<WarpstoneBlockEntity> WARPSTONE_BLOCK_ENTITY
            = registerBlockEntity("warpstone_block_entity",WarpstoneBlockEntity::new, Blocks.WARPSTONE_BLOCK);

    public static void init() {}

    private static <T extends BlockEntity> net.minecraft.block.entity.BlockEntityType<T> registerBlockEntity(
            String name,
            net.minecraft.block.entity.BlockEntityType.BlockEntityFactory<T> factory,
            Block block
    ) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BetterEndgame.MOD_ID, name),
                net.minecraft.block.entity.BlockEntityType.Builder.create(factory, block).build());
    }
}
