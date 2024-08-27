package snownee.fruits.mixin.food;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.food.FoodModule;

@Mixin(Panda.class)
public class PandaMixin {

	@Inject(at = @At("HEAD"), method = "isFood", cancellable = true)
	private void isFood(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (Hooks.food && stack.is(FoodModule.PANDA_FOOD)) {
			ci.setReturnValue(true);
		}
	}

}
