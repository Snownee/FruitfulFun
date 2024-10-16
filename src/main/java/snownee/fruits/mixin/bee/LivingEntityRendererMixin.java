package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@WrapOperation(
			method = "getRenderType",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/EntityModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
	private RenderType getRenderType(
			EntityModel instance,
			ResourceLocation resourceLocation,
			Operation<RenderType> original,
			@Local(argsOnly = true) LivingEntity entity) {
		if (entity instanceof Bee bee && BeeAttributes.of(bee).hasTrait(Trait.GHOST)) {
			return RenderType.entityTranslucent(resourceLocation);
		}
		return original.call(instance, resourceLocation);
	}
}
