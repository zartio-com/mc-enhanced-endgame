package com.zartio.betterendgame.data.special_crafting.warp_rune;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import com.zartio.betterendgame.data.registry.Items;
import com.zartio.betterendgame.data.registry.Potions;
import com.zartio.betterendgame.data.registry.RecipeSerializers;

public class WarpRuneSpecialCraftingRecipe extends SpecialCraftingRecipe {
    public WarpRuneSpecialCraftingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        boolean hasPaper = false;
        boolean hasPotion = false;

        for (int i = 0; i < input.getHeight(); i++) {
            for (int j = 0; j < input.getWidth(); j++) {
                ItemStack itemStack = input.getStackInSlot(j, i);

                if (itemStack.isOf(net.minecraft.item.Items.PAPER)) {
                    if (hasPaper) {
                        return false;
                    }

                    hasPaper = true;
                } else if (itemStack.isOf(net.minecraft.item.Items.POTION)) {
                    PotionContentsComponent potionContents = itemStack.get(DataComponentTypes.POTION_CONTENTS);
                    if (potionContents == null) {
                        return false;
                    }

                    if (!potionContents.matches(Potions.CHORUS_ENERGY) && !potionContents.matches(Potions.CHORUS_ENERGY_1)) {
                        return false;
                    }

                    if (hasPotion) {
                        return false;
                    }

                    hasPotion = true;
                }
            }
        }

        return hasPaper && hasPotion;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return new ItemStack(Items.EMPTY_WARP_RUNE_ITEM, 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 || height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.WARP_RUNE;
    }
}
