package snownee.fruits.datagen;

import static net.minecraft.tags.BlockTags.*;
import static snownee.fruits.CoreModule.*;
import static snownee.fruits.cherry.CherryModule.*;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import snownee.fruits.FruitsMod;
import snownee.kiwi.datagen.provider.KiwiBlockTagsProvider;
import snownee.kiwi.datagen.provider.TagsProviderHelper;

public class CommonBlockTagsProvider extends KiwiBlockTagsProvider {

	private final TagsProviderHelper<Block> helper;

	public CommonBlockTagsProvider(PackOutput packOutput, CompletableFuture<Provider> provider, ExistingFileHelper existingFileHelper) {
		super(packOutput, provider, FruitsMod.ID, existingFileHelper);
		helper = new TagsProviderHelper<>(this);
	}

	static final TagKey<Block> CITRUS_LOGS = BlockTags.create(new ResourceLocation(FruitsMod.ID, "citrus_logs"));
	static final TagKey<Block> CHERRY_LOGS = BlockTags.create(new ResourceLocation(FruitsMod.ID, "cherry_logs"));

	@Override
	protected void addTags(Provider provider) {
		helper.getModEntries().forEach($ -> processTools($, false));

		helper.add(CITRUS_LOGS, CITRUS_LOG, CITRUS_WOOD, STRIPPED_CITRUS_LOG, STRIPPED_CITRUS_WOOD);
		helper.optional(CHERRY_LOGS, CHERRY_LOG, CHERRY_WOOD, STRIPPED_CHERRY_LOG, STRIPPED_CHERRY_WOOD);
		tag(LOGS_THAT_BURN).addTags(CITRUS_LOGS, CHERRY_LOGS);

		helper.add(Tags.Blocks.FENCE_GATES_WOODEN, CITRUS_FENCE_GATE);
		helper.optional(Tags.Blocks.FENCE_GATES_WOODEN, CHERRY_FENCE_GATE);

		helper.add(Tags.Blocks.FENCE_GATES, CITRUS_FENCE_GATE);
		helper.optional(Tags.Blocks.FENCE_GATES, CHERRY_FENCE_GATE);

		helper.add(WOODEN_FENCES, CITRUS_FENCE);
		helper.optional(WOODEN_FENCES, CHERRY_FENCE);

		helper.add(WOODEN_BUTTONS, CITRUS_BUTTON);
		helper.optional(WOODEN_BUTTONS, CHERRY_BUTTON);

		helper.add(WOODEN_SLABS, CITRUS_SLAB);
		helper.optional(WOODEN_SLABS, CHERRY_SLAB);

		helper.add(WOODEN_STAIRS, CITRUS_STAIRS);
		helper.optional(WOODEN_STAIRS, CHERRY_STAIRS);

		helper.add(WOODEN_PRESSURE_PLATES, CITRUS_PRESSURE_PLATE);
		helper.optional(WOODEN_PRESSURE_PLATES, CHERRY_PRESSURE_PLATE);

		helper.add(WOODEN_TRAPDOORS, CITRUS_TRAPDOOR);
		helper.optional(WOODEN_TRAPDOORS, CHERRY_TRAPDOOR);

		helper.add(PLANKS, CITRUS_PLANKS);
		helper.optional(PLANKS, CHERRY_PLANKS);

		helper.add(WALL_SIGNS, CITRUS_WALL_SIGN);
		helper.optional(WALL_SIGNS, CHERRY_WALL_SIGN);

		helper.add(STANDING_SIGNS, CITRUS_SIGN);
		helper.optional(STANDING_SIGNS, CHERRY_SIGN);

		helper.add(WOODEN_DOORS, CITRUS_DOOR);
		helper.optional(WOODEN_DOORS, CHERRY_DOOR, CHERRY_SLIDING_DOOR);

		helper.add(FLOWER_POTS, POTTED_APPLE, POTTED_CITRON, POTTED_GRAPEFRUIT, POTTED_LEMON, POTTED_LIME, POTTED_MANDARIN, POTTED_ORANGE, POTTED_POMELO);
		helper.optional(FLOWER_POTS, POTTED_CHERRY, POTTED_REDLOVE);

		helper.add(SAPLINGS, APPLE_SAPLING, CITRON_SAPLING, GRAPEFRUIT_SAPLING, LEMON_SAPLING, LIME_SAPLING, MANDARIN_SAPLING, ORANGE_SAPLING, POMELO_SAPLING);
		helper.optional(SAPLINGS, CHERRY_SAPLING, REDLOVE_SAPLING);

		helper.add(ALL_LEAVES, APPLE_LEAVES, CITRON_LEAVES, GRAPEFRUIT_LEAVES, LEMON_LEAVES, LIME_LEAVES, MANDARIN_LEAVES, ORANGE_LEAVES, POMELO_LEAVES);
		helper.optional(ALL_LEAVES, CHERRY_LEAVES, REDLOVE_LEAVES);

		tag(LEAVES).addTag(ALL_LEAVES);
	}

}
