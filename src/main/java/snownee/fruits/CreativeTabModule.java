package snownee.fruits;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.food.FoodModule;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.item.ItemCategoryFiller;
import snownee.kiwi.util.GameObjectLookup;

@KiwiModule("creative_tab")
@KiwiModule.Optional
public final class CreativeTabModule extends AbstractModule {
	public static final KiwiGO<CreativeModeTab> MAIN = go(() -> itemCategory(
			FruitfulFun.id("main"),
			CoreModule.GRAPEFRUIT::itemStack).title(Component.translatable("modmenu.nameTranslation.fruitfulfun"))
			.displayItems(CreativeTabModule.Generator::new)
			.build());

	static class Generator {
		private final Map<Item, String> map;
		private final CreativeModeTab.Output output;

		public Generator(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
			this.output = output;
			HolderLookup.RegistryLookup<Item> items = parameters.holders().lookupOrThrow(Registries.ITEM);
			this.map = GameObjectLookup.allHolders(items, FruitfulFun.ID)
					.collect(Collectors.toMap(
							Holder.Reference::value,
							item -> item.key().identifier().getPath(),
							(a, _) -> a,
							LinkedHashMap::new));
			run();
		}

		void run() {
			map.remove(CherryModule.REDLOVE_CROWN.get());
			for (FruitType type : FFRegistries.FRUIT_TYPE) {
				add(type.fruit.get());
			}
			add(PomegranateModule.ENCHANTED_POMEGRANATE.get());
			if (Hooks.food) {
				add(FoodModule.LEMON_ROAST_CHICKEN_BLOCK.get());
				add(FoodModule.LEMON_ROAST_CHICKEN.get());
				add(FoodModule.GRAPEFRUIT_PANNA_COTTA.get());
				add(FoodModule.DONAUWELLE.get());
				add(FoodModule.HONEY_POMELO_TEA.get());
				add(FoodModule.RICE_WITH_FRUITS.get());
				add(FoodModule.CHORUS_FRUIT_PIE.get());
				add(FoodModule.CHORUS_FRUIT_PIE_SLICE.get());
			}
			addByTemplate("*_sapling");
			addByTemplate("*_leaves");
			add(CoreModule.CITRUS_LOG.get());
			add(CoreModule.CITRUS_WOOD.get());
			add(CoreModule.STRIPPED_CITRUS_LOG.get());
			add(CoreModule.STRIPPED_CITRUS_WOOD.get());
			addByTemplate("citrus_*");
			add(CherryModule.REDLOVE_LOG.get());
			add(CherryModule.REDLOVE_WOOD.get());
			add(CherryModule.STRIPPED_REDLOVE_LOG.get());
			add(CherryModule.STRIPPED_REDLOVE_WOOD.get());
			addByTemplate("redlove_*");
			add(CherryModule.CHERRY_CROWN.get());
			add(CherryModule.REDLOVE_CROWN.get());
			add(CherryModule.PEACH_PINK_PETALS.get());
			addByTemplate("*_banner_pattern");
			if (Hooks.bee) {
				add(BeeModule.INSPECTOR.get());
				add(BeeModule.MUTAGEN.get());
			}
			if (Hooks.gadget) {
				add(GadgetModule.BUZZY_CRAFTER.get());
				add(GadgetModule.BUZZY_SHIELD.get());
				addByTemplate("*_candle");
			}
		}

		void addByTemplate(String template) {
			boolean prefix = template.startsWith("*");
			String trimmed = prefix ? template.substring(1) : template.substring(0, template.length() - 1);
			for (Map.Entry<Item, String> entry : List.copyOf(map.entrySet())) {
				String key = entry.getValue();
				if (prefix && !key.endsWith(trimmed) || !prefix && !key.startsWith(trimmed)) {
					continue;
				}
				add(entry.getKey());
			}
		}

		void add(ItemLike item) {
			map.remove(item.asItem());
			if (item instanceof ItemCategoryFiller filler) {
				List<ItemStack> itemStacks = Lists.newArrayList();
				filler.fillItemCategory(MAIN.getOrCreate(), FeatureFlags.VANILLA_SET, true, itemStacks);
				itemStacks.forEach(output::accept);
			} else {
				output.accept(item.asItem().getDefaultInstance());
			}
		}
	}
}
