package com.zartio.betterendgame.data.status_effect.black_hole;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public class BlackHoleStatusEffect extends StatusEffect {
    private final static int RADIUS = 32;

    public BlackHoleStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x000000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        List<Entity> entities = entity.getEntityWorld().getOtherEntities(
                entity,
                entity.getBoundingBox().expand(RADIUS * (amplifier + 1)),
                e -> !(e instanceof PlayerEntity) && e instanceof LivingEntity);

        for (Entity e : entities) {
            if (e.getPos().isInRange(entity.getPos(), 4)) {
                continue;
            }

            ((LivingEntity) e).teleport(
                entity.getBlockPos().getX() + e.getRandom().nextBetween(-2, 2),
                entity.getBlockPos().getY() + e.getRandom().nextBetween(-2, 2),
                entity.getBlockPos().getZ() + e.getRandom().nextBetween(-2, 2),
                true
            );
            entity.getEntityWorld().playSound(
                    null, entity.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL);

            break;
        }

        return true;
    }
}
