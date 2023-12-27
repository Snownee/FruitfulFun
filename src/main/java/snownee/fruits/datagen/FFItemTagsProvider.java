package snownee.fruits.datagen;

import static net.minecraft.world.item.Items.APPLE;
import static net.minecraft.world.item.Items.CHORUS_FRUIT;
import static net.minecraft.world.item.Items.GLOW_BERRIES;
import static net.minecraft.world.item.Items.MELON_SLICE;
import static net.minecraft.world.item.Items.SWEET_BERRIES;
import static snownee.fruits.CoreModule.CITRON;
import static snownee.fruits.cherry.CherryModule.CHERRY;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.food.FoodModule;
import snownee.kiwi.AbstractModule;

public class FFItemTagsProvider extends FabricTagProvider.ItemTagProvider {
	static final TagKey<Item> CITRUS_LOGS = AbstractModule.itemTag(FruitfulFun.ID, "citrus_logs");
	static final TagKey<Item> REDLOVE_LOGS = AbstractModule.itemTag(FruitfulFun.ID, "redlove_logs");
	static final TagKey<Item> FRUITS = AbstractModule.itemTag("c", "fruits");

	public FFItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
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
		copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		copy(CoreModule.ALL_LEAVES, ItemTags.LEAVES);
		copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);

		copy(FFBlockTagsProvider.CITRUS_LOGS, CITRUS_LOGS);
		copy(FFBlockTagsProvider.REDLOVE_LOGS, REDLOVE_LOGS);

		getOrCreateTagBuilder(FRUITS).add(APPLE, MELON_SLICE, SWEET_BERRIES, CHORUS_FRUIT, GLOW_BERRIES)
				.addOptional(CHERRY.key())
				.addOptional(CITRON.key());
		tag(ItemTags.FOX_FOOD).addTag(FRUITS);
		tag(FoodModule.PANDA_FOOD).addOptional(FoodModule.RICE_WITH_FRUITS.key());
	}
}
