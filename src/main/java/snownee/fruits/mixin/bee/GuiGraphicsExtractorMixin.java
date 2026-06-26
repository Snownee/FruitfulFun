package snownee.fruits.mixin.bee;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.bee.BeeModule;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {
	@Inject(
			method = "itemCount", at = @At("HEAD"))
	private void itemCount(
			Font font,
			ItemStack stack,
			int x,
			int y,
			@Nullable String text,
			CallbackInfo ci,
			@Local(argsOnly = true) LocalRef<String> textRef) {
		if (stack.is(Items.EMERALD) && text == null && stack.has(BeeModule.MERCHANT_OFFER.get())) {
			textRef.set("?");
		}
	}
}
