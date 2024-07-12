package com.zartio.betterendgame.mixin.worldgen;

import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructurePlacementData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleStructurePiece.class)
public class SimpleStructurePieceAccessorMixin {
    @Shadow
    protected StructurePlacementData placementData;
}