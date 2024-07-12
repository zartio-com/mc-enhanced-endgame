package com.zartio.betterendgame.data.item;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import com.zartio.betterendgame.data.block.warpstone_block.WarpstoneBlock;
import com.zartio.betterendgame.data.registry.Items;

public class EmptyWarpRuneItem extends Item {
    public EmptyWarpRuneItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Block usedOn = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        if (!(usedOn instanceof WarpstoneBlock)) {
            return ActionResult.PASS;
        }

        BlockPos destination = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (player == null) { return ActionResult.PASS; }

        PlayerInventory inventory = player.getInventory();

        if (inventory.getEmptySlot() == -1) {
            return ActionResult.FAIL;
        }

        ItemStack stack = new ItemStack(Items.INSCRIBED_WARP_RUNE_ITEM, 1);
        NbtCompound nbt = new NbtCompound();
        nbt.putString("destWorld", context.getWorld().getRegistryKey().getValue().toString());
        nbt.putInt("destX", destination.getX());
        nbt.putInt("destY", destination.getY());
        nbt.putInt("destZ", destination.getZ());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        context.getStack().decrementUnlessCreative(1, context.getPlayer());
        inventory.setStack(inventory.getEmptySlot(), stack);

        return ActionResult.SUCCESS;
    }
}
