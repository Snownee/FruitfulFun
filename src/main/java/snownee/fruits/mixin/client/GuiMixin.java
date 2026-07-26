package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(Gui.class)
public class GuiMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
	private void extractSelectedItemName(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			ci.cancel();
		}
	}
}
