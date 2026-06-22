package snownee.fruits.datagen;

import static net.minecraft.world.item.Items.APPLE;
import static net.minecraft.world.item.Items.CHORUS_FRUIT;
import static net.minecraft.world.item.Items.GLOW_BERRIES;
import static net.minecraft.world.item.Items.MELON_SLICE;
import static net.minecraft.world.item.Items.SWEET_BERRIES;
import static snownee.fruits.CoreModule.CITRUS_FRUITS;
import static snownee.fruits.cherry.CherryModule.CHERRY_CROWN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_CROWN;
import static snownee.fruits.compat.farmersdelight.FarmersDelightModule.CITRUS_CABINET;
import static snownee.fruits.compat.farmersdelight.FarmersDelightModule.REDLOVE_CABINET;
import static snownee.fruits.food.FoodModule.HONEY_POMELO_TEA;
import static snownee.fruits.food.FoodModule.RICE_WITH_FRUITS;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
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
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModules;

public class FFItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	static final TagKey<Item> CITRUS_LOGS = AbstractModule.itemTag(FruitfulFun.ID, "citrus_logs");
	static final TagKey<Item> REDLOVE_LOGS = AbstractModule.itemTag(FruitfulFun.ID, "redlove_logs");
	static final TagKey<Item> FRUITS = AbstractModule.itemTag("c", "fruits");
	static final TagKey<Item> TULIPS = AbstractModule.itemTag("c", "tulips");
	static final TagKey<Item> HAT = AbstractModule.itemTag("trinkets", "head/hat");
	static final TagKey<Item> WOODEN_CABINETS = AbstractModule.itemTag("farmersdelight", "cabinets/wooden");
	static final TagKey<Item> OFFHAND_EQUIPMENT = AbstractModule.itemTag("farmersdelight", "offhand_equipment");
	static final TagKey<Item> HYDRATING_DRINKS = AbstractModule.itemTag("dehydration", "hydrating_drinks");
	static final TagKey<Item> UPRIGHT_ON_BELT = AbstractModule.itemTag("create", "upright_on_belt");
	static final TagKey<Item> GADGET_TOKEN = AbstractModule.itemTag(FruitfulFun.ID, "gadget_token");

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

		var fruits = valueLookupBuilder(FRUITS).add(APPLE, MELON_SLICE, SWEET_BERRIES, CHORUS_FRUIT, GLOW_BERRIES);
		var citrusFruits = valueLookupBuilder(CITRUS_FRUITS);
		var villagerPicksUp = valueLookupBuilder(ItemTags.VILLAGER_PICKS_UP);
		FFRegistries.FRUIT_TYPE.forEach($ -> {
			if (CoreModule.CITRUS_LOG.is($.log.get())) {
				citrusFruits.add($.fruit.get());
			} else {
				fruits.add($.fruit.get());
			}
			villagerPicksUp.add($.fruit.get());
		});
		fruits.addTag(CITRUS_FRUITS);
		valueLookupBuilder(ItemTags.FOX_FOOD).addTag(FRUITS);
		getOrCreateRawBuilder(ItemTags.PANDA_FOOD).addOptionalElement(RICE_WITH_FRUITS.key());

		var tagAppender = valueLookupBuilder(ConventionalItemTags.FOODS);
		tagAppender.addTag(FRUITS);
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
				.addOptionalTag(AbstractModule.itemTag("biomeswevegone", "flowers/tulips"));
		getOrCreateRawBuilder(ConventionalItemTags.SHIELD_TOOLS).addOptionalElement(GadgetModule.BUZZY_SHIELD.key());
		getOrCreateRawBuilder(OFFHAND_EQUIPMENT).addOptionalElement(GadgetModule.BUZZY_SHIELD.key());

		if (Hooks.farmersdelight) {
			getOrCreateRawBuilder(WOODEN_CABINETS)
					.addOptionalElement(CITRUS_CABINET.key())
					.addOptionalElement(REDLOVE_CABINET.key());
		}
	}
}
