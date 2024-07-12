package com.zartio.betterendgame.data.registry.client;

import com.zartio.betterendgame.data.entity.leeched_experience_orb.client.LeechedExperienceOrbEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import com.zartio.betterendgame.data.registry.EntityTypes;

public class EntityRenderer {
    static {
        EntityRendererRegistry.register(EntityTypes.LEECHED_EXPERIENCE_ORB, LeechedExperienceOrbEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.TELEPORTING_EYE_OF_ENDER, context -> new FlyingItemEntityRenderer<>(context, 1.0F, true));
    }

    public static void init() {}
}
