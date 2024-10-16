package snownee.fruits.mixin.client;

import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.mixin.EntityAccess;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Final
	@Shadow
	Minecraft minecraft;

	@Inject(at = @At("RETURN"), method = "pick")
	private void pick(float partialTicks, CallbackInfo cir) {
		Hooks.modifyRayTraceResult(minecraft.hitResult, $ -> minecraft.hitResult = $);
	}

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

	@WrapOperation(
			method = "pick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
	private HitResult pickBlock(Entity entity, double hitDistance, float partialTicks, boolean hitFluids, Operation<HitResult> original) {
		LocalPlayer localPlayer = minecraft.player;
		if (Hooks.bee && localPlayer instanceof FFPlayer player && player.fruits$hauntingTarget() == entity) {
			Vec3 eyePosition = entity.getEyePosition(partialTicks);
			Vec3 viewVector = calculateViewVector(entity, partialTicks);
			Vec3 end = eyePosition.add(viewVector.x * hitDistance, viewVector.y * hitDistance, viewVector.z * hitDistance);
			return entity.level().clip(new ClipContext(
					eyePosition,
					end,
					ClipContext.Block.OUTLINE,
					hitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
					entity));
		}
		return original.call(entity, hitDistance, partialTicks, hitFluids);
	}

	@WrapOperation(
			method = "pick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"))
	private Vec3 pickEntity(Entity entity, float partialTicks, Operation<Vec3> original) {
		if (minecraft.player instanceof FFPlayer player && player.fruits$hauntingTarget() == entity) {
			return calculateViewVector(entity, partialTicks);
		}
		return original.call(entity, partialTicks);
	}

	@Unique
	private static Vec3 calculateViewVector(Entity entity, float partialTicks) {
		LocalPlayer localPlayer = Objects.requireNonNull(Minecraft.getInstance().player);
		return ((EntityAccess) entity).callCalculateViewVector(
				entity.getViewXRot(partialTicks) + localPlayer.getViewXRot(partialTicks),
				entity.getViewYRot(partialTicks) + localPlayer.getViewYRot(partialTicks));
	}
}
