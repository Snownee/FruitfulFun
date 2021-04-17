package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import snownee.fruits.CoreModule;

@Mixin(FoxEntity.class)
public class MixinFoxEntity {

	@Inject(at = @At("HEAD"), method = "isBreedingItem", cancellable = true)
	public void fruits_isBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if (CoreModule.FOX_BREEDABLES.contains(stack.getItem())) {
			info.setReturnValue(Boolean.TRUE);
		}
	}

}
