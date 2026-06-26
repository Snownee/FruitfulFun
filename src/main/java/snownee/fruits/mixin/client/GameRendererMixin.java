package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Final
	@Shadow
	private Minecraft minecraft;

	@WrapOperation(
			method = "renderItemInHand", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;"))
	private GameType renderItemInHand(MultiPlayerGameMode gameMode, Operation<GameType> original) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			return GameType.SPECTATOR;
		}
		return original.call(gameMode);
	}

	@WrapOperation(
			method = "shouldRenderBlockOutline", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
	private Entity shouldRenderBlockOutline(Minecraft instance, Operation<Entity> original) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			return minecraft.player;
		}
		return original.call(minecraft);
	}
}
