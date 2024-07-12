package com.zartio.betterendgame.data.entity.leeched_experience_orb;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import com.zartio.betterendgame.data.registry.EntityTypes;

public class LeechedExperienceOrbEntity extends Entity {
    private int orbAge = 0;

    public LeechedExperienceOrbEntity(EntityType<LeechedExperienceOrbEntity> type, World world) {
        super(type, world);
    }

    public LeechedExperienceOrbEntity(World world, double x, double y, double z) {
        this(EntityTypes.LEECHED_EXPERIENCE_ORB, world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        this.baseTick();

        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());

        float f = 0.98F;
        if (this.isOnGround()) {
            f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.98F;
        }

        this.setVelocity(this.getVelocity().multiply(f, 0.98, f));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(1.0, -0.9, 1.0));
        }

        this.orbAge++;
        if (this.orbAge >= 60) {
            this.discard();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        return;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }
}
