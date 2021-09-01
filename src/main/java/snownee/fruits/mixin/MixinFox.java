package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.CoreModule;

@Mixin(Fox.class)
public class MixinFox {

	@Inject(at = @At("HEAD"), method = "isFood", cancellable = true)
	public void fruits_isFood(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if (CoreModule.FOX_BREEDABLES.contains(stack.getItem())) {
			info.setReturnValue(Boolean.TRUE);
		}
	}

}
