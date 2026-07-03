package snownee.fruits.mixin.crafter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import snownee.fruits.util.ClientProxy;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
	@WrapOperation(
			method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
			at = @At(
					value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
	private void translate(
			PoseStack instance,
			float xo,
			float yo,
			float zo,
			Operation<Void> original,
			@Local(argsOnly = true, name = "state") ItemEntityRenderState state) {
		if (state.getData(ClientProxy.NO_BOB) != null) {
			return;
		}
		original.call(instance, xo, yo, zo);
	}
}
