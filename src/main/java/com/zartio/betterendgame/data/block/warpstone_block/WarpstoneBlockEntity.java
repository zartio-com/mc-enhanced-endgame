package com.zartio.betterendgame.data.block.warpstone_block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.registry.BlockEntityTypes;
import com.zartio.betterendgame.data.registry.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WarpstoneBlockEntity extends BlockEntity {
    public final static int MAX_CHARGES = 10;

    private final Random random = Random.create();

    private boolean isDestinationRandom = true;

    private RegistryKey<World> destWorld;
    private int destX;
    private int destY;
    private int destZ;

    private int charge = 0;

    public WarpstoneBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.WARPSTONE_BLOCK_ENTITY, pos, state);
    }

    public RegistryKey<World> getDestWorld() {
        return destWorld;
    }

    @Nullable
    private BlockPos randomDestination() {
        if (getWorld() == null) {
            return null;
        }

        double size = getWorld().getWorldBorder().getSize() / (getWorld().getRegistryKey() == World.END ? 128 : 2);
        int x = random.nextBetween((int)-size, (int)size);
        int y = random.nextBetween(10, 100);
        int z = random.nextBetween((int)-size, (int)size);
        return new BlockPos(x, y, z);
    }

    public void randomizeDestination() {
        if (getWorld() == null) {
            return;
        }

        double size = getWorld().getWorldBorder().getSize() / 2;
        int x = random.nextBetween((int)-size, (int)size);
        int y = random.nextBetween(10, 100);
        int z = random.nextBetween((int)-size, (int)size);
        setDestination(getWorld().getRegistryKey(), x, y, z);
        this.isDestinationRandom = true;
        this.markDirty();
    }

    public boolean hasDestination() {
        return this.destWorld != null;
    }

    public void setDestination(RegistryKey<World> world, int x, int y, int z) {
        destWorld = world;
        destX = x;
        destY = y;
        destZ = z;
        this.isDestinationRandom = false;
        this.markDirty();
    }

    public void setDestination(RegistryKey<World> world, int[] destination) {
        setDestination(world, destination[0], destination[1], destination[2]);
    }

    public void setDestination(RegistryKey<World> world, BlockPos pos) {
        this.setDestination(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public void setDestination(RegistryKey<World> world, Vec3d pos) {
        this.setDestination(world, (int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
    }

    public boolean isDestinationRandom() {
        return isDestinationRandom;
    }

    public BlockPos getDestination() {
        return new BlockPos(destX, destY, destZ);
    }

    public Vec3d getDestinationPos() {
        return new Vec3d(destX, destY, destZ);
    }

    public int getCharge() {
        return this.charge;
    }

    public void addCharge(int charge) {
        this.charge += charge;
        if (this.charge > MAX_CHARGES) {
            this.charge = MAX_CHARGES;
        }

        if (this.charge > 0) {
            Objects.requireNonNull(this.getWorld())
                    .setBlockState(
                            this.getPos(),
                            Blocks.WARPSTONE_BLOCK.getDefaultState().with(WarpstoneBlock.CHARGED, true)
                    );
        }

        this.markDirty();
    }

    public void removeCharge(int charge) {
        this.charge -= charge;
        if (this.charge < 0) {
            this.charge = 0;
        }

        if (this.charge == 0) {
            Objects.requireNonNull(this.getWorld())
                    .setBlockState(
                            this.getPos(),
                            Blocks.WARPSTONE_BLOCK.getDefaultState().with(WarpstoneBlock.CHARGED, false)
                    );
        }

        this.markDirty();
    }

    public boolean isCharged() {
        return this.charge > 0;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("charge", this.charge);

        nbt.putString("destWorld", this.destWorld.getValue().toString());
        nbt.putInt("destX", this.destX);
        nbt.putInt("destY", this.destY);
        nbt.putInt("destZ", this.destZ);
        nbt.putBoolean("isDestinationRandom", this.isDestinationRandom);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        this.charge = nbt.getInt("charge");

        String destWorld = nbt.getString("destWorld");
        if (destWorld != null) {
            this.destWorld = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(nbt.getString("destWorld")));
        }

        this.destX = nbt.getInt("destX");
        this.destY = nbt.getInt("destY");
        this.destZ = nbt.getInt("destZ");
        this.isDestinationRandom = nbt.getBoolean("isDestinationRandom");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public void markDirty() {
        if (getWorld() == null) {
            super.markDirty();
        }

        BlockState state = getWorld().getBlockState(getPos());
        getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        super.markDirty();
    }
}
