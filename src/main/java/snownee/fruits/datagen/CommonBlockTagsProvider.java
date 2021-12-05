package snownee.fruits.datagen;

import static net.minecraft.tags.BlockTags.*;
import static snownee.fruits.CoreModule.*;
import static snownee.fruits.cherry.CherryModule.*;
import static snownee.kiwi.data.provider.TagsProviderHelper.*;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import snownee.fruits.FruitsMod;
import snownee.kiwi.data.provider.KiwiBlockTagsProvider;

public class CommonBlockTagsProvider extends KiwiBlockTagsProvider {

	public CommonBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, FruitsMod.MODID, existingFileHelper);
	}

	static final Tag.Named<Block> CITRUS_LOGS = createOptional(new ResourceLocation(FruitsMod.MODID, "cherry_logs"));
	static final Tag.Named<Block> CHERRY_LOGS = createOptional(new ResourceLocation(FruitsMod.MODID, "citrus_logs"));

	@Override
	protected void addTags() {
		getModEntries(modId, registry).forEach($ -> processTools($, false));

		tag(CITRUS_LOGS).add(CITRUS_LOG, CITRUS_WOOD, STRIPPED_CITRUS_LOG, STRIPPED_CITRUS_WOOD);
		addOptional(tag(CHERRY_LOGS), CHERRY_LOG, CHERRY_WOOD, STRIPPED_CHERRY_LOG, STRIPPED_CHERRY_WOOD);
		tag(LOGS_THAT_BURN).addTags(CITRUS_LOGS, CHERRY_LOGS);

		addOptional(tag(Tags.Blocks.FENCE_GATES_WOODEN), CHERRY_FENCE_GATE).add(CITRUS_FENCE_GATE);
		addOptional(tag(FENCE_GATES), CHERRY_FENCE_GATE).add(CITRUS_FENCE_GATE);
		addOptional(tag(WOODEN_FENCES), CHERRY_FENCE).add(CITRUS_FENCE);
		addOptional(tag(WOODEN_BUTTONS), CHERRY_BUTTON).add(CITRUS_BUTTON);
		addOptional(tag(WOODEN_SLABS), CHERRY_SLAB).add(CITRUS_SLAB);
		addOptional(tag(WOODEN_STAIRS), CHERRY_STAIRS).add(CITRUS_STAIRS);
		addOptional(tag(WOODEN_PRESSURE_PLATES), CHERRY_STAIRS).add(CITRUS_STAIRS);
		addOptional(tag(WOODEN_TRAPDOORS), CHERRY_TRAPDOOR).add(CITRUS_TRAPDOOR);
		addOptional(tag(PLANKS), CHERRY_PLANKS).add(CITRUS_PLANKS);
		addOptional(tag(WALL_SIGNS), CHERRY_WALL_SIGN).add(CITRUS_WALL_SIGN);
		addOptional(tag(STANDING_SIGNS), CHERRY_SIGN).add(CITRUS_SIGN);
		addOptional(tag(WOODEN_DOORS), CHERRY_DOOR, CHERRY_SLIDING_DOOR).add(CITRUS_DOOR);

		addOptional(tag(FLOWER_POTS), POTTED_CHERRY, POTTED_REDLOVE).add(POTTED_APPLE, POTTED_CITRON, POTTED_GRAPEFRUIT, POTTED_LEMON, POTTED_LIME, POTTED_MANDARIN, POTTED_ORANGE, POTTED_POMELO);
		addOptional(tag(SAPLINGS), CHERRY_SAPLING, REDLOVE_SAPLING).add(APPLE_SAPLING, CITRON_SAPLING, GRAPEFRUIT_SAPLING, LEMON_SAPLING, LIME_SAPLING, MANDARIN_SAPLING, ORANGE_SAPLING, POMELO_SAPLING);
		addOptional(tag(LEAVES), CHERRY_LEAVES, REDLOVE_LEAVES).add(APPLE_LEAVES, CITRON_LEAVES, GRAPEFRUIT_LEAVES, LEMON_LEAVES, LIME_LEAVES, MANDARIN_LEAVES, ORANGE_LEAVES, POMELO_LEAVES);
	}

}
