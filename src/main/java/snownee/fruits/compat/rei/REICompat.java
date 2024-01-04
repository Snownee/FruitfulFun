package snownee.fruits.compat.rei;

import dev.architectury.event.EventResult;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.MutagenItem;

public class REICompat implements REIClientPlugin {
	@Override
	public void registerDisplays(DisplayRegistry registry) {
		CategoryIdentifier<Display> categoryIdentifier = CategoryIdentifier.of("minecraft", "plugins/brewing");
		registry.registerVisibilityPredicate((category, display) -> {
			if (category.getCategoryIdentifier().equals(categoryIdentifier) && display.getOutputEntries().stream()
					.flatMap(EntryIngredient::stream)
					.anyMatch($ -> {
						if ($.getType() != VanillaEntryTypes.ITEM) {
							return false;
						}
						ItemStack output = $.castValue();
						return BeeModule.MUTAGEN.is(output) && output.hasTag();
					})) {
				return EventResult.interruptFalse();
			}
			return EventResult.pass();
		});
		registry.add(new BrewingRecipe(
				Ingredient.of(MutagenItem.BREWING_ITEM),
				Ingredient.of(Items.POTION.getDefaultInstance()),
				new ItemStack(BeeModule.MUTAGEN.get())));
	}
}
