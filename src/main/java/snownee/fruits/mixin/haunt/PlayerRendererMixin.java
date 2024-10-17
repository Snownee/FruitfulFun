package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
	@Inject(
			method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At("HEAD"),
			cancellable = true)
	private void render(
			Entity entity,
			float entityYaw,
			float partialTick,
			PoseStack poseStack,
			MultiBufferSource buffer,
			int packedLight,
			CallbackInfo ci) {
		if (Hooks.bee && FFPlayer.of(entity).fruits$isHaunting()) {
			ci.cancel();
		}
	}
}
