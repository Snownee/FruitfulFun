package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
	@Inject(
			method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
			at = @At("HEAD"))
	private void renderItemDecorationsMixin(
			Font font,
			ItemStack stack,
			int x,
			int y,
			String text,
			CallbackInfo ci,
			@Local(argsOnly = true) LocalRef<String> textRef) {
		if (stack.is(Items.EMERALD) && text == null && stack.getTag() != null && stack.getTag().getBoolean("FFTrade")) {
			textRef.set("?");
		}
	}
}
