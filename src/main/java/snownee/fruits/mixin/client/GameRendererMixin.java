package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import snownee.fruits.Hooks;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Final
	@Shadow
	Minecraft minecraft;

	@Inject(at = @At("RETURN"), method = "pick")
	private void pick(float partialTicks, CallbackInfo cir) {
		Hooks.modifyRayTraceResult(minecraft.hitResult, $ -> minecraft.hitResult = $);
	}
}
