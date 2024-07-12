package com.zartio.betterendgame.data.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import com.zartio.betterendgame.BetterEndgame;

public class Potions {
    public static final RegistryEntry<Potion> CHORUS_ESSENCE
            = registerPotion("chorus_essence", StatusEffects.DISPLACED, 6 * 20, 0);
    public static RegistryEntry<Potion> CHORUS_ESSENCE_1
            = registerPotion("chorus_essence_1", StatusEffects.DISPLACED, 6 * 20, 1);
    public static RegistryEntry<Potion> CHORUS_ENERGY
            = registerPotion("chorus_energy", StatusEffects.BLACK_HOLE, 6 * 20, 0);
    public static RegistryEntry<Potion> CHORUS_ENERGY_1
            = registerPotion("chorus_energy_1", StatusEffects.BLACK_HOLE, 12 * 20, 1);

    public static void init() {
        registerPotionRecipes();
    }

    public static void registerPotionRecipes() {
        BrewingRecipeRegistry.Builder.BUILD.register(b -> {
            b.registerRecipes(Items.CHORUS_FRUIT, CHORUS_ESSENCE);
            b.registerPotionRecipe(CHORUS_ESSENCE, Items.GLOWSTONE_DUST, CHORUS_ESSENCE_1);
            b.registerPotionRecipe(CHORUS_ESSENCE, Items.ENDER_PEARL, CHORUS_ENERGY);
            b.registerPotionRecipe(CHORUS_ENERGY, Items.GLOWSTONE_DUST, CHORUS_ENERGY_1);
        });
    }

    private static RegistryEntry<Potion> registerPotion(
            String name,
            RegistryEntry<StatusEffect> effectRegistryEntry,
            int duration,
            int amplifier
    ) {
        return Registry.registerReference(
                Registries.POTION,
                Identifier.of(BetterEndgame.MOD_ID, name),
                new Potion(new StatusEffectInstance(effectRegistryEntry, duration, amplifier)));
    }
}
