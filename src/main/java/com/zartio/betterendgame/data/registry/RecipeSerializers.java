package com.zartio.betterendgame.data.registry;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.special_crafting.warp_rune.WarpRuneSpecialCraftingRecipe;

public class RecipeSerializers {
    public static final RecipeSerializer<WarpRuneSpecialCraftingRecipe> WARP_RUNE
            = register("crafting_special_warprune", WarpRuneSpecialCraftingRecipe::new);

    public static void init() {}

    private static <T extends SpecialCraftingRecipe> RecipeSerializer<T> register(
            String name,
            SpecialRecipeSerializer.Factory<T> factory
    ) {
        return Registry.register(
                Registries.RECIPE_SERIALIZER,
                Identifier.of(BetterEndgame.MOD_ID, name),
                new SpecialRecipeSerializer<>(factory)
        );
    }
}
