package snownee.fruits.mixin.client;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import snownee.fruits.bee.BeeModule;

@Mixin(Camera.class)
public class CameraMixin {
	@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F"))
	private float getViewXRot(Entity entity, float partialTicks, Operation<Float> original) {
		float value = original.call(entity, partialTicks);
		LocalPlayer localPlayer = Minecraft.getInstance().player;
		if (BeeModule.isHauntingNormalEntity(localPlayer, entity)) {
			value = Mth.clamp(value + Objects.requireNonNull(localPlayer).getViewXRot(partialTicks), -90F, 90F);
		}
		return value;
	}

	@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F"))
	private float getViewYRot(Entity entity, float partialTicks, Operation<Float> original) {
		float value = original.call(entity, partialTicks);
		LocalPlayer localPlayer = Minecraft.getInstance().player;
		if (BeeModule.isHauntingNormalEntity(localPlayer, entity)) {
			value += Objects.requireNonNull(localPlayer).getViewYRot(partialTicks);
		}
		return value;
	}
}
