package snownee.fruits.compat.rei;

import java.util.List;
import java.util.function.Consumer;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.lychee.compat.rei.display.BaseREIDisplay;

public class HybridizingDisplay extends BaseREIDisplay<HybridizingRecipe> {
	public HybridizingDisplay(HybridizingRecipe recipe, CategoryIdentifier<?> categoryId) {
		super(recipe, categoryId);
	}

	@Override
	public List<EntryIngredient> getInputEntries() {
		return appendItems(super.getInputEntries(), recipe::addInvisibleInputs);
	}

	@Override
	public List<EntryIngredient> getOutputEntries() {
		return appendItems(super.getOutputEntries(), recipe::addInvisibleOutputs);
	}

	private List<EntryIngredient> appendItems(List<EntryIngredient> entries, Consumer<Consumer<ItemStack>> function) {
		function.accept($ -> entries.add(EntryIngredients.of($)));
		return entries;
	}
}
