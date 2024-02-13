package snownee.fruits.compat.jei;

import java.util.List;

import org.jetbrains.annotations.Unmodifiable;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import net.minecraft.world.item.ItemStack;

public class NoHashBrewingRecipe implements IJeiBrewingRecipe {
	private final List<ItemStack> ingredients;
	private final List<ItemStack> potionInputs;
	private final ItemStack potionOutput;
	private final int brewingSteps;

	public NoHashBrewingRecipe(List<ItemStack> ingredients, List<ItemStack> potionInputs, ItemStack potionOutput, int brewingSteps) {
		this.ingredients = List.copyOf(ingredients);
		this.potionInputs = List.copyOf(potionInputs);
		this.potionOutput = potionOutput;
		this.brewingSteps = brewingSteps;
	}

	@Override
	public @Unmodifiable List<ItemStack> getPotionInputs() {
		return potionInputs;
	}

	@Override
	public @Unmodifiable List<ItemStack> getIngredients() {
		return ingredients;
	}

	@Override
	public ItemStack getPotionOutput() {
		return potionOutput;
	}

	@Override
	public int getBrewingSteps() {
		return brewingSteps;
	}
}
