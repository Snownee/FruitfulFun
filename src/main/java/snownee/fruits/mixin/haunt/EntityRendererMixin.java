package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void shouldRender(Entity entity, Frustum culler, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && entity instanceof FFPlayer player && player.fruits$isHaunting()) {
			cir.cancel();
		}
	}
}
