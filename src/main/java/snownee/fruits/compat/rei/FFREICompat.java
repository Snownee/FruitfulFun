package snownee.fruits.compat.rei;

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
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.fruits.compat.lychee.LycheeCompat;
import snownee.fruits.gadget.GadgetModule;

public class FFREICompat implements REIClientPlugin {
	public FFREICompat() {
//		REICompat.addCategoryFactoryProvider($ -> {
//			if (Hooks.bee) {
//				$.put(BeeModule.RECIPE_TYPE.get().categoryId, $$ -> new HybridizingCategory(BeeModule.RECIPE_TYPE.get()));
//			}
//			if (Hooks.ritual) {
//				$.put(RitualModule.RECIPE_TYPE.get().categoryId, $$ -> new DragonRitualCategory(RitualModule.RECIPE_TYPE.get()));
//			}
//		});
//		REICompat.addDisplayFactoryProvider($ -> {
//			if (Hooks.bee) {
//				REICompat.registerDisplayFactory($, BeeModule.RECIPE_TYPE.get().categoryId, HybridizingDisplay::new);
//			}
//			if (Hooks.ritual) {
//				REICompat.registerDisplayFactory($, RitualModule.RECIPE_TYPE.get().categoryId, DragonRitualDisplay::new);
//			}
//		});
	}

	@Override
	public void registerCategories(CategoryRegistry registry) {
//		if (Hooks.ritual) {
//			ItemStack dragonHead = Items.DRAGON_HEAD.getDefaultInstance();
//			ItemStack pie = LycheeCompat.pieItem.get();
//			forEachCategories(
//					BeeModule.RECIPE_TYPE.get(), $ -> {
//						registry.addWorkstations($.getCategoryIdentifier(), EntryStacks.of(dragonHead), EntryStacks.of(pie));
//					});
//		}
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
							return output.has(BeeModule.MUTAGEN_CONTENT.get());
						})) {
					return EventResult.interruptFalse();
				}
				return EventResult.pass();
			});
			registry.add(new BrewingRecipe(
					EntryIngredients.of(MutagenItem.BREWING_ITEM),
					EntryIngredients.of(Items.POTION.getDefaultInstance()),
					EntryIngredients.of(new ItemStack(BeeModule.MUTAGEN.get()))));
		}

		LycheeCompat.addInformation((items, component) -> {
			registry.add(DefaultInformationDisplay.createFromEntries(EntryIngredients.ofItemStacks(items), items.getFirst().getHoverName())
					.line(component));
		});
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
		if (Hooks.gadget) {
			rule.hide(EntryStacks.of(GadgetModule.VAC_GUN.get()));
			rule.hide(EntryStacks.of(GadgetModule.VAC_GUN_CASING.get()));
		}
	}
}
