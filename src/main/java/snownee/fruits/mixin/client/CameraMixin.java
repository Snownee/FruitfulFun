package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(Camera.class)
public class CameraMixin {
	@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F"))
	private float getViewXRot(Entity instance, float partialTicks, Operation<Float> original) {
		float value = original.call(instance, partialTicks);
		if (Hooks.bee && Minecraft.getInstance().player instanceof FFPlayer player && player.fruits$hauntingTarget() == instance) {
			value += Minecraft.getInstance().player.getViewXRot(partialTicks);
		}
		return value;
	}

	@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F"))
	private float getViewYRot(Entity instance, float partialTicks, Operation<Float> original) {
		float value = original.call(instance, partialTicks);
		if (Hooks.bee && Minecraft.getInstance().player instanceof FFPlayer player && player.fruits$hauntingTarget() == instance) {
			value += Minecraft.getInstance().player.getViewYRot(partialTicks);
		}
		return value;
	}
}
