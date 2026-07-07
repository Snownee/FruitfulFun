package snownee.fruits.datagen;

import static snownee.fruits.cherry.CherryModule.CHERRY;
import static snownee.fruits.cherry.CherryModule.REDLOVE;
import static snownee.fruits.datagen.SeasonalBlockTags.SERENESEASONS;
import static snownee.fruits.pomegranate.PomegranateModule.POMEGRANATE_ITEM;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.kiwi.AbstractModule;

public class SeasonalItemTags extends FabricTagsProvider.ItemTagsProvider {
	static final TagKey<Item> SPRING_CROPS = AbstractModule.itemTag(SERENESEASONS, "spring_crops");
	static final TagKey<Item> SUMMER_CROPS = AbstractModule.itemTag(SERENESEASONS, "summer_crops");
	static final TagKey<Item> AUTUMN_CROPS = AbstractModule.itemTag(SERENESEASONS, "autumn_crops");
	static final TagKey<Item> WINTER_CROPS = AbstractModule.itemTag(SERENESEASONS, "winter_crops");

	public SeasonalItemTags(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> completableFuture,
			FabricTagsProvider.BlockTagsProvider blockTagProvider) {
		super(output, completableFuture, blockTagProvider);
	}

	// spring: cherry
	// summer: apple, redlove
	// autumn: apple, redlove, citrus
	@Override
	protected void addTags(HolderLookup.Provider arg) {
		copy(SeasonalBlockTags.SPRING_CROPS, SPRING_CROPS);
		copy(SeasonalBlockTags.SUMMER_CROPS, SUMMER_CROPS);
		copy(SeasonalBlockTags.AUTUMN_CROPS, AUTUMN_CROPS);
		copy(SeasonalBlockTags.WINTER_CROPS, WINTER_CROPS);

		Item[] citrus = FFRegistries.FRUIT_TYPE.stream()
				.filter($ -> CoreModule.CITRUS_LOG.is($.log.get()))
				.map(t -> t.fruit.get())
				.toArray(Item[]::new);

		valueLookupBuilder(SPRING_CROPS)
				.add(CHERRY.get());
		valueLookupBuilder(SUMMER_CROPS)
				.add(Items.APPLE, REDLOVE.get());
		valueLookupBuilder(AUTUMN_CROPS)
				.add(Items.APPLE, REDLOVE.get(), POMEGRANATE_ITEM.get())
				.add(citrus);
	}

	@Override
	public String getName() {
		return "[Seasonal] " + super.getName();
	}
}
