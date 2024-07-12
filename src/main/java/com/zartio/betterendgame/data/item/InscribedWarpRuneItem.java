package com.zartio.betterendgame.data.item;

import com.zartio.betterendgame.BetterEndgame;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import com.zartio.betterendgame.data.block.warpstone_block.WarpstoneBlockEntity;

import java.util.List;

public class InscribedWarpRuneItem extends Item {
    public InscribedWarpRuneItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        NbtComponent nbtData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtData == null) {
            return;
        }

        NbtCompound nbt = nbtData.copyNbt();
        if (!nbt.contains("destWorld") || !nbt.contains("destX") || !nbt.contains("destY") || !nbt.contains("destZ")) {
            return;
        }

        StringBuilder sb = new StringBuilder("§k");
//        StringBuilder sb = new StringBuilder();
        sb.append(nbt.getString("destWorld"));
        sb.append("\n");
        sb.append(nbt.getInt("destX"));
        sb.append(", ");
        sb.append(nbt.getInt("destY"));
        sb.append(", ");
        sb.append(nbt.getInt("destZ"));

        tooltip.add(Text.literal(sb.toString()));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getWorld().getBlockEntity(context.getBlockPos()) instanceof WarpstoneBlockEntity blockEntity)) {
            return ActionResult.PASS;
        }

        // TODO: Maybe allow changing destination?
        if (!blockEntity.isDestinationRandom() && blockEntity.hasDestination()) {
            return ActionResult.PASS;
        }

        ItemStack stack = context.getStack();
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {
            return ActionResult.PASS;
        }

        NbtCompound nbt = nbtComponent.copyNbt();
        if (!nbt.contains("destWorld") || !nbt.contains("destX") || !nbt.contains("destY") || !nbt.contains("destZ")) {
            return ActionResult.PASS;
        }

        if (!context.getWorld().isClient) {
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(nbt.getString("destWorld")));
            if (worldKey == null) {
                return ActionResult.PASS;
            }

            if (context.getWorld().getServer() == null) {
                return ActionResult.PASS;
            }

            ServerWorld serverWorld = context.getWorld().getServer().getWorld(worldKey);
            if (serverWorld == null) {
                return ActionResult.PASS;
            }

            blockEntity.setDestination(
                    worldKey,
                    nbt.getInt("destX"),
                    nbt.getInt("destY"),
                    nbt.getInt("destZ")
            );
        }

        stack.decrementUnlessCreative(1, context.getPlayer());
        return ActionResult.SUCCESS;
    }
}
