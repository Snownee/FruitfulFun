package snownee.fruits.datagen;

import static snownee.fruits.CoreModule.CITRUS_BOAT;
import static snownee.fruits.CoreModule.CITRUS_CHEST_BOAT;
import static snownee.fruits.CoreModule.CITRUS_FRUITS;
import static snownee.fruits.CoreModule.CITRUS_LOG;
import static snownee.fruits.CoreModule.CITRUS_SHELF;
import static snownee.fruits.CoreModule.CITRUS_WOOD;
import static snownee.fruits.CoreModule.SNOWFLAKE_BANNER_PATTERN;
import static snownee.fruits.CoreModule.STRIPPED_CITRUS_LOG;
import static snownee.fruits.CoreModule.STRIPPED_CITRUS_WOOD;
import static snownee.fruits.cherry.CherryModule.CHERRY;
import static snownee.fruits.cherry.CherryModule.CHERRY_CROWN;
import static snownee.fruits.cherry.CherryModule.HEART_BANNER_PATTERN;
import static snownee.fruits.cherry.CherryModule.REDLOVE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_BOAT;
import static snownee.fruits.cherry.CherryModule.REDLOVE_CHEST_BOAT;
import static snownee.fruits.cherry.CherryModule.REDLOVE_CROWN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SHELF;
import static snownee.fruits.cherry.CherryModule.STRIPPED_REDLOVE_LOG;
import static snownee.fruits.cherry.CherryModule.STRIPPED_REDLOVE_WOOD;
import static snownee.fruits.compat.farmersdelight.FarmersDelightModule.CITRUS_CABINET;
import static snownee.fruits.compat.farmersdelight.FarmersDelightModule.REDLOVE_CABINET;
import static snownee.fruits.food.FoodModule.CHORUS_FRUIT_PIE;
import static snownee.fruits.food.FoodModule.CHORUS_FRUIT_PIE_SLICE;
import static snownee.fruits.food.FoodModule.HONEY_POMELO_TEA;
import static snownee.fruits.food.FoodModule.RICE_WITH_FRUITS;
import static snownee.fruits.gadget.GadgetModule.VAC_GUN;
import static snownee.fruits.gadget.GadgetModule.VAC_GUN_CASING;
import static snownee.fruits.pomegranate.PomegranateModule.POMEGRANATE;
import static snownee.kiwi.AbstractModule.itemTag;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.gadget.GadgetModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModules;

public class FFItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	static final TagKey<Item> CITRUS_LOGS = itemTag(FruitfulFun.ID, "citrus_logs");
	static final TagKey<Item> REDLOVE_LOGS = itemTag(FruitfulFun.ID, "redlove_logs");
	static final TagKey<Item> TULIPS = itemTag("c:flowers/tulips");
	static final TagKey<Item> HAT = itemTag("trinkets", "head/hat");
	static final TagKey<Item> WOODEN_CABINETS = itemTag("farmersdelight", "cabinets/wooden");
	static final TagKey<Item> OFFHAND_EQUIPMENT = itemTag("farmersdelight", "offhand_equipment");
	static final TagKey<Item> HYDRATING_DRINKS = itemTag("dehydration", "hydrating_drinks");
	static final TagKey<Item> UPRIGHT_ON_BELT = itemTag("create", "upright_on_belt");
	static final TagKey<Item> GADGET_TOKEN = itemTag(FruitfulFun.ID, "gadget_token");

	public FFItemTagsProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registriesFuture,
			FabricTagsProvider.BlockTagsProvider blockTagProvider) {
		super(output, registriesFuture, blockTagProvider);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		copy(BlockTags.PLANKS, ItemTags.PLANKS);
		copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
		copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
		copy(CoreModule.ALL_LEAVES, ItemTags.LEAVES);
		copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);

		copy(FFBlockTagsProvider.CITRUS_LOGS, CITRUS_LOGS);
		copy(FFBlockTagsProvider.REDLOVE_LOGS, REDLOVE_LOGS);
		copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
		valueLookupBuilder(ConventionalItemTags.OVERWORLD_NATURAL_LOGS).add(CITRUS_LOG.asItem());
		valueLookupBuilder(ConventionalItemTags.NATURAL_WOODS).add(CITRUS_WOOD.asItem());
		valueLookupBuilder(ConventionalItemTags.STRIPPED_LOGS).add(STRIPPED_CITRUS_LOG.asItem(), STRIPPED_REDLOVE_LOG.asItem());
		valueLookupBuilder(ConventionalItemTags.STRIPPED_WOODS).add(STRIPPED_CITRUS_WOOD.asItem(), STRIPPED_REDLOVE_WOOD.asItem());

		var fruits = valueLookupBuilder(ConventionalItemTags.FRUIT_FOODS);
		var citrusFruits = valueLookupBuilder(CITRUS_FRUITS);
		var villagerPicksUp = valueLookupBuilder(ItemTags.VILLAGER_PICKS_UP);
		FFRegistries.FRUIT_TYPE.forEach($ -> {
			if (CITRUS_LOG.is($.log.get())) {
				TagKey<Item> tagKey = itemTag("c:crops/%s".formatted(BuiltInRegistries.ITEM.getKey($.fruit.get()).getPath()));
				citrusFruits.addTag(tagKey);
				valueLookupBuilder(tagKey).add($.fruit.get());
			} else {
				fruits.add($.fruit.get());
			}
			villagerPicksUp.add($.fruit.get());
		});
		fruits.addTag(CITRUS_FRUITS);
		valueLookupBuilder(itemTag("c:crops/apple")).add(Items.APPLE, REDLOVE.get());
		valueLookupBuilder(itemTag("c:crops/cherry")).add(CHERRY.get());
		valueLookupBuilder(itemTag("c:crops/pomegranate")).add(POMEGRANATE.asItem());
		valueLookupBuilder(ItemTags.FOX_FOOD).addTag(ConventionalItemTags.FRUIT_FOODS);
		getOrCreateRawBuilder(ItemTags.PANDA_FOOD).addOptionalElement(RICE_WITH_FRUITS.key());

		var tagAppender = valueLookupBuilder(ConventionalItemTags.FOODS);
		KiwiModules.get(FruitfulFun.id("food")).getRegistryEntries(Registries.ITEM)
				.map(KiwiGO::get)
				.forEach(tagAppender::addOptional);
		tagAppender = valueLookupBuilder(UPRIGHT_ON_BELT);
		KiwiModules.get(FruitfulFun.id("food")).getRegistryEntries(Registries.ITEM)
				.map(KiwiGO::get)
				.forEach(tagAppender::addOptional);
		tagAppender.addOptional(BeeModule.MUTAGEN.get());
		getOrCreateRawBuilder(HYDRATING_DRINKS).addOptionalElement(HONEY_POMELO_TEA.key());
		valueLookupBuilder(HAT).add(CHERRY_CROWN.get(), REDLOVE_CROWN.get());
		valueLookupBuilder(GADGET_TOKEN).add(Items.EMERALD_BLOCK);
		valueLookupBuilder(TULIPS)
				.add(Items.ORANGE_TULIP)
				.add(Items.PINK_TULIP)
				.add(Items.RED_TULIP)
				.add(Items.WHITE_TULIP)
				.addOptionalTag(itemTag("biomeswevegone", "flowers/tulips"));
		getOrCreateRawBuilder(ConventionalItemTags.SHIELD_TOOLS).addOptionalElement(GadgetModule.BUZZY_SHIELD.key());
		getOrCreateRawBuilder(OFFHAND_EQUIPMENT).addOptionalElement(GadgetModule.BUZZY_SHIELD.key());

		valueLookupBuilder(ItemTags.BOATS).add(CITRUS_BOAT.get()).add(REDLOVE_BOAT.get());
		valueLookupBuilder(ItemTags.CHEST_BOATS).add(CITRUS_CHEST_BOAT.get()).add(REDLOVE_CHEST_BOAT.get());
		valueLookupBuilder(ItemTags.WOODEN_SHELVES).add(CITRUS_SHELF.asItem()).add(REDLOVE_SHELF.asItem());
		valueLookupBuilder(ItemTags.LOOM_PATTERNS).addOptional(SNOWFLAKE_BANNER_PATTERN.get()).add(HEART_BANNER_PATTERN.get());

		valueLookupBuilder(ConventionalItemTags.HONEY_DRINKS).addOptional(HONEY_POMELO_TEA.asItem());
		valueLookupBuilder(ConventionalItemTags.PIE_FOODS)
				.addOptional(CHORUS_FRUIT_PIE.asItem())
				.addOptional(CHORUS_FRUIT_PIE_SLICE.asItem());

		if (Hooks.farmersdelight) {
			getOrCreateRawBuilder(WOODEN_CABINETS)
					.addOptionalElement(CITRUS_CABINET.key())
					.addOptionalElement(REDLOVE_CABINET.key());
		}

		valueLookupBuilder(ConventionalItemTags.HIDDEN_FROM_RECIPE_VIEWERS).add(VAC_GUN_CASING.get(), VAC_GUN.get());
	}
}
