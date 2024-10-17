package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.CoreModule;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.duck.FFPlayer;
import snownee.kiwi.loader.Platform;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Inject(
			method = "attack", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/Player;attack(Lnet/minecraft/world/entity/Entity;)V"), cancellable = true)
	private void attack(Entity target, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (Hooks.bee && target instanceof LivingEntity && !target.getType().is(BeeModule.CANNOT_HAUNT) &&
				CoreModule.ORANGE.is(player.getMainHandItem())) {
			if (!Platform.isProduction()) {
				FFPlayer.of(player).fruits$setHauntingTarget(target);
				ci.cancel();
			}
		}
	}

	@Inject(method = "setCamera", at = @At("TAIL"))
	private void setCamera(Entity target, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
//		Hooks.debugInChat(player, "setCamera to %s".formatted(target == null ? "null" : target.getName().getString()));
		if (Hooks.bee && (target == null || target == player) && FFPlayer.of(player).fruits$isHaunting()) {
//			Hooks.debugInChat(player, "setCamera 2 to %s".formatted(target == null ? "null" : target.getName().getString()));
			FFPlayer.of(this).fruits$setHauntingTarget(player);
		}
	}

	@Inject(method = "changeDimension", at = @At("RETURN"))
	private void changeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
		BeeModule.changeDimension(destination, (ServerPlayer) (Object) this, cir.getReturnValue());
	}

	@Inject(method = "hasChangedDimension", at = @At("HEAD"))
	private void hasChangedDimension(CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			FFPlayer.of(player).fruits$ensureCamera();
		}
	}

	@WrapOperation(
			method = "setPlayerInput",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isPassenger()Z"))
	private boolean setPlayerInput(ServerPlayer player, Operation<Boolean> original) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			return true;
		}
		return original.call(player);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		HauntingManager manager = FFPlayer.of(player).fruits$hauntingManager();
		if (Hooks.bee && manager != null) {
			manager.tick(player);
		}
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"))
	private void tick(ServerPlayer player, double x, double y, double z, float yRot, float xRot, Operation<Void> original) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			yRot = player.getYRot();
			xRot = player.getXRot();
		}
		original.call(player, x, y, z, yRot, xRot);
	}
}
