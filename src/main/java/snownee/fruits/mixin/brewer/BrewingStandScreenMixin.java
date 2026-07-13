package snownee.fruits.mixin.brewer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBrewingStand;
import snownee.fruits.gadget.GadgetModule;
import snownee.kiwi.util.Color;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends AbstractContainerScreen<BrewingStandMenu> {
	@Unique
	private static final Identifier TRANSPARENT_BREW_PROGRESS = FruitfulFun.id("brew_progress");

	public BrewingStandScreenMixin(BrewingStandMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@ModifyExpressionValue(method = "extractBackground", at = @At(value = "CONSTANT", args = "floatValue=400.0F"))
	private float extractBackground(
			float original,
			@Local(name = "graphics", argsOnly = true) GuiGraphicsExtractor graphics,
			@Local(name = "mouseX", argsOnly = true) int mouseX,
			@Local(name = "mouseY", argsOnly = true) int mouseY,
			@Local(name = "xo") int xo,
			@Local(name = "yo") int yo) {
		if (Hooks.gadget && menu.menuType != null && GadgetModule.BREWER_MENU.is(menu.menuType)) {
			double bonus = FFBrewingStand.calculateBrewSpeedBonus(menu.brewingStandData.get(menu.brewingStandData.getCount() - 1));
			double newSpeed = 1 + bonus;
			original = (int) (original / newSpeed);
			xo += 63;
			yo += 14;
			if (mouseX >= xo && mouseX < xo + 12 && mouseY >= yo && mouseY < yo + 29) {
				graphics.setTooltipForNextFrame(Component.translatable("tip.fruitfulfun.speedBonus", (int) (100 * bonus)), mouseX, mouseY);
			}
		}
		return original;
	}

	@WrapOperation(
			method = "extractBackground",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V",
					ordinal = 1))
	private void changeArrowColor(
			GuiGraphicsExtractor instance,
			RenderPipeline renderPipeline,
			Identifier location,
			int spriteWidth,
			int spriteHeight,
			int textureX,
			int textureY,
			int x,
			int y,
			int width,
			int height,
			Operation<Void> original,
			@Local(name = "graphics", argsOnly = true) GuiGraphicsExtractor graphics) {
		if (Hooks.gadget && menu.menuType != null && GadgetModule.BREWER_MENU.is(menu.menuType)) {
			double bonus = FFBrewingStand.calculateBrewSpeedBonus(menu.brewingStandData.get(menu.brewingStandData.getCount() - 1));
			Color color = Color.hsl(268, 0.5, 1 - bonus * 0.3);
			graphics.blitSprite(
					renderPipeline,
					TRANSPARENT_BREW_PROGRESS,
					spriteWidth,
					spriteHeight,
					textureX,
					textureY,
					x,
					y,
					width,
					height,
					color.toInt());
		} else {
			original.call(instance, renderPipeline, location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height);
		}
	}
}
