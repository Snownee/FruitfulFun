package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(Gui.class)
public class GuiMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(
			method = "render", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;"))
	private GameType getPlayerMode(MultiPlayerGameMode gameMode, Operation<GameType> original) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			return GameType.SPECTATOR;
		}
		return original.call(gameMode);
	}

	@WrapOperation(
			method = "render",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"))
	private boolean hasExperience(MultiPlayerGameMode gameMode, Operation<Boolean> original) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			return false;
		}
		return original.call(gameMode);
	}
}
