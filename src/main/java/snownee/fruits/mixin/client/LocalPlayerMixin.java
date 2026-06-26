package snownee.fruits.mixin.client;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
	public LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
		super(level, gameProfile);
	}

	@WrapOperation(
			method = "pick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
	private static HitResult pickBlock(
			Entity entity,
			double range,
			float a,
			boolean withLiquids,
			Operation<HitResult> original) {
		if (BeeModule.isHauntingNormalEntity(Minecraft.getInstance().player, entity)) {
			Vec3 eyePosition = entity.getEyePosition(a);
			Vec3 viewVector = Hooks.calculateViewVector(entity, Objects.requireNonNull(Minecraft.getInstance().player), a);
			Vec3 end = eyePosition.add(viewVector.x * range, viewVector.y * range, viewVector.z * range);
			return entity.level().clip(new ClipContext(
					eyePosition,
					end,
					ClipContext.Block.OUTLINE,
					withLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
					entity));
		}
		return original.call(entity, range, a, withLiquids);
	}

	@WrapOperation(
			method = "pick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"))
	private static Vec3 pickEntity(Entity entity, float a, Operation<Vec3> original) {
		if (BeeModule.isHauntingNormalEntity(Minecraft.getInstance().player, entity)) {
			return Hooks.calculateViewVector(entity, Objects.requireNonNull(Minecraft.getInstance().player), a);
		}
		return original.call(entity, a);
	}
}
