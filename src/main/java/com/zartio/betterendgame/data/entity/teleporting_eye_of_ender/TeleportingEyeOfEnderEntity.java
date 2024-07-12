package com.zartio.betterendgame.data.entity.teleporting_eye_of_ender;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import com.zartio.betterendgame.BetterEndgame;

public class TeleportingEyeOfEnderEntity extends EyeOfEnderEntity {
    private double targetX;
    private double targetY;
    private double targetZ;
    private int lifespan;
    private boolean dropsItem;

    private LivingEntity owner;

    private boolean flownAboveVoid = false;

    public TeleportingEyeOfEnderEntity(EntityType<? extends EyeOfEnderEntity> entityType, World world) {
        super(entityType, world);
    }

    public TeleportingEyeOfEnderEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    private LivingEntity getOwner() {
        return owner;
    }

    /**
     * Sets where the eye will fly towards.
     * If close enough, it will fly directly towards it, otherwise, it will fly upwards, in the direction of the BlockPos.
     *
     * @param pos the block the eye of ender is drawn towards
     */
    public void initTargetPos(BlockPos pos) {
        double d = (double)pos.getX();
        int i = pos.getY();
        double e = (double)pos.getZ();
        this.targetX = d;
        this.targetY = (double)i;
        this.targetZ = e;

        this.lifespan = 0;
        this.dropsItem = this.random.nextInt(5) > 0;
    }

    @Override
    public void tick() {
//        super.tick();
        super.baseTick();
        Vec3d velocity = this.getVelocity();
        double newX = this.getX() + velocity.x;
        double newY = this.getY() + velocity.y;
        double newZ = this.getZ() + velocity.z;
        double speed = velocity.horizontalLength();

        this.setPitch(TeleportingEyeOfEnderEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(velocity.y, speed) * 180.0F / (float)Math.PI)));
        this.setYaw(TeleportingEyeOfEnderEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(velocity.x, velocity.z) * 180.0F / (float)Math.PI)));
        if (!this.getWorld().isClient) {
            double h = this.targetX - newX;
            double i = this.targetZ - newZ;
            float j = (float)Math.sqrt(h * h + i * i);
            float k = (float)MathHelper.atan2(i, h);
            double l = MathHelper.lerp(0.0005, speed, (double)j);
            double m = velocity.y;
            if (j < 1.0F) {
                l *= 0.8;
                m *= 0.8;
            }

            int n = this.getY() < this.targetY ? 1 : -1;
            velocity = new Vec3d(Math.cos((double)k) * l, m + ((double)n - m) * 0.015F, Math.sin((double)k) * l);
            this.setVelocity(velocity);
        }

        float o = 0.25F;
        if (this.isTouchingWater()) {
            for (int p = 0; p < 4; p++) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, newX - velocity.x * 0.25, newY - velocity.y * 0.25, newZ - velocity.z * 0.25, velocity.x, velocity.y, velocity.z);
            }
        } else {
            this.getWorld()
                    .addParticle(
                            ParticleTypes.PORTAL,
                            newX - velocity.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
                            newY - velocity.y * 0.25 - 0.5,
                            newZ - velocity.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
                            velocity.x,
                            velocity.y,
                            velocity.z
                    );
        }

        if (!this.getWorld().isClient) {
            this.setPosition(newX, newY, newZ);

            // Check for ground below
            BlockPos topPos = this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos((int)newX, (int)newY, (int)newZ));
            if (topPos.getY() == 0 && !flownAboveVoid) {
                flownAboveVoid = true;
            }

            this.lifespan++;
            if (this.lifespan > 80 || (topPos.getY() != 0 && flownAboveVoid)) {
                if (topPos.getY() != 0 && flownAboveVoid && owner != null) {
                    owner.teleport(topPos.getX(), topPos.getY() + 3, topPos.getZ(), true);
                }

                this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.discard();
                if (this.dropsItem) {
                    this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.getStack()));
                } else {
                    this.getWorld().syncWorldEvent(WorldEvents.EYE_OF_ENDER_BREAKS, this.getBlockPos(), 0);
                }
            }
        } else {
            this.setPos(newX, newY, newZ);
        }
    }

    protected static float updateRotation(float prevRot, float newRot) {
        while (newRot - prevRot < -180.0F) {
            prevRot -= 360.0F;
        }

        while (newRot - prevRot >= 180.0F) {
            prevRot += 360.0F;
        }

        return MathHelper.lerp(0.2F, prevRot, newRot);
    }
}
