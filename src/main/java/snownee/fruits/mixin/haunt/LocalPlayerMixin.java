package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@Inject(method = "drop", at = @At("HEAD"), cancellable = true)
	private void drop(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && this instanceof FFPlayer player && player.fruits$isHaunting()) {
			cir.setReturnValue(false);
		}
	}

	@WrapOperation(
			method = {"sendPosition", "serverAiStep"},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
	private boolean isControlledCamera(LocalPlayer player, Operation<Boolean> original) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			return true;
		}
		return original.call(player);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z"))
	private boolean isPassenger(LocalPlayer player, Operation<Boolean> original) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			return true;
		}
		return original.call(player);
	}

	@WrapOperation(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
	private Entity getRootVehicle(LocalPlayer player, Operation<Entity> original) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			return FFPlayer.of(player).fruits$hauntingTarget();
		}
		return original.call(player);
	}
}
