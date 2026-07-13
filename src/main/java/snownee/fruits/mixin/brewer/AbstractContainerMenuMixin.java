package snownee.fruits.mixin.brewer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import snownee.fruits.gadget.brewer.RemoteBrewerContainerData;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
	@Inject(method = "checkContainerDataCount", at = @At("HEAD"), cancellable = true)
	private static void checkContainerDataCount(ContainerData data, int expected, CallbackInfo ci) {
		if (data instanceof RemoteBrewerContainerData) {
			ci.cancel();
		}
	}
}
