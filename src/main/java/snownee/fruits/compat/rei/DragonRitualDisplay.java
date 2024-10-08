package snownee.fruits.compat.rei;

import java.util.List;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import snownee.fruits.compat.FFJEIREI;
import snownee.fruits.ritual.DragonRitualRecipe;
import snownee.lychee.compat.rei.display.BaseREIDisplay;

public class DragonRitualDisplay extends BaseREIDisplay<DragonRitualRecipe> {
	public DragonRitualDisplay(DragonRitualRecipe recipe, CategoryIdentifier<?> categoryId) {
		super(recipe, categoryId);
	}

	@Override
	public List<EntryIngredient> getInputEntries() {
		List<EntryIngredient> entries = super.getInputEntries();
		entries.add(EntryIngredients.of(FFJEIREI.pieItem.get()));
		return entries;
	}
}
