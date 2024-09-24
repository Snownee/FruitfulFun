package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(
			method = {"performUseItemOn", "useItem", "attack", "interact", "interactAt"}, at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;localPlayerMode:Lnet/minecraft/world/level/GameType;"))
	private GameType performUseItemOn(MultiPlayerGameMode instance, Operation<GameType> original) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			return GameType.SPECTATOR;
		}
		return original.call(instance);
	}

	@Inject(method = "isServerControlledInventory", at = @At("HEAD"), cancellable = true)
	private void isServerControlledInventory(CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			cir.setReturnValue(true);
		}
	}
}
