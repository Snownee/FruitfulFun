package snownee.fruits.mixin.forge;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;

@Mixin(Gui.class)
public class GuiMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(
			method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
			at = @At("HEAD"),
			cancellable = true,
			remap = false)
	private void renderSelectedItemName(GuiGraphics guiGraphics, int yShift, CallbackInfo ci) {
		if (Hooks.bee && minecraft.player instanceof FFPlayer player && player.fruits$isHaunting()) {
			ci.cancel();
		}
	}
}
