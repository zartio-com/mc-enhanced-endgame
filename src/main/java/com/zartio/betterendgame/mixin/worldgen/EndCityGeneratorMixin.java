package com.zartio.betterendgame.mixin.worldgen;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.zartio.betterendgame.data.registry.Items;

@Mixin(EndCityGenerator.Piece.class)
public class EndCityGeneratorMixin extends SimpleStructurePieceAccessorMixin {



	@Inject(at = @At("HEAD"), method = "handleMetadata", cancellable = true)
	private void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox, CallbackInfo info) {
		if (metadata.startsWith("Elytra")) {
			info.cancel();
		} else {
			return;
		}

//		SimpleStructurePieceAccessor selfAsSSP = (SimpleStructurePieceAccessor) (Object) this;

		if (boundingBox.contains(pos) && World.isValid(pos)) {
			ItemFrameEntity itemFrameEntity = new ItemFrameEntity(world.toServerWorld(), pos, this.placementData.getRotation().rotate(Direction.SOUTH));
			itemFrameEntity.setHeldItemStack(new ItemStack(Items.UNSTABLE_MATTER_ITEM), false);
			world.spawnEntity(itemFrameEntity);
		}
	}
}