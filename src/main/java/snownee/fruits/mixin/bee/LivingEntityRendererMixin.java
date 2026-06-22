package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@WrapOperation(
			method = "getRenderType",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/EntityModel;renderType(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/RenderType;"))
	private RenderType getRenderType(
			EntityModel instance,
			Identifier Identifier,
			Operation<RenderType> original,
			@Local(argsOnly = true) LivingEntity entity) {
		if (entity instanceof Bee bee && BeeAttributes.of(bee).hasTrait(Trait.GHOST)) {
			return RenderType.entityTranslucent(Identifier);
		}
		return original.call(instance, Identifier);
	}
}
