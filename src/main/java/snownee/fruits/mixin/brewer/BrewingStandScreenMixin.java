package snownee.fruits.mixin.brewer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBrewingStand;
import snownee.fruits.duck.FFBrewingStandMenu;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.util.Color;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends AbstractContainerScreen<BrewingStandMenu> {
	@Unique
	private static final ResourceLocation TRANSPARENT_BREW_PROGRESS = FruitfulFun.id("textures/gui/sprites/brew_progress.png");

	protected BrewingStandScreenMixin(BrewingStandMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@WrapOperation(
			method = "renderBg", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 2))
	private void renderSpeedBonus(
			GuiGraphics instance,
			ResourceLocation atlasLocation,
			int x,
			int y,
			int uOffset,
			int vOffset,
			int uWidth,
			int vHeight,
			Operation<Void> original,
			@Local(argsOnly = true) GuiGraphics graphics) {
		if (!Hooks.gadget || menu.getType() != GadgetModule.BREWER_MENU.get()) {
			original.call(instance, atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
			return;
		}
		ContainerData containerData = ((FFBrewingStandMenu) menu).fruits$dataAccess();
		double bonus = FFBrewingStand.calculateBrewSpeedBonus(containerData.get(containerData.getCount() - 1));
		Color color = Color.hsl(268, 0.5, 1 - bonus * 0.3);
		graphics.setColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1);
		graphics.blit(TRANSPARENT_BREW_PROGRESS, x - 1, y - 1, 0, 0, uWidth, vHeight, 9, 28);
		graphics.setColor(1, 1, 1, 1);
	}

	@Inject(method = "renderBg", at = @At("HEAD"))
	private void showTooltip(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
		if (!Hooks.gadget || menu.getType() != GadgetModule.BREWER_MENU.get()) {
			return;
		}
		int x = leftPos + 63;
		int y = topPos + 14;
		if (mouseX >= x && mouseX < x + 12 && mouseY >= y && mouseY < y + 29) {
			ContainerData containerData = ((FFBrewingStandMenu) menu).fruits$dataAccess();
			double bonus = FFBrewingStand.calculateBrewSpeedBonus(containerData.get(containerData.getCount() - 1));
			graphics.renderTooltip(font, Component.translatable("tip.fruitfulfun.speedBonus", (int) (bonus * 100)), mouseX, mouseY);
		}
	}
}
