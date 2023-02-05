package snownee.fruits.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.food.FoodModule;

@Mixin(Panda.class)
public class PandaMixin {

	@Final
	@Mutable
	private static Predicate<ItemEntity> PANDA_ITEMS;

	static {
		PANDA_ITEMS = PANDA_ITEMS.or(itemEntity -> {
			ItemStack itemstack = itemEntity.getItem();
			return Hooks.food && itemEntity.isAlive() && !itemEntity.hasPickUpDelay() && itemstack.is(FoodModule.PANDA_FOOD);
		});
	}

	@Inject(at = @At("HEAD"), method = "isFood", cancellable = true)
	private void fruits_isFood(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (Hooks.food && stack.is(FoodModule.PANDA_FOOD)) {
			ci.setReturnValue(true);
		}
	}

}
