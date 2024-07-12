package com.zartio.betterendgame.mixin.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.entity.teleporting_eye_of_ender.TeleportingEyeOfEnderEntity;

@Mixin(EnderEyeItem.class)
public abstract class EyeOfEnderItemMixin {

	@Inject(at = @At("RETURN"), method = "use", cancellable = true)
	private void init(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		assert world.getDimensionEntry().getKey().isPresent();

		if (world.getDimensionEntry().getKey().get() != DimensionTypes.THE_END) {
			return;
		}

		EnderEyeItem self = (EnderEyeItem) (Object) this;

		Vec3d scaledLookVector = user.getRotationVector().multiply(300);
		BlockPos blockPos = user.getBlockPos().add((int)scaledLookVector.x, user.getBlockY(), (int)scaledLookVector.z);

		ItemStack itemStack = user.getStackInHand(hand);
		TeleportingEyeOfEnderEntity eyeOfEnderEntity = new TeleportingEyeOfEnderEntity(world, user.getX(), user.getBodyY(0.5), user.getZ());
		eyeOfEnderEntity.setItem(itemStack);
		eyeOfEnderEntity.initTargetPos(blockPos);
		eyeOfEnderEntity.setOwner(user);
		world.emitGameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfEnderEntity.getPos(), GameEvent.Emitter.of(user));
		world.spawnEntity(eyeOfEnderEntity);

		float f = MathHelper.lerp(world.random.nextFloat(), 0.33F, 0.5F);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, f);
		itemStack.decrementUnlessCreative(1, user);
		user.incrementStat(Stats.USED.getOrCreateStat(self));
		user.swingHand(hand, true);

		cir.setReturnValue(TypedActionResult.success(itemStack));
	}
}