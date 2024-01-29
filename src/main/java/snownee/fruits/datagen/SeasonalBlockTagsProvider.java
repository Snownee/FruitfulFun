package snownee.fruits.datagen;

import static snownee.fruits.CoreModule.APPLE_LEAVES;
import static snownee.fruits.CoreModule.CITRUS_LOG;
import static snownee.fruits.cherry.CherryModule.CHERRY_LEAVES;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LEAVES;
import static snownee.fruits.pomegranate.PomegranateModule.POMEGRANATE_LEAVES;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import snownee.fruits.FFRegistries;
import snownee.kiwi.AbstractModule;

public class SeasonalBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
	static final String SERENESEASONS = "sereneseasons";
	static final TagKey<Block> SPRING_CROPS = AbstractModule.blockTag(SERENESEASONS, "spring_crops");
	static final TagKey<Block> SUMMER_CROPS = AbstractModule.blockTag(SERENESEASONS, "summer_crops");
	static final TagKey<Block> AUTUMN_CROPS = AbstractModule.blockTag(SERENESEASONS, "autumn_crops");
	static final TagKey<Block> WINTER_CROPS = AbstractModule.blockTag(SERENESEASONS, "winter_crops");
	static final TagKey<Block> UNBREAKABLE_INFERTILE_CROPS = AbstractModule.blockTag(SERENESEASONS, "unbreakable_infertile_crops");

	public SeasonalBlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	// spring: cherry
	// summer: apple, redlove
	// autumn: apple, redlove, citrus, pomegranate
	@Override
	protected void addTags(HolderLookup.Provider arg) {
		Block[] saplings = FFRegistries.FRUIT_TYPE.stream().map(t -> t.sapling.get()).toArray(Block[]::new);
		Block[] citrusLeaves = FFRegistries.FRUIT_TYPE.stream()
				.filter($ -> CITRUS_LOG.is($.log.get()))
				.map(t -> t.leaves.get())
				.toArray(Block[]::new);
		getOrCreateTagBuilder(SPRING_CROPS).add(saplings)
				.add(CHERRY_LEAVES.get());
		getOrCreateTagBuilder(SUMMER_CROPS).add(saplings)
				.add(APPLE_LEAVES.get(), REDLOVE_LEAVES.get());
		getOrCreateTagBuilder(AUTUMN_CROPS).add(saplings)
				.add(APPLE_LEAVES.get(), REDLOVE_LEAVES.get(), POMEGRANATE_LEAVES.get())
				.add(citrusLeaves);
		getOrCreateTagBuilder(WINTER_CROPS).add(saplings);

		Block[] leaves = FFRegistries.FRUIT_TYPE.stream().map(t -> t.leaves.get()).toArray(Block[]::new);
		getOrCreateTagBuilder(UNBREAKABLE_INFERTILE_CROPS).add(leaves);
	}

	@Override
	public @NotNull String getName() {
		return "[Seasonal] " + super.getName();
	}
}
