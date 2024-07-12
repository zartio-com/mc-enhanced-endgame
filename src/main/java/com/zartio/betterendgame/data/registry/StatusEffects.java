package com.zartio.betterendgame.data.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.status_effect.black_hole.BlackHoleStatusEffect;
import com.zartio.betterendgame.data.status_effect.displaced.DisplacedStatusEffect;

public class StatusEffects {
    public static final RegistryEntry<StatusEffect> DISPLACED
            = registerStatusEffect("displaced", new DisplacedStatusEffect());
    public static final RegistryEntry<StatusEffect> BLACK_HOLE
            = registerStatusEffect("black_hole", new BlackHoleStatusEffect());

    public static void init() {}

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(
                Registries.STATUS_EFFECT, Identifier.of(BetterEndgame.MOD_ID, name), statusEffect);
    }
}
