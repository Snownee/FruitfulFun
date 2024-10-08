package snownee.fruits.compat.rei;

import java.util.Map;
import java.util.function.Consumer;

import dev.architectury.event.EventResult;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.fruits.compat.FFJEIREI;
import snownee.fruits.ritual.RitualModule;
import snownee.fruits.vacuum.VacModule;
import snownee.lychee.compat.rei.REICompat;
import snownee.lychee.compat.rei.category.BaseREICategory;
import snownee.lychee.compat.rei.display.BaseREIDisplay;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.recipe.LycheeRecipe;
import snownee.lychee.core.recipe.type.LycheeRecipeType;

public class FFREICompat implements REIClientPlugin {
	public FFREICompat() {
		REICompat.addCategoryFactoryProvider($ -> {
			if (Hooks.bee) {
				$.put(BeeModule.RECIPE_TYPE.get().categoryId, $$ -> new HybridizingCategory(BeeModule.RECIPE_TYPE.get()));
			}
			if (Hooks.ritual) {
				$.put(RitualModule.RECIPE_TYPE.get().categoryId, $$ -> new DragonRitualCategory(RitualModule.RECIPE_TYPE.get()));
			}
		});
		REICompat.addDisplayFactoryProvider($ -> {
			if (Hooks.bee) {
				REICompat.registerDisplayFactory($, BeeModule.RECIPE_TYPE.get().categoryId, HybridizingDisplay::new);
			}
			if (Hooks.ritual) {
				REICompat.registerDisplayFactory($, RitualModule.RECIPE_TYPE.get().categoryId, DragonRitualDisplay::new);
			}
		});
	}

	@Override
	public void registerCategories(CategoryRegistry registry) {
		if (Hooks.ritual) {
			ItemStack dragonHead = Items.DRAGON_HEAD.getDefaultInstance();
			ItemStack pie = FFJEIREI.pieItem.get();
			forEachCategories(BeeModule.RECIPE_TYPE.get(), $ -> {
				registry.addWorkstations($.getCategoryIdentifier(), EntryStacks.of(dragonHead), EntryStacks.of(pie));
			});
		}
	}

	private static <C extends LycheeContext, T extends LycheeRecipe<C>, D extends BaseREIDisplay<T>> void forEachCategories(
			LycheeRecipeType<C, T> recipeType,
			Consumer<BaseREICategory<C, T, D>> consumer) {
		//noinspection unchecked
		REICompat.CATEGORIES.getOrDefault(recipeType.categoryId, Map.of()).values().stream().map($ -> (BaseREICategory<C, T, D>) $).forEach(
				consumer);
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		if (FFCommonConfig.isMutagenRecipeEnabled()) {
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

		FFJEIREI.addInformation((items, component) -> {
			registry.add(DefaultInformationDisplay.createFromEntries(EntryIngredients.ofItemStacks(items), items.get(0).getHoverName())
					.line(component));
		});
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
		if (Hooks.vac) {
			rule.hide(EntryStacks.of(VacModule.VAC_GUN.get()));
			rule.hide(EntryStacks.of(VacModule.VAC_GUN_CASING.get()));
		}
	}
}
