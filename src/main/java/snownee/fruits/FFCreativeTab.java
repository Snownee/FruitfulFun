package snownee.fruits;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.core.registries.BuiltInRegistries;
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
import snownee.kiwi.datagen.GameObjectLookup;
import snownee.kiwi.item.ItemCategoryFiller;

@KiwiModule("creative_tab")
@KiwiModule.Optional
public final class FFCreativeTab extends AbstractModule {
	public static final KiwiGO<CreativeModeTab> MAIN = go(() -> itemCategory(
			FruitfulFun.ID,
			"main",
			CoreModule.GRAPEFRUIT::itemStack)
			.title(Component.translatable("modmenu.nameTranslation.fruitfulfun"))
			.displayItems(FFCreativeTab::generate)
			.build());

	private static void generate(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		LinkedHashMap<String, Item> map = GameObjectLookup.all(Registries.ITEM, FruitfulFun.ID).collect(Collectors.toMap(
				item -> BuiltInRegistries.ITEM.getKey(item).getPath(),
				item -> item,
				(a, b) -> a,
				LinkedHashMap::new
		));
		map.remove("redlove_crown");
		for (FruitType type : FFRegistries.FRUIT_TYPE) {
			add(type.fruit.get(), output::accept);
		}
		add(PomegranateModule.ENCHANTED_POMEGRANATE.get(), output::accept);
		if (Hooks.food) {
			add(FoodModule.LEMON_ROAST_CHICKEN_BLOCK.get(), output::accept);
			add(FoodModule.LEMON_ROAST_CHICKEN.get(), output::accept);
			add(FoodModule.GRAPEFRUIT_PANNA_COTTA.get(), output::accept);
			add(FoodModule.DONAUWELLE.get(), output::accept);
			add(FoodModule.HONEY_POMELO_TEA.get(), output::accept);
			add(FoodModule.RICE_WITH_FRUITS.get(), output::accept);
			add(FoodModule.CHORUS_FRUIT_PIE.get(), output::accept);
			add(FoodModule.CHORUS_FRUIT_PIE_SLICE.get(), output::accept);
		}
		addByTemplate(map, "*_sapling", output::accept);
		addByTemplate(map, "*_leaves", output::accept);
		addByTemplate(map, "citrus_*", output::accept);
		addByTemplate(map, "redlove_*", output::accept);
		add(CherryModule.CHERRY_CROWN.get(), output::accept);
		add(CherryModule.REDLOVE_CROWN.get(), output::accept);
		add(CherryModule.PEACH_PINK_PETALS.get(), output::accept);
		addByTemplate(map, "*_banner_pattern", output::accept);
		if (Hooks.bee) {
			add(BeeModule.INSPECTOR.get(), output::accept);
			add(BeeModule.MUTAGEN.get(), output::accept);
		}
		if (Hooks.gadget) {
			add(GadgetModule.BUZZY_CRAFTER.get(), output::accept);
			add(GadgetModule.BUZZY_SHIELD.get(), output::accept);
			addByTemplate(map, "*_candle", output::accept);
		}
	}

	private static void addByTemplate(Map<String, Item> map, String template, Consumer<ItemStack> consumer) {
		boolean prefix = template.startsWith("*");
		String trimmed = prefix ? template.substring(1) : template.substring(0, template.length() - 1);
		for (String key : List.copyOf(map.keySet())) {
			if (prefix && !key.endsWith(trimmed) || !prefix && !key.startsWith(trimmed)) {
				continue;
			}
			add(map, key, consumer);
			map.remove(key);
		}
	}

	private static void add(Map<String, Item> map, String key, Consumer<ItemStack> consumer) {
		add(map.get(key), consumer);
	}

	private static void add(ItemLike item, Consumer<ItemStack> consumer) {
		if (item instanceof ItemCategoryFiller filler) {
			List<ItemStack> itemStacks = Lists.newArrayList();
			filler.fillItemCategory(MAIN.getOrCreate(), FeatureFlags.VANILLA_SET, true, itemStacks);
			itemStacks.forEach(consumer);
		} else {
			consumer.accept(item.asItem().getDefaultInstance());
		}
	}
}
