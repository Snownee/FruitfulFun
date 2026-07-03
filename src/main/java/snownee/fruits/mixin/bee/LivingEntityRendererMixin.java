package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import snownee.fruits.util.ClientProxy;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@WrapOperation(
			method = "getRenderType",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/EntityModel;renderType(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
	private RenderType getRenderType(
			EntityModel<?> instance,
			Identifier texture,
			Operation<RenderType> original,
			@Local(argsOnly = true, name = "state") LivingEntityRenderState state) {
		if (state instanceof BeeRenderState && state.getData(ClientProxy.RENDER_TYPE) != null) {
			return RenderTypes.entityTranslucent(texture);
		}
		return original.call(instance, texture);
	}
}
