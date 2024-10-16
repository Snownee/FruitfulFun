package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.player.LocalPlayer;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@Inject(method = "drop", at = @At("HEAD"), cancellable = true)
	private void drop(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && this instanceof FFPlayer player && player.fruits$isHaunting()) {
			cir.setReturnValue(false);
		}
	}
}
