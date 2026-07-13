package snownee.fruits.mixin.brewer;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import snownee.fruits.duck.FFBrewingStand;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.brewer.RemoteBrewerContainerData;

@Mixin(BrewingStandMenu.class)
public abstract class BrewingStandMenuMixin extends AbstractContainerMenu {
	@Mutable
	@Shadow
	@Final
	public ContainerData brewingStandData;

	protected BrewingStandMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
		super(menuType, containerId);
	}

	@Inject(
			method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",
			at = @At("RETURN"))
	private void init(int containerId, Inventory inventory, Container brewingStand, ContainerData brewingStandData, CallbackInfo ci) {
		if (brewingStand instanceof FFBrewingStand brewer && brewer.fruits$isBrewer()) {
			addDataSlot(DataSlot.forContainer(brewer.fruits$dataAccess(), brewer.fruits$dataAccess().getCount() - 1));
			this.brewingStandData = brewer.fruits$dataAccess();
			menuType = GadgetModule.BREWER_MENU.get();
		} else if (brewingStandData instanceof RemoteBrewerContainerData) {
			menuType = GadgetModule.BREWER_MENU.get();
		}
	}
}
