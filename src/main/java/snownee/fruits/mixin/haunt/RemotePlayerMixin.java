package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.RemotePlayer;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(RemotePlayer.class)
public class RemotePlayerMixin {
	@Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
	private void aiStep(CallbackInfo ci) {
		if (Hooks.bee && ((FFPlayer) this).fruits$isHaunting()) {
			ci.cancel();
		}
	}
}
