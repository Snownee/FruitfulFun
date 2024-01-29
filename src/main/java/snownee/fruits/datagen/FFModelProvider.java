package snownee.fruits.datagen;

import java.util.List;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
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
		createCitrusLeaves(generators, CoreModule.CITRON_LEAVES.get(), FruitScale.SMALL);
		createCitrusLeaves(generators, CoreModule.GRAPEFRUIT_LEAVES.get(), FruitScale.MIDDLE);
		createCitrusLeaves(generators, CoreModule.LEMON_LEAVES.get(), FruitScale.MIDDLE);
		createCitrusLeaves(generators, CoreModule.LIME_LEAVES.get(), FruitScale.MIDDLE);
		createCitrusLeaves(generators, CoreModule.ORANGE_LEAVES.get(), FruitScale.MIDDLE);
		createCitrusLeaves(generators, CoreModule.APPLE_LEAVES.get(), FruitScale.MIDDLE);
		createCitrusLeaves(generators, CoreModule.POMELO_LEAVES.get(), FruitScale.LARGE);
		createRedloveLeaves(generators, CherryModule.REDLOVE_LEAVES.get());
		createRedloveLeaves(generators, CherryModule.CHERRY_LEAVES.get());
		generators.createFlowerBed(CherryModule.PEACH_PINK_PETALS.get());
		generators.createSimpleFlatItemModel(CherryModule.CHERRY_CROWN.get());
		generators.createSimpleFlatItemModel(CherryModule.REDLOVE_CROWN.get());
		generators.createHangingSign(CoreModule.STRIPPED_CITRUS_LOG.get(), CoreModule.CITRUS_HANGING_SIGN.get(), CoreModule.CITRUS_WALL_HANGING_SIGN.get());
		generators.createHangingSign(CherryModule.STRIPPED_REDLOVE_LOG.get(), CherryModule.REDLOVE_HANGING_SIGN.get(), CherryModule.REDLOVE_WALL_HANGING_SIGN.get());
		generators.createSimpleFlatItemModel(FoodModule.CHORUS_FRUIT_PIE.get());
		generators.createSimpleFlatItemModel(FoodModule.CHORUS_FRUIT_PIE_SLICE.get());
		generators.createSimpleFlatItemModel(PomegranateModule.POMEGRANATE.get());
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
		ResourceLocation baseTexture = TextureMapping.getBlockTexture(CoreModule.APPLE_LEAVES.is(block) ? Blocks.OAK_LEAVES : block);
		ResourceLocation model2 = FFModelTemplates.FLOWERING_LEAVES.create(block, new TextureMapping()
						.put(FFModelTemplates.LEAVES, baseTexture)
						.put(FFModelTemplates.FLOWERS, tex("%s_flowers".formatted(typeId.getPath()))),
				generators.modelOutput);
		ResourceLocation model3 = scale.template.create(block, new TextureMapping()
						.put(FFModelTemplates.LEAVES, baseTexture),
				generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(FruitLeavesBlock.AGE).generateList(age -> {
			if (age < 2) {
				return List.of(Variant.variant().with(VariantProperties.MODEL, model01));
			}
			if (age == 2) {
				return List.of(Variant.variant().with(VariantProperties.MODEL, model2));
			}
			if (age == 3) {
				List<Variant> variants = Lists.newArrayList(Variant.variant().with(VariantProperties.MODEL, model3));
				if (scale.randomRotation) {
					variants.add(Variant.variant()
							.with(VariantProperties.MODEL, model3)
							.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
							.with(VariantProperties.UV_LOCK, true));
					variants.add(Variant.variant()
							.with(VariantProperties.MODEL, model3)
							.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
							.with(VariantProperties.UV_LOCK, true));
				}
				return variants;
			}
			throw new IllegalStateException("Unexpected value: " + age);
		}));
		generators.blockStateOutput.accept(generator);
		generators.delegateItemModel(block, model2);
	}

	public static void createRedloveLeaves(BlockModelGenerators generators, FruitLeavesBlock block) {
		ResourceLocation model012;
		if (CherryModule.CHERRY_LEAVES.is(block)) {
			model012 = ModelLocationUtils.getModelLocation(Blocks.CHERRY_LEAVES);
		} else {
			model012 = TexturedModel.LEAVES.create(block, generators.modelOutput);
		}
		ResourceLocation model3 = ModelTemplates.LEAVES.createWithSuffix(block, "_2", TextureMapping.cube(TextureMapping.getBlockTexture(block, "_2")), generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(FruitLeavesBlock.AGE).generateList(age -> {
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
		SMALL(FFModelTemplates.FRUIT_SM_LEAVES),
		MIDDLE(FFModelTemplates.FRUIT_MD_LEAVES),
		LARGE(FFModelTemplates.FRUIT_LG_LEAVES);

		public final ModelTemplate template;
		public final boolean randomRotation;

		FruitScale(ModelTemplate template) {
			this.template = template;
			randomRotation = template != FFModelTemplates.FRUIT_LG_LEAVES;
		}
	}
}
