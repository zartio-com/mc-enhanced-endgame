package com.zartio.betterendgame.data.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.entity.leeched_experience_orb.LeechedExperienceOrbEntity;
import com.zartio.betterendgame.data.entity.teleporting_eye_of_ender.TeleportingEyeOfEnderEntity;

public class EntityTypes {
    public static final EntityType<LeechedExperienceOrbEntity> LEECHED_EXPERIENCE_ORB
            = registerEntity("leeched_experience_orb", LeechedExperienceOrbEntity::new);

    public static final EntityType<TeleportingEyeOfEnderEntity> TELEPORTING_EYE_OF_ENDER
            = registerEntity("teleporting_eye_of_ender", TeleportingEyeOfEnderEntity::new);

    public static void init() {}

    private static <T extends Entity> EntityType<T> registerEntity(
            String name,
            EntityType.EntityFactory<T> factory
    ) {
        return Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(BetterEndgame.MOD_ID, name),
                EntityType.Builder.create(factory, SpawnGroup.MISC).dimensions(0.2f, 0.2f).build()
        );
    }
}
