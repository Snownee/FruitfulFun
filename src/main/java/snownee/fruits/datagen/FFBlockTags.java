package snownee.fruits.datagen;

import static snownee.fruits.CoreModule.ALL_LEAVES;
import static snownee.fruits.CoreModule.APPLE_LEAVES;
import static snownee.fruits.CoreModule.CITRUS_BUTTON;
import static snownee.fruits.CoreModule.CITRUS_DOOR;
import static snownee.fruits.CoreModule.CITRUS_FENCE;
import static snownee.fruits.CoreModule.CITRUS_FENCE_GATE;
import static snownee.fruits.CoreModule.CITRUS_HANGING_SIGN;
import static snownee.fruits.CoreModule.CITRUS_LOG;
import static snownee.fruits.CoreModule.CITRUS_PLANKS;
import static snownee.fruits.CoreModule.CITRUS_PRESSURE_PLATE;
import static snownee.fruits.CoreModule.CITRUS_SHELF;
import static snownee.fruits.CoreModule.CITRUS_SIGN;
import static snownee.fruits.CoreModule.CITRUS_SLAB;
import static snownee.fruits.CoreModule.CITRUS_STAIRS;
import static snownee.fruits.CoreModule.CITRUS_TRAPDOOR;
import static snownee.fruits.CoreModule.CITRUS_WALL_HANGING_SIGN;
import static snownee.fruits.CoreModule.CITRUS_WALL_SIGN;
import static snownee.fruits.CoreModule.CITRUS_WOOD;
import static snownee.fruits.CoreModule.POTTED_APPLE;
import static snownee.fruits.CoreModule.POTTED_CITRON;
import static snownee.fruits.CoreModule.POTTED_GRAPEFRUIT;
import static snownee.fruits.CoreModule.POTTED_LEMON;
import static snownee.fruits.CoreModule.POTTED_LIME;
import static snownee.fruits.CoreModule.POTTED_ORANGE;
import static snownee.fruits.CoreModule.POTTED_POMELO;
import static snownee.fruits.CoreModule.POTTED_TANGERINE;
import static snownee.fruits.CoreModule.STRIPPED_CITRUS_LOG;
import static snownee.fruits.CoreModule.STRIPPED_CITRUS_WOOD;
import static snownee.fruits.cherry.CherryModule.CHERRY_SAPLING;
import static snownee.fruits.cherry.CherryModule.PEACH_PINK_PETALS;
import static snownee.fruits.cherry.CherryModule.POTTED_CHERRY;
import static snownee.fruits.cherry.CherryModule.POTTED_REDLOVE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_BUTTON;
import static snownee.fruits.cherry.CherryModule.REDLOVE_DOOR;
import static snownee.fruits.cherry.CherryModule.REDLOVE_FENCE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_FENCE_GATE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_HANGING_SIGN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LEAVES;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LOG;
import static snownee.fruits.cherry.CherryModule.REDLOVE_PLANKS;
import static snownee.fruits.cherry.CherryModule.REDLOVE_PRESSURE_PLATE;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SHELF;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SIGN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SLAB;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SLIDING_DOOR;
import static snownee.fruits.cherry.CherryModule.REDLOVE_STAIRS;
import static snownee.fruits.cherry.CherryModule.REDLOVE_TRAPDOOR;
import static snownee.fruits.cherry.CherryModule.REDLOVE_WALL_HANGING_SIGN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_WALL_SIGN;
import static snownee.fruits.cherry.CherryModule.REDLOVE_WOOD;
import static snownee.fruits.cherry.CherryModule.STRIPPED_REDLOVE_LOG;
import static snownee.fruits.cherry.CherryModule.STRIPPED_REDLOVE_WOOD;
import static snownee.fruits.compat.farmersdelight.FarmersDelightModule.CITRUS_CABINET;
import static snownee.fruits.compat.farmersdelight.FarmersDelightModule.REDLOVE_CABINET;
import static snownee.fruits.pomegranate.PomegranateModule.POMEGRANATE;
import static snownee.fruits.pomegranate.PomegranateModule.POMEGRANATE_LEAVES;
import static snownee.fruits.pomegranate.PomegranateModule.POTTED_POMEGRANATE;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.scent.ScentedCandleBlock;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModules;

public class FFBlockTags extends FabricTagsProvider.BlockTagsProvider {
	static final TagKey<Block> CITRUS_LOGS = AbstractModule.blockTag(FruitfulFun.ID, "citrus_logs");
	static final TagKey<Block> REDLOVE_LOGS = AbstractModule.blockTag(FruitfulFun.ID, "redlove_logs");
	// Leaves us in Peace mod compatibility
	static final TagKey<Block> TREE_TYPES_OAK_LOG = AbstractModule.blockTag("minecraft", "tree_types/oak_log");
	static final TagKey<Block> TREE_TYPES_JUNGLE_LOG = AbstractModule.blockTag("minecraft", "tree_types/jungle_log");
	static final TagKey<Block> MINEABLE_WITH_KNIFE = AbstractModule.blockTag("farmersdelight", "mineable/knife");

	public FFBlockTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		valueLookupBuilder(CITRUS_LOGS).add(
				CITRUS_LOG.get(),
				CITRUS_WOOD.get(),
				STRIPPED_CITRUS_LOG.get(),
				STRIPPED_CITRUS_WOOD.get());
		valueLookupBuilder(REDLOVE_LOGS).add(
				REDLOVE_LOG.get(),
				REDLOVE_WOOD.get(),
				STRIPPED_REDLOVE_LOG.get(),
				STRIPPED_REDLOVE_WOOD.get());
		valueLookupBuilder(BlockTags.LOGS_THAT_BURN).addTag(CITRUS_LOGS).addTag(REDLOVE_LOGS);
		valueLookupBuilder(ConventionalBlockTags.OVERWORLD_NATURAL_LOGS).add(CITRUS_LOG.get());
		valueLookupBuilder(ConventionalBlockTags.NATURAL_WOODS).add(CITRUS_WOOD.get());
		valueLookupBuilder(ConventionalBlockTags.STRIPPED_LOGS).add(STRIPPED_CITRUS_LOG.get(), STRIPPED_REDLOVE_LOG.get());
		valueLookupBuilder(ConventionalBlockTags.STRIPPED_WOODS).add(STRIPPED_CITRUS_WOOD.get(), STRIPPED_REDLOVE_WOOD.get());
		valueLookupBuilder(BlockTags.WOODEN_FENCES).add(CITRUS_FENCE.get(), REDLOVE_FENCE.get());
		valueLookupBuilder(BlockTags.FENCE_GATES).add(CITRUS_FENCE_GATE.get(), REDLOVE_FENCE_GATE.get());
		valueLookupBuilder(BlockTags.WOODEN_BUTTONS).add(CITRUS_BUTTON.get(), REDLOVE_BUTTON.get());
		valueLookupBuilder(BlockTags.WOODEN_SLABS).add(CITRUS_SLAB.get(), REDLOVE_SLAB.get());
		valueLookupBuilder(BlockTags.WOODEN_STAIRS).add(CITRUS_STAIRS.get(), REDLOVE_STAIRS.get());
		valueLookupBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(CITRUS_PRESSURE_PLATE.get(), REDLOVE_PRESSURE_PLATE.get());
		valueLookupBuilder(BlockTags.WOODEN_TRAPDOORS).add(CITRUS_TRAPDOOR.get(), REDLOVE_TRAPDOOR.get());
		valueLookupBuilder(BlockTags.PLANKS).add(CITRUS_PLANKS.get(), REDLOVE_PLANKS.get());
		valueLookupBuilder(BlockTags.WALL_SIGNS).add(CITRUS_WALL_SIGN.get(), REDLOVE_WALL_SIGN.get());
		valueLookupBuilder(BlockTags.STANDING_SIGNS).add(CITRUS_SIGN.get(), REDLOVE_SIGN.get());
		valueLookupBuilder(BlockTags.WALL_HANGING_SIGNS).add(CITRUS_WALL_HANGING_SIGN.get(), REDLOVE_WALL_HANGING_SIGN.get());
		valueLookupBuilder(BlockTags.CEILING_HANGING_SIGNS).add(CITRUS_HANGING_SIGN.get(), REDLOVE_HANGING_SIGN.get());
		valueLookupBuilder(BlockTags.WOODEN_DOORS).add(CITRUS_DOOR.get(), REDLOVE_DOOR.get(), REDLOVE_SLIDING_DOOR.get());
		valueLookupBuilder(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED).add(REDLOVE_SLIDING_DOOR.get());
		valueLookupBuilder(BlockTags.FLOWER_POTS).add(
				POTTED_APPLE.get(),
				POTTED_CITRON.get(),
				POTTED_GRAPEFRUIT.get(),
				POTTED_LEMON.get(),
				POTTED_LIME.get(),
				POTTED_TANGERINE.get(),
				POTTED_ORANGE.get(),
				POTTED_POMELO.get(),
				POTTED_REDLOVE.get(),
				POTTED_CHERRY.get(),
				POTTED_POMEGRANATE.get());
		{
			var builder = valueLookupBuilder(BlockTags.SAPLINGS);
			FFRegistries.FRUIT_TYPE.forEach($ -> builder.add($.sapling.get()));
		}
		{
			var builder = valueLookupBuilder(ALL_LEAVES);
			FFRegistries.FRUIT_TYPE.forEach($ -> builder.add($.leaves.get()));
		}
		valueLookupBuilder(BlockTags.LEAVES).addTag(ALL_LEAVES);
		valueLookupBuilder(BlockTags.MINEABLE_WITH_HOE).addTag(ALL_LEAVES);
		valueLookupBuilder(BlockTags.FLOWERS).add(
				REDLOVE_LEAVES.get(),
				CHERRY_SAPLING.get(),
				PEACH_PINK_PETALS.get(),
				Blocks.SPORE_BLOSSOM);
		valueLookupBuilder(BlockTags.INSIDE_STEP_SOUND_BLOCKS).add(PEACH_PINK_PETALS.get());

		{
			var builder = getOrCreateRawBuilder(CoreModule.CANDLES);
			for (Block block : BuiltInRegistries.BLOCK) {
				if (block instanceof ScentedCandleBlock) {
					builder.addOptionalElement(BuiltInRegistries.BLOCK.getKey(block));
				}
			}
			builder
					.addOptionalTag(BlockTags.CANDLES.location())
					.addOptionalTag(BlockTags.CANDLE_CAKES.location())
					.addOptionalTag(Identifier.parse("supplementaries:candle_holders"))
					.addOptionalTag(Identifier.parse("the_bumblezone:candles"));
		}
		valueLookupBuilder(GadgetModule.SUSTAIN_CRAFTER_ITEM)
				.addTag(CoreModule.CANDLES);
		valueLookupBuilder(GadgetModule.VCD_PERFORM_USING)
				.addTag(ALL_LEAVES)
				.addOptionalTag(BlockTags.CAVE_VINES)
				.add(Blocks.SWEET_BERRY_BUSH);
		valueLookupBuilder(GadgetModule.VCD_PERFORM_BREAKING)
				.add(Blocks.COCOA)
				.add(POMEGRANATE.get());
		valueLookupBuilder(TREE_TYPES_OAK_LOG).add(APPLE_LEAVES.get());
		valueLookupBuilder(TREE_TYPES_JUNGLE_LOG).add(POMEGRANATE_LEAVES.get());
		valueLookupBuilder(BlockTags.BEEHIVES).addOptional(GadgetModule.BUZZY_CRAFTER.get());
		valueLookupBuilder(BlockTags.WOODEN_SHELVES).add(CITRUS_SHELF.get()).add(REDLOVE_SHELF.get());

		if (Hooks.farmersdelight) {
			getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_AXE)
					.addOptionalElement(CITRUS_CABINET.key())
					.addOptionalElement(REDLOVE_CABINET.key());
		}

		var builder = valueLookupBuilder(MINEABLE_WITH_KNIFE);
		KiwiModules.get(FruitfulFun.id("food")).getRegistryEntries(Registries.BLOCK)
				.map(KiwiGO::get)
				.forEach(builder::addOptional);

		valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
				.addOptional(GadgetModule.BUZZY_CRAFTER.get())
				.addOptional(GadgetModule.RAIN_DETECTOR.get());

		valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
				.addOptional(GadgetModule.BREWER.get());
	}
}
