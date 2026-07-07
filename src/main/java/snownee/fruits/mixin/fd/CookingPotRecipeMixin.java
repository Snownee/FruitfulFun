package snownee.fruits.mixin.fd;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import snownee.kiwi.recipe.AlternativesIngredient;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(CookingPotRecipe.class)
public class CookingPotRecipeMixin {
	@Mutable
	@Shadow
	@Final
	private List<Ingredient> inputItems;
	@Unique
	private boolean kiwi$trimmed;

	@Inject(
			method = {
					"placementInfo",
					"matches(Lvectorwing/farmersdelight/refabricated/inventory/RecipeWrapper;Lnet/minecraft/world/level/Level;)Z",
					"display"}, at = @At("HEAD"))
	private void kiwi$trim(CallbackInfoReturnable<PlacementInfo> cir) {
		if (kiwi$trimmed) {
			return;
		}
		kiwi$trimmed = true;
		List<Ingredient> filtered = inputItems.stream().filter((ingredient) ->
				!(ingredient.getCustomIngredient() instanceof AlternativesIngredient) || !ingredient.isEmpty()).toList();
		if (filtered.size() != inputItems.size()) {
			inputItems = filtered;
		}
	}
}
