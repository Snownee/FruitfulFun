package snownee.fruits.mixin.brewer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBrewingStand;
import snownee.fruits.gadget.GadgetModule;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends AbstractContainerScreen<BrewingStandMenu> {
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
}
