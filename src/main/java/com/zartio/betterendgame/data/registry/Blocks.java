package com.zartio.betterendgame.data.registry;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.block.warpstone_block.WarpstoneBlock;

public class Blocks {
    public static final Block WARPSTONE_BLOCK
            = registerBlock("warpstone_block", new WarpstoneBlock(Block.Settings.create().strength(50.0f)));

    public static void init() {}

    private static Block registerBlock(String name, Block block) {
        Block registeredBlock = Registry.register(Registries.BLOCK, Identifier.of(BetterEndgame.MOD_ID, name), block);
        Registry.register(
                Registries.ITEM,
                Identifier.of(BetterEndgame.MOD_ID, name),
                new BlockItem(registeredBlock, new Item.Settings()));
        return registeredBlock;
    }
}
