package snownee.fruits.datagen;

import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.mojang.math.Quadrant;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.MutagenTintSource;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.client.Head;
import snownee.fruits.food.FoodModule;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.scent.ScentedCandleBlock;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.kiwi.ItemObject;

public class FFModels extends FabricModelProvider {
	private @Nullable ItemModelGenerators itemGenerators;

	public FFModels(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		generators.family(CoreModule.CITRUS_PLANKS.get()).generateFor(FFRecipes.CITRUS_FAMILY);
		generators.family(CherryModule.REDLOVE_PLANKS.get()).generateFor(FFRecipes.REDLOVE_FAMILY);
		generators.woodProvider(CoreModule.CITRUS_LOG.get())
				.logWithHorizontal(CoreModule.CITRUS_LOG.get())
				.wood(CoreModule.CITRUS_WOOD.get());
		generators.woodProvider(CoreModule.STRIPPED_CITRUS_LOG.get()).logWithHorizontal(CoreModule.STRIPPED_CITRUS_LOG.get()).wood(
				CoreModule.STRIPPED_CITRUS_WOOD.get());
		generators.woodProvider(CherryModule.REDLOVE_LOG.get())
				.logWithHorizontal(CherryModule.REDLOVE_LOG.get())
				.wood(CherryModule.REDLOVE_WOOD.get());
		generators.woodProvider(CherryModule.STRIPPED_REDLOVE_LOG.get()).logWithHorizontal(CherryModule.STRIPPED_REDLOVE_LOG.get()).wood(
				CherryModule.STRIPPED_REDLOVE_WOOD.get());
		generators.createHangingSign(
				CoreModule.STRIPPED_CITRUS_LOG.get(),
				CoreModule.CITRUS_HANGING_SIGN.get(),
				CoreModule.CITRUS_WALL_HANGING_SIGN.get());
		generators.createHangingSign(
				CherryModule.STRIPPED_REDLOVE_LOG.get(),
				CherryModule.REDLOVE_HANGING_SIGN.get(),
				CherryModule.REDLOVE_WALL_HANGING_SIGN.get());
		generators.createShelf(CoreModule.CITRUS_SHELF.get(), CoreModule.STRIPPED_CITRUS_LOG.get());
		generators.createShelf(CherryModule.REDLOVE_SHELF.get(), CherryModule.STRIPPED_REDLOVE_LOG.get());

		createFruitLeaves(generators, CoreModule.TANGERINE_LEAVES.get(), FruitScale.SMALL);
		generators.createPlantWithDefaultItem(
				CoreModule.TANGERINE_SAPLING.get(),
				CoreModule.POTTED_TANGERINE.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, CoreModule.CITRON_LEAVES.get(), FruitScale.SMALL);
		generators.createPlantWithDefaultItem(
				CoreModule.CITRON_SAPLING.get(),
				CoreModule.POTTED_CITRON.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, CoreModule.GRAPEFRUIT_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlantWithDefaultItem(
				CoreModule.GRAPEFRUIT_SAPLING.get(),
				CoreModule.POTTED_GRAPEFRUIT.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, CoreModule.LEMON_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlantWithDefaultItem(
				CoreModule.LEMON_SAPLING.get(),
				CoreModule.POTTED_LEMON.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, CoreModule.LIME_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlantWithDefaultItem(
				CoreModule.LIME_SAPLING.get(),
				CoreModule.POTTED_LIME.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, CoreModule.ORANGE_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlantWithDefaultItem(
				CoreModule.ORANGE_SAPLING.get(),
				CoreModule.POTTED_ORANGE.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, CoreModule.APPLE_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlantWithDefaultItem(
				CoreModule.APPLE_SAPLING.get(),
				CoreModule.POTTED_APPLE.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, CoreModule.POMELO_LEAVES.get(), FruitScale.LARGE);
		generators.createPlantWithDefaultItem(
				CoreModule.POMELO_SAPLING.get(),
				CoreModule.POTTED_POMELO.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createFruitLeaves(generators, PomegranateModule.POMEGRANATE_LEAVES.get(), FruitScale.NONE);
		generators.createPlantWithDefaultItem(
				PomegranateModule.POMEGRANATE_SAPLING.get(),
				PomegranateModule.POTTED_POMEGRANATE.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createRedloveLeaves(generators, CherryModule.REDLOVE_LEAVES.get());
		generators.createPlantWithDefaultItem(
				CherryModule.REDLOVE_SAPLING.get(),
				CherryModule.POTTED_REDLOVE.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		createRedloveLeaves(generators, CherryModule.CHERRY_LEAVES.get());
		generators.createPlantWithDefaultItem(
				CherryModule.CHERRY_SAPLING.get(),
				CherryModule.POTTED_CHERRY.get(),
				BlockModelGenerators.PlantType.NOT_TINTED);
		generators.createFlowerBed(CherryModule.PEACH_PINK_PETALS.get());
		generators.registerSimpleFlatItemModel(FoodModule.CHORUS_FRUIT_PIE.asItem());
		generators.registerSimpleFlatItemModel(FoodModule.LEMON_ROAST_CHICKEN_BLOCK.asItem());
		generators.registerSimpleFlatItemModel(FoodModule.DONAUWELLE.asItem());
		generators.registerSimpleFlatItemModel(FoodModule.GRAPEFRUIT_PANNA_COTTA.asItem());
		generators.registerSimpleFlatItemModel(FoodModule.RICE_WITH_FRUITS.asItem());
		generators.registerSimpleFlatItemModel(FoodModule.HONEY_POMELO_TEA.asItem());

		createCandle(generators, GadgetModule.PHANTOM_CANDLE.get());
		createCandle(generators, GadgetModule.WANDERING_TRADER_CANDLE.get());
		createCandle(generators, GadgetModule.ENDER_CANDLE.get());
		createCandle(generators, GadgetModule.WEAK_CANDLE.get());
		createCandle(generators, GadgetModule.PEACE_CANDLE.get());
//		createCandle(generators, GadgetModule.HEAVY_CANDLE.get());
		generators.createHorizontallyRotatedBlock(GadgetModule.BUZZY_CRAFTER.get(), TexturedModel.ORIENTABLE);
		createSlidingDoor(generators, CherryModule.REDLOVE_SLIDING_DOOR.get());
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
				GadgetModule.RAIN_DETECTOR.get(),
				plainVariant(ModelLocationUtils.getModelLocation(GadgetModule.RAIN_DETECTOR.get()))));
		generators.registerSimpleItemModel(
				GadgetModule.RAIN_DETECTOR.get(),
				ModelLocationUtils.getModelLocation(GadgetModule.RAIN_DETECTOR.get()));
		createBrewer(generators);
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
		itemGenerators = generators;
		Identifier model = new ModelTemplate(Optional.of(Identifier.parse("item/potion")), Optional.empty()).create(
				BeeModule.MUTAGEN.get(),
				new TextureMapping(),
				generators.modelOutput);
		generators.itemModelOutput.accept(BeeModule.MUTAGEN.get(), ItemModelUtils.tintedModel(model, MutagenTintSource.INSTANCE));
		buzzyShield(GadgetModule.BUZZY_SHIELD.get());
		inspector(BeeModule.INSPECTOR.get());

		flat(CoreModule.TANGERINE);
		flat(CoreModule.LIME);
		flat(CoreModule.CITRON);
		flat(CoreModule.POMELO);
		flat(CoreModule.ORANGE);
		flat(CoreModule.LEMON);
		flat(CoreModule.GRAPEFRUIT);
		flat(CherryModule.CHERRY);
		flat(CherryModule.REDLOVE);
		flat(PomegranateModule.POMEGRANATE_ITEM);
		generators.itemModelOutput.copy(PomegranateModule.POMEGRANATE_ITEM.get(), PomegranateModule.ENCHANTED_POMEGRANATE.get());
		flat(CoreModule.SNOWFLAKE_BANNER_PATTERN);
		flat(CherryModule.HEART_BANNER_PATTERN);
		flat(FoodModule.CHORUS_FRUIT_PIE_SLICE);
		flat(FoodModule.LEMON_ROAST_CHICKEN);
		flat(CoreModule.CITRUS_BOAT);
		flat(CoreModule.CITRUS_CHEST_BOAT);
		flat(CherryModule.REDLOVE_BOAT);
		flat(CherryModule.REDLOVE_CHEST_BOAT);

		flowerCrown(CherryModule.CHERRY_CROWN);
		flowerCrown(CherryModule.REDLOVE_CROWN);
	}

	private void createBrewer(BlockModelGenerators generators) {
		generators.registerSimpleFlatItemModel(GadgetModule.BREWER.asItem());
		generators.blockStateOutput
				.accept(
						MultiPartGenerator.multiPart(GadgetModule.BREWER.get())
								.with(plainVariant(ModelLocationUtils.getModelLocation(GadgetModule.BREWER.get())))
								.with(
										condition().term(BlockStateProperties.HAS_BOTTLE_0, true),
										plainVariant(ModelLocationUtils.getModelLocation(Blocks.BREWING_STAND, "_bottle0")))
								.with(
										condition().term(BlockStateProperties.HAS_BOTTLE_1, true),
										plainVariant(ModelLocationUtils.getModelLocation(Blocks.BREWING_STAND, "_bottle1")))
								.with(
										condition().term(BlockStateProperties.HAS_BOTTLE_2, true),
										plainVariant(ModelLocationUtils.getModelLocation(Blocks.BREWING_STAND, "_bottle2")))
								.with(
										condition().term(BlockStateProperties.HAS_BOTTLE_0, false),
										plainVariant(ModelLocationUtils.getModelLocation(Blocks.BREWING_STAND, "_empty0")))
								.with(
										condition().term(BlockStateProperties.HAS_BOTTLE_1, false),
										plainVariant(ModelLocationUtils.getModelLocation(Blocks.BREWING_STAND, "_empty1")))
								.with(
										condition().term(BlockStateProperties.HAS_BOTTLE_2, false),
										plainVariant(ModelLocationUtils.getModelLocation(Blocks.BREWING_STAND, "_empty2")))
				);
	}

	private void inspector(Item item) {
		Objects.requireNonNull(itemGenerators);
		Identifier id = ModelLocationUtils.getModelLocation(item);
		ItemModel.Unbaked trueModel = ItemModelUtils.plainModel(id.withSuffix("_using"));
		ItemModel.Unbaked falseModel = ItemModelUtils.plainModel(id);
		itemGenerators.itemModelOutput.accept(item, ItemModelUtils.conditional(new IsUsingItem(), trueModel, falseModel));
	}

	private void flowerCrown(ItemObject<?> item) {
		Objects.requireNonNull(itemGenerators).itemModelOutput.accept(
				item.get(), ItemModelUtils.conditional(
						new Head(),
						ItemModelUtils.plainModel(item.key().withPrefix("block/")),
						ItemModelUtils.plainModel(itemGenerators.createFlatItemModel(item.get(), ModelTemplates.FLAT_ITEM))));
	}

	private void flat(ItemLike item) {
		Objects.requireNonNull(itemGenerators).generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
	}

	public static void createCandle(BlockModelGenerators generators, ScentedCandleBlock block) {
		generators.registerSimpleFlatItemModel(block.asItem());
		TextureMapping textureMapping = TextureMapping.cube(TextureMapping.getBlockTexture(block));
		TextureMapping textureMapping2 = TextureMapping.cube(TextureMapping.getBlockTexture(block, "_lit"));
		Identifier Identifier = ModelTemplates.CANDLE.createWithSuffix(block, "_one_candle", textureMapping, generators.modelOutput);
		Identifier resourceLocation2 = ModelTemplates.TWO_CANDLES.createWithSuffix(
				block,
				"_two_candles",
				textureMapping,
				generators.modelOutput);
		Identifier resourceLocation3 = ModelTemplates.THREE_CANDLES.createWithSuffix(
				block,
				"_three_candles",
				textureMapping,
				generators.modelOutput);
		Identifier resourceLocation4 = ModelTemplates.FOUR_CANDLES.createWithSuffix(
				block,
				"_four_candles",
				textureMapping,
				generators.modelOutput);
		Identifier resourceLocation5 = ModelTemplates.CANDLE.createWithSuffix(
				block,
				"_one_candle_lit",
				textureMapping2,
				generators.modelOutput);
		Identifier resourceLocation6 = ModelTemplates.TWO_CANDLES.createWithSuffix(
				block,
				"_two_candles_lit",
				textureMapping2,
				generators.modelOutput);
		Identifier resourceLocation7 = ModelTemplates.THREE_CANDLES.createWithSuffix(
				block,
				"_three_candles_lit",
				textureMapping2,
				generators.modelOutput);
		Identifier resourceLocation8 = ModelTemplates.FOUR_CANDLES.createWithSuffix(
				block,
				"_four_candles_lit",
				textureMapping2,
				generators.modelOutput);
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(
						BlockStateProperties.CANDLES,
						BlockStateProperties.LIT)
				.select(1, false, plainVariant(Identifier))
				.select(2, false, plainVariant(resourceLocation2))
				.select(3, false, plainVariant(resourceLocation3))
				.select(4, false, plainVariant(resourceLocation4))
				.select(1, true, plainVariant(resourceLocation5))
				.select(2, true, plainVariant(resourceLocation6))
				.select(3, true, plainVariant(resourceLocation7))
				.select(4, true, plainVariant(resourceLocation8))));
	}

	public static void createFruitLeaves(BlockModelGenerators generators, FruitLeavesBlock block, FruitScale scale) {
		FruitType fruitType = block.type.value();
		Identifier typeId = Objects.requireNonNull(FFRegistries.FRUIT_TYPE.getKey(fruitType));
		Identifier model01;
		if (CoreModule.APPLE_LEAVES.is(block)) {
			model01 = ModelLocationUtils.getModelLocation(Blocks.OAK_LEAVES);
		} else {
			model01 = TexturedModel.LEAVES.create(block, generators.modelOutput);
		}
		Material flowersTexture = new Material(tex("%s_flowers".formatted(typeId.getPath())));
		Identifier model2 = FFModelTemplates.FLOWERING_LEAVES.create(
				block,
				new TextureMapping().put(FFModelTemplates.FLOWERS, flowersTexture),
				generators.modelOutput);
		MultiPartGenerator generator = MultiPartGenerator.multiPart(block).with(variant(plainModel(model01).withUvLock(true))).with(
				condition().term(FruitLeavesBlock.AGE, FruitLeavesBlock.BLOOMING),
				plainVariant(model2));
		if (scale.model != null) {
			WeightedList.Builder<Variant> variants = WeightedList.builder();
			variants.add(plainModel(scale.model));
			if (scale.randomRotation) {
				variants.add(plainModel(scale.model).withYRot(Quadrant.R90));
				variants.add(plainModel(scale.model).withYRot(Quadrant.R180));
			}
			generator.with(condition().term(FruitLeavesBlock.AGE, FruitLeavesBlock.FRUITING), new MultiVariant(variants.build()));
		}
		generators.blockStateOutput.accept(generator);
		Material baseTexture = TextureMapping.getBlockTexture(CoreModule.APPLE_LEAVES.is(block) ? Blocks.OAK_LEAVES : block);
		Identifier model = FFModelTemplates.FLOWERING_INVENTORY.create(
				ModelLocationUtils.getModelLocation(block.asItem()),
				new TextureMapping().put(FFModelTemplates.LEAVES, baseTexture).put(FFModelTemplates.FLOWERS, flowersTexture),
				generators.modelOutput);
		if (PomegranateModule.POMEGRANATE_LEAVES.is(block)) {
			generators.registerSimpleItemModel(block, model);
		} else {
			generators.registerSimpleTintedItemModel(block, model, ItemModelUtils.constantTint(-12012264));
		}
	}

	public static void createRedloveLeaves(BlockModelGenerators generators, FruitLeavesBlock block) {
		Identifier model012;
		if (CherryModule.CHERRY_LEAVES.is(block)) {
			model012 = ModelLocationUtils.getModelLocation(Blocks.CHERRY_LEAVES);
		} else {
			model012 = TexturedModel.LEAVES.create(block, generators.modelOutput);
		}
		Identifier model3 = ModelTemplates.LEAVES.createWithSuffix(
				block,
				"_2",
				TextureMapping.cube(TextureMapping.getBlockTexture(block, "_2")),
				generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(FruitLeavesBlock.AGE)
				.generate(age -> {
					if (age < 3) {
						return plainVariant(model012);
					}
					if (age == 3) {
						return plainVariant(model3);
					}
					throw new IllegalStateException("Unexpected value: " + age);
				}));
		generators.blockStateOutput.accept(generator);
		generators.registerSimpleItemModel(block, model012);
	}

	public void createSlidingDoor(BlockModelGenerators generators, Block door) {
		MultiVariant doorBottomLeft = plainVariant(ModelLocationUtils.getModelLocation(door, "_bottom_left"));
		MultiVariant doorBottomRight = plainVariant(ModelLocationUtils.getModelLocation(door, "_bottom_right"));
		MultiVariant doorTopLeft = plainVariant(ModelLocationUtils.getModelLocation(door, "_top_left"));
		MultiVariant doorTopRight = plainVariant(ModelLocationUtils.getModelLocation(door, "_top_right"));
		MultiVariant doorBottomOpen = plainVariant(ModelTemplates.PARTICLE_ONLY.createWithSuffix(
				door,
				"_bottom_open",
				new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(door, "_bottom")),
				generators.modelOutput));
		MultiVariant doorTopOpen = plainVariant(ModelTemplates.PARTICLE_ONLY.createWithSuffix(
				door,
				"_top_open",
				new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(door, "_top")),
				generators.modelOutput));
		generators.registerSimpleFlatItemModel(door.asItem());
		generators.blockStateOutput.accept(BlockModelGenerators.createDoor(
				door,
				doorBottomLeft,
				doorBottomOpen,
				doorBottomRight,
				doorBottomOpen,
				doorTopLeft,
				doorTopOpen,
				doorTopRight,
				doorTopOpen));
	}

	public final void buzzyShield(final Item item) {
		ItemModel.Unbaked normal = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
		ItemModel.Unbaked blocking = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_blocking"));
		Objects.requireNonNull(itemGenerators).itemModelOutput.accept(
				item,
				ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), blocking, normal));
	}

	public static Identifier tex(String path) {
		return FruitfulFun.id("block/" + path);
	}

	public enum FruitScale {
		NONE(null, false), SMALL("template_leaves_fruit_sm", true), MIDDLE("template_leaves_fruit_md", true), LARGE(
				"template_leaves_fruit_lg",
				false);

		public final @Nullable Identifier model;
		public final boolean randomRotation;

		FruitScale(@Nullable String model, boolean randomRotation) {
			this.model = model == null ? null : FruitfulFun.id("block/" + model);
			this.randomRotation = randomRotation;
		}
	}

	public static Variant plainModel(final Identifier model) {
		return BlockModelGenerators.plainModel(model);
	}

	public static MultiVariant plainVariant(final Identifier model) {
		return BlockModelGenerators.plainVariant(model);
	}

	public static MultiVariant variant(final Variant variant) {
		return BlockModelGenerators.variant(variant);
	}

	public static ConditionBuilder condition() {
		return new ConditionBuilder();
	}
}
