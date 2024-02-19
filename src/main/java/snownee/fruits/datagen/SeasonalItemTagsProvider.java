package snownee.fruits.datagen;

import static snownee.fruits.cherry.CherryModule.CHERRY;
import static snownee.fruits.cherry.CherryModule.REDLOVE;
import static snownee.fruits.datagen.SeasonalBlockTagsProvider.SERENESEASONS;
import static snownee.fruits.pomegranate.PomegranateModule.POMEGRANATE;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.kiwi.AbstractModule;

public class SeasonalItemTagsProvider extends FabricTagProvider.ItemTagProvider {
	static final TagKey<Item> SPRING_CROPS = AbstractModule.itemTag(SERENESEASONS, "spring_crops");
	static final TagKey<Item> SUMMER_CROPS = AbstractModule.itemTag(SERENESEASONS, "summer_crops");
	static final TagKey<Item> AUTUMN_CROPS = AbstractModule.itemTag(SERENESEASONS, "autumn_crops");
	static final TagKey<Item> WINTER_CROPS = AbstractModule.itemTag(SERENESEASONS, "winter_crops");

	public SeasonalItemTagsProvider(
			FabricDataOutput output,
			CompletableFuture<HolderLookup.Provider> completableFuture,
			@Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
		super(output, completableFuture, blockTagProvider);
	}

	// spring: cherry
	// summer: apple, redlove
	// autumn: apple, redlove, citrus
	@Override
	protected void addTags(HolderLookup.Provider arg) {
		copy(SeasonalBlockTagsProvider.SPRING_CROPS, SPRING_CROPS);
		copy(SeasonalBlockTagsProvider.SUMMER_CROPS, SUMMER_CROPS);
		copy(SeasonalBlockTagsProvider.AUTUMN_CROPS, AUTUMN_CROPS);
		copy(SeasonalBlockTagsProvider.WINTER_CROPS, WINTER_CROPS);

		Item[] citrus = FFRegistries.FRUIT_TYPE.stream()
				.filter($ -> CoreModule.CITRUS_LOG.is($.log.get()))
				.map(t -> t.fruit.get())
				.toArray(Item[]::new);

		getOrCreateTagBuilder(SPRING_CROPS)
				.add(CHERRY.get());
		getOrCreateTagBuilder(SUMMER_CROPS)
				.add(Items.APPLE, REDLOVE.get());
		getOrCreateTagBuilder(AUTUMN_CROPS)
				.add(Items.APPLE, REDLOVE.get(), POMEGRANATE.get().asItem())
				.add(citrus);
	}

	@Override
	public @NotNull String getName() {
		return "[Seasonal] " + super.getName();
	}
}
