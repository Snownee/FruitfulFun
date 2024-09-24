package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.CoreModule;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
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
				((FFPlayer) player).fruits$setHauntingTarget(target);
				ci.cancel();
			}
		}
	}

	@Inject(method = "setCamera", at = @At("HEAD"))
	private void setCamera(Entity target, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (Hooks.bee && (target == null || target == player) && ((FFPlayer) player).fruits$isHaunting()) {
			((FFPlayer) this).fruits$setHauntingTarget(player);
		}
	}

	@Inject(method = "changeDimension", at = @At("RETURN"))
	private void changeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
		BeeModule.changeDimension(destination, (ServerPlayer) (Object) this, cir.getReturnValue());
	}

	@Inject(method = "hasChangedDimension", at = @At("HEAD"))
	private void hasChangedDimension(CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (Hooks.bee && ((FFPlayer) player).fruits$isHaunting()) {
			((FFPlayer) player).fruits$ensureCamera();
		}
	}
}
