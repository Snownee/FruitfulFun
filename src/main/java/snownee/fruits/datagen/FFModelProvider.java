package snownee.fruits.datagen;

import java.util.List;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.food.FoodModule;
import snownee.fruits.pomegranate.PomegranateModule;

public class FFModelProvider extends FabricModelProvider {
	public FFModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		createCitrusLeaves(generators, CoreModule.TANGERINE_LEAVES.get(), FruitScale.SMALL);
		generators.createPlant(
				CoreModule.TANGERINE_SAPLING.get(), CoreModule.POTTED_TANGERINE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, CoreModule.CITRON_LEAVES.get(), FruitScale.SMALL);
		generators.createPlant(CoreModule.CITRON_SAPLING.get(), CoreModule.POTTED_CITRON.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, CoreModule.GRAPEFRUIT_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlant(
				CoreModule.GRAPEFRUIT_SAPLING.get(), CoreModule.POTTED_GRAPEFRUIT.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, CoreModule.LEMON_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlant(CoreModule.LEMON_SAPLING.get(), CoreModule.POTTED_LEMON.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, CoreModule.LIME_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlant(CoreModule.LIME_SAPLING.get(), CoreModule.POTTED_LIME.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, CoreModule.ORANGE_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlant(CoreModule.ORANGE_SAPLING.get(), CoreModule.POTTED_ORANGE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, CoreModule.APPLE_LEAVES.get(), FruitScale.MIDDLE);
		generators.createPlant(CoreModule.APPLE_SAPLING.get(), CoreModule.POTTED_APPLE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, CoreModule.POMELO_LEAVES.get(), FruitScale.LARGE);
		generators.createPlant(CoreModule.POMELO_SAPLING.get(), CoreModule.POTTED_POMELO.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createCitrusLeaves(generators, PomegranateModule.POMEGRANATE_LEAVES.get(), FruitScale.NONE);
		generators.createPlant(
				PomegranateModule.POMEGRANATE_SAPLING.get(), PomegranateModule.POTTED_POMEGRANATE.get(),
				BlockModelGenerators.TintState.NOT_TINTED);
		createRedloveLeaves(generators, CherryModule.REDLOVE_LEAVES.get());
		generators.createPlant(
				CherryModule.REDLOVE_SAPLING.get(), CherryModule.POTTED_REDLOVE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		createRedloveLeaves(generators, CherryModule.CHERRY_LEAVES.get());
		generators.createPlant(
				CherryModule.CHERRY_SAPLING.get(), CherryModule.POTTED_CHERRY.get(), BlockModelGenerators.TintState.NOT_TINTED);
		generators.createFlowerBed(CherryModule.PEACH_PINK_PETALS.get());
		generators.createSimpleFlatItemModel(CherryModule.CHERRY_CROWN.get());
		generators.createSimpleFlatItemModel(CherryModule.REDLOVE_CROWN.get());
		generators.createHangingSign(
				CoreModule.STRIPPED_CITRUS_LOG.get(), CoreModule.CITRUS_HANGING_SIGN.get(), CoreModule.CITRUS_WALL_HANGING_SIGN.get());
		generators.createHangingSign(
				CherryModule.STRIPPED_REDLOVE_LOG.get(), CherryModule.REDLOVE_HANGING_SIGN.get(),
				CherryModule.REDLOVE_WALL_HANGING_SIGN.get());
		generators.createSimpleFlatItemModel(FoodModule.CHORUS_FRUIT_PIE.get().asItem());
		generators.createSimpleFlatItemModel(FoodModule.CHORUS_FRUIT_PIE_SLICE.get());
		generators.createSimpleFlatItemModel(PomegranateModule.POMEGRANATE_ITEM.get());
	}

	public static void createCitrusLeaves(BlockModelGenerators generators, FruitLeavesBlock block, FruitScale scale) {
		FruitType fruitType = block.type.get();
		ResourceLocation typeId = FFRegistries.FRUIT_TYPE.getKey(fruitType);
		ResourceLocation model01;
		if (CoreModule.APPLE_LEAVES.is(block)) {
			model01 = ModelLocationUtils.getModelLocation(Blocks.OAK_LEAVES);
		} else {
			model01 = TexturedModel.LEAVES.create(block, generators.modelOutput);
		}
		ResourceLocation flowersTexture = tex("%s_flowers".formatted(typeId.getPath()));
		ResourceLocation model2 = FFModelTemplates.FLOWERING_LEAVES.create(block, new TextureMapping()
						.put(FFModelTemplates.FLOWERS, flowersTexture),
				generators.modelOutput);
		MultiPartGenerator generator = MultiPartGenerator.multiPart(block)
				.with(Variant.variant()
						.with(VariantProperties.MODEL, model01)
						.with(VariantProperties.UV_LOCK, true))
				.with(Condition.condition().term(FruitLeavesBlock.AGE, 2), Variant.variant()
						.with(VariantProperties.MODEL, model2));
		if (scale != FruitScale.NONE) {
			List<Variant> variants = Lists.newArrayList(Variant.variant().with(VariantProperties.MODEL, scale.model));
			if (scale.randomRotation) {
				variants.add(Variant.variant()
						.with(VariantProperties.MODEL, scale.model)
						.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
				variants.add(Variant.variant()
						.with(VariantProperties.MODEL, scale.model)
						.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
			}
			generator.with(Condition.condition().term(FruitLeavesBlock.AGE, 3), variants);
		}
		generators.blockStateOutput.accept(generator);
		ResourceLocation baseTexture = TextureMapping.getBlockTexture(CoreModule.APPLE_LEAVES.is(block) ? Blocks.OAK_LEAVES : block);
		FFModelTemplates.FLOWERING_INVENTORY.create(
				ModelLocationUtils.getModelLocation(block.asItem()),
				new TextureMapping().put(FFModelTemplates.LEAVES, baseTexture).put(FFModelTemplates.FLOWERS, flowersTexture),
				generators.modelOutput);
	}

	public static void createRedloveLeaves(BlockModelGenerators generators, FruitLeavesBlock block) {
		ResourceLocation model012;
		if (CherryModule.CHERRY_LEAVES.is(block)) {
			model012 = ModelLocationUtils.getModelLocation(Blocks.CHERRY_LEAVES);
		} else {
			model012 = TexturedModel.LEAVES.create(block, generators.modelOutput);
		}
		ResourceLocation model3 = ModelTemplates.LEAVES.createWithSuffix(
				block, "_2", TextureMapping.cube(TextureMapping.getBlockTexture(block, "_2")), generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.property(FruitLeavesBlock.AGE).generateList(age -> {
					if (age < 3) {
						return List.of(Variant.variant().with(VariantProperties.MODEL, model012));
					}
					if (age == 3) {
						return List.of(Variant.variant().with(VariantProperties.MODEL, model3));
					}
					throw new IllegalStateException("Unexpected value: " + age);
				}));
		generators.blockStateOutput.accept(generator);
		generators.delegateItemModel(block, model012);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {

	}

	public static ResourceLocation tex(String path) {
		return new ResourceLocation(FruitfulFun.ID, "block/" + path);
	}

	public enum FruitScale {
		NONE(null, false),
		SMALL("template_leaves_fruit_sm", true),
		MIDDLE("template_leaves_fruit_md", true),
		LARGE("template_leaves_fruit_lg", false);

		public final ResourceLocation model;
		public final boolean randomRotation;

		FruitScale(String model, boolean randomRotation) {
			this.model = model == null ? null : new ResourceLocation(FruitfulFun.ID, "block/" + model);
			this.randomRotation = randomRotation;
		}
	}
}
