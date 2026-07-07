package snownee.fruits.mixin.ritual;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import snownee.fruits.Hooks;
import snownee.fruits.ritual.RitualModule;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@WrapOperation(method = "shouldShowName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hasCustomName()Z"))
	private boolean shouldShowName(Entity entity, Operation<Boolean> original) {
		if (Hooks.ritual && entity instanceof Interaction interaction && RitualModule.isFFInteractionEntity(interaction)) {
			return false;
		}
		return original.call(entity);
	}
}
