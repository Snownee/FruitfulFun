package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.MutagenItem;


@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

	@Inject(method = "mix", at = @At("HEAD"), cancellable = true)
	private static void mix(ItemStack ingredient, ItemStack container, CallbackInfoReturnable<ItemStack> ci) {
		if (matchesMutagenRecipe(ingredient, container)) {
			ci.setReturnValue(BeeModule.MUTAGEN.get().randomMutagen(true, null));
		}
	}

	@Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
	private static void hasMix(ItemStack container, ItemStack ingredient, CallbackInfoReturnable<Boolean> ci) {
		if (matchesMutagenRecipe(ingredient, container)) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
	private static void isIngredient(ItemStack ingredient, CallbackInfoReturnable<Boolean> ci) {
		if (ingredient.is(MutagenItem.BREWING_ITEM)) {
			ci.setReturnValue(true);
		}
	}

	@Unique
	private static boolean matchesMutagenRecipe(ItemStack ingredient, ItemStack container) {
		if (!Hooks.bee) {
			return false;
		}
		if (!ingredient.is(MutagenItem.BREWING_ITEM)) {
			return false;
		}
		if (!container.is(Items.POTION) || PotionUtils.getPotion(container) != Potions.WATER) {
			return false;
		}
		return true;
	}
}
