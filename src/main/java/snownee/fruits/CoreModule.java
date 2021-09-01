package snownee.fruits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.Tag;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biome.ClimateSettings;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.block.trees.FruitTree;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.FruitTypeExtension;
import snownee.fruits.cherry.block.SlidingDoorEntity;
import snownee.fruits.cherry.client.SlidingDoorRenderer;
import snownee.fruits.levelgen.foliageplacers.FruitBlobFoliagePlacer;
import snownee.fruits.levelgen.treedecorators.CarpetTreeDecorator;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.Name;
import snownee.kiwi.NoItem;
import snownee.kiwi.RenderLayer;
import snownee.kiwi.RenderLayer.Layer;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.util.DeferredActions;

@KiwiModule
@KiwiModule.Subscriber(Bus.MOD)
public final class CoreModule extends AbstractModule {

	public static final class Foods {
		public static final FoodProperties MANDARIN = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties LIME = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties CITRON = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties POMELO = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build();
		public static final FoodProperties ORANGE = new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).build();
		public static final FoodProperties LEMON = new FoodProperties.Builder().nutrition(2).saturationMod(1f).fast().build();
		public static final FoodProperties GRAPEFRUIT = new FoodProperties.Builder().nutrition(6).saturationMod(0.4f).build();
		public static final FoodProperties EMPOWERED_CITRON = new FoodProperties.Builder().nutrition(3).saturationMod(5f).build();
	}

	public static Item MANDARIN = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.MANDARIN));
	public static Item LIME = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.LIME));
	public static Item CITRON = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.CITRON));
	public static Item POMELO = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.POMELO));
	public static Item ORANGE = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.ORANGE));
	public static Item LEMON = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.LEMON));
	public static Item GRAPEFRUIT = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.GRAPEFRUIT));
	public static Item EMPOWERED_CITRON = new ModItem(itemProp().rarity(Rarity.RARE).food(Foods.EMPOWERED_CITRON)) {
		@Override
		public boolean isFoil(ItemStack stack) {
			return true;
		}
	};

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock MANDARIN_LEAVES = new FruitLeavesBlock(() -> FruitType.MANDARIN, blockProp(Blocks.OAK_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock LIME_LEAVES = new FruitLeavesBlock(() -> FruitType.LIME, blockProp(Blocks.OAK_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock CITRON_LEAVES = new FruitLeavesBlock(() -> FruitType.CITRON, blockProp(Blocks.OAK_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock POMELO_LEAVES = new FruitLeavesBlock(() -> FruitType.POMELO, blockProp(Blocks.OAK_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock ORANGE_LEAVES = new FruitLeavesBlock(() -> FruitType.ORANGE, blockProp(Blocks.OAK_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock LEMON_LEAVES = new FruitLeavesBlock(() -> FruitType.LEMON, blockProp(Blocks.OAK_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock GRAPEFRUIT_LEAVES = new FruitLeavesBlock(() -> FruitType.GRAPEFRUIT, blockProp(Blocks.OAK_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static FruitLeavesBlock APPLE_LEAVES = new FruitLeavesBlock(() -> FruitType.APPLE, blockProp(Blocks.OAK_LEAVES));

	@Category("building_blocks")
	public static RotatedPillarBlock CITRUS_LOG = new RotatedPillarBlock(blockProp(Blocks.JUNGLE_LOG));
	@Category("building_blocks")
	public static Block CITRUS_WOOD = new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD));
	@Category("building_blocks")
	public static Block STRIPPED_CITRUS_LOG = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG));
	@Category("building_blocks")
	public static Block STRIPPED_CITRUS_WOOD = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD));
	@Category("building_blocks")
	public static Block CITRUS_PLANKS = new ModBlock(blockProp(Blocks.JUNGLE_PLANKS));
	@Category("building_blocks")
	public static SlabBlock CITRUS_SLAB = new SlabBlock(blockProp(Blocks.JUNGLE_SLAB));
	@Category("building_blocks")
	public static StairBlock CITRUS_STAIRS = new StairBlock(() -> CITRUS_PLANKS.defaultBlockState(), blockProp(Blocks.JUNGLE_STAIRS));
	@Category("decorations")
	public static FenceBlock CITRUS_FENCE = new FenceBlock(blockProp(Blocks.JUNGLE_FENCE));
	@Category("redstone")
	public static FenceGateBlock CITRUS_FENCE_GATE = new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE));
	@Category("redstone")
	public static TrapDoorBlock CITRUS_TRAPDOOR = new TrapDoorBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
	@Category("redstone")
	@RenderLayer(Layer.CUTOUT)
	public static DoorBlock CITRUS_DOOR = new DoorBlock(blockProp(Blocks.JUNGLE_DOOR));
	@Category("redstone")
	public static WoodButtonBlock CITRUS_BUTTON = new WoodButtonBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
	@Category("redstone")
	public static PressurePlateBlock CITRUS_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.JUNGLE_DOOR));

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock MANDARIN_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.MANDARIN), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock LIME_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.LIME), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock CITRON_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.CITRON), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock POMELO_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.POMELO), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock ORANGE_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.ORANGE), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock LEMON_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.LEMON), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock GRAPEFRUIT_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.GRAPEFRUIT), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock APPLE_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.APPLE), blockProp(Blocks.OAK_SAPLING));

	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_MANDARIN = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> MANDARIN_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_LIME = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> LIME_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_CITRON = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> CITRON_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_POMELO = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> POMELO_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_ORANGE = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> ORANGE_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_LEMON = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> LEMON_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_GRAPEFRUIT = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> GRAPEFRUIT_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_APPLE = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> APPLE_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));

	public static final Set<Block> ALL_LEAVES = Collections.synchronizedSet(Sets.newHashSet(Arrays.asList(MANDARIN_LEAVES, LIME_LEAVES, CITRON_LEAVES, POMELO_LEAVES, ORANGE_LEAVES, LEMON_LEAVES, GRAPEFRUIT_LEAVES, APPLE_LEAVES)));
	public static BlockEntityType<FruitTreeBlockEntity> FRUIT_TREE = new BlockEntityType<>(FruitTreeBlockEntity::new, ALL_LEAVES, null);

	@Name("carpet")
	public static TreeDecoratorType<CarpetTreeDecorator> CARPET_DECORATOR = new TreeDecoratorType<>(CarpetTreeDecorator.CODEC);

	@Name("blob")
	public static FoliagePlacerType<FruitBlobFoliagePlacer> BLOB_PLACER = new FoliagePlacerType<>(FruitBlobFoliagePlacer.CODEC);

	public static final BannerPattern SNOWFLAKE = BannerPattern.create("SNOWFLAKE", "snowflake", "sno", true);
	public static final BannerPatternItem SNOWFLAKE_BANNER_PATTERN = new BannerPatternItem(SNOWFLAKE, itemProp().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON));

	public static final Tag.Named<Item> FOX_BREEDABLES = itemTag(FruitsMod.MODID, "fox_breedables");

	public static SoundEvent OPEN_SOUND = new SoundEvent(new ResourceLocation(FruitsMod.MODID, "block.wooden_door.open"));
	public static SoundEvent CLOSE_SOUND = new SoundEvent(new ResourceLocation(FruitsMod.MODID, "block.wooden_door.close"));

	/* off */
	public static EntityType<SlidingDoorEntity> SLIDING_DOOR = EntityType.Builder
			.<SlidingDoorEntity>of(SlidingDoorEntity::new, MobCategory.MISC)
			.sized(1, 2)
			.fireImmune()
			.noSummon()
			.setCustomClientFactory((p, w) -> {
				return new SlidingDoorEntity(w, new BlockPos(p.getPosX(), p.getPosY(), p.getPosZ()));
			})
			.build("fruittrees:door");
	/* on */

	public static final WoodType CITRUS_WOODTYPE = WoodType.create("fruittrees_citrus");
	@NoItem
	public static StandingSignBlock CITRUS_SIGN = new StandingSignBlock(blockProp(Blocks.OAK_SIGN), CITRUS_WOODTYPE);
	@NoItem
	public static WallSignBlock CITRUS_WALL_SIGN = new WallSignBlock(blockProp(Blocks.OAK_WALL_SIGN), CITRUS_WOODTYPE);
	@Name("citrus_sign")
	public static SignItem CITRUS_SIGN_ITEM = new SignItem(itemProp().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), CITRUS_SIGN, CITRUS_WALL_SIGN);

	public CoreModule() {
		MinecraftForge.EVENT_BUS.addListener(CoreModule::insertFeatures);
	}

	public static List<FruitType> types;

	@Override
	protected void init(FMLCommonSetupEvent event) {
		types = Arrays.asList(FruitType.CITRON, FruitType.LIME, FruitType.MANDARIN);
		try {
			FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
			pot.addPlant(MANDARIN_SAPLING.getRegistryName(), () -> POTTED_MANDARIN);
			pot.addPlant(LIME_SAPLING.getRegistryName(), () -> POTTED_LIME);
			pot.addPlant(CITRON_SAPLING.getRegistryName(), () -> POTTED_CITRON);
			pot.addPlant(POMELO_SAPLING.getRegistryName(), () -> POTTED_POMELO);
			pot.addPlant(ORANGE_SAPLING.getRegistryName(), () -> POTTED_ORANGE);
			pot.addPlant(LEMON_SAPLING.getRegistryName(), () -> POTTED_LEMON);
			pot.addPlant(GRAPEFRUIT_SAPLING.getRegistryName(), () -> POTTED_GRAPEFRUIT);
			pot.addPlant(APPLE_SAPLING.getRegistryName(), () -> POTTED_APPLE);
		} catch (Exception e) {
			FruitsMod.logger.catching(e);
		}

		DeferredActions.registerAxeConversion(CITRUS_LOG, STRIPPED_CITRUS_LOG);
		DeferredActions.registerAxeConversion(CITRUS_WOOD, STRIPPED_CITRUS_WOOD);
		for (FruitType type : FruitType.values()) {
			DeferredActions.registerCompostable(0.5f, type.fruit);
			DeferredActions.registerCompostable(0.3f, type.leaves);
			DeferredActions.registerCompostable(0.3f, type.sapling.get());
			DeferredActions.registerVillagerPickupable(type.fruit);
			DeferredActions.registerVillagerCompostable(type.fruit);
		}

		if (FruitsConfig.worldGen) {
			ImmutableList.Builder<Supplier<ConfiguredFeature<?, ?>>> builder = ImmutableList.builder();
			for (FruitType type : types) {
				Supplier<ConfiguredFeature<?, ?>> cf = () -> buildTreeFeature(type, true, null);
				builder.add(cf);
			}
			trees = builder.build();
			if (FruitTypeExtension.CHERRY != null) {
				cherry = buildTreeFeature(FruitTypeExtension.CHERRY, true, new SimpleStateProvider(CherryModule.CHERRY_CARPET.defaultBlockState()));
				allFeatures = new ConfiguredFeature[5];
			} else {
				allFeatures = new ConfiguredFeature[3];
			}
			makeFeature("002", 0, FruitsConfig.treesGenInPlains, 0);
			makeFeature("005", 0, FruitsConfig.treesGenInForest, 1);
			makeFeature("1", 1, 0, 2);
			trees = null;
			cherry = null;
		}

		event.enqueueWork(() -> WoodType.register(CITRUS_WOODTYPE));
	}

	@Override
	protected void clientInit(FMLClientSetupEvent event) {
		event.enqueueWork(() -> Sheets.addWoodType(CITRUS_WOODTYPE));
	}

	@SubscribeEvent
	protected void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SLIDING_DOOR, SlidingDoorRenderer::new);
	}

	private List<Supplier<ConfiguredFeature<?, ?>>> trees;
	private ConfiguredFeature<?, ?> cherry;
	private static ConfiguredFeature<?, ?>[] allFeatures;

	private void makeFeature(String id, int count, float chance, int index) {
		if (count == 0 && chance == 0)
			return;
		ConfiguredFeature<?, ?> cf = Feature.SIMPLE_RANDOM_SELECTOR.configured(new SimpleRandomFeatureConfiguration(trees)).decorated(Features.Decorators.HEIGHTMAP_SQUARE).decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(count, chance, 1)));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "fruittrees:trees_" + id, cf);
		allFeatures[index] = cf;
		if (chance > 0 && cherry != null) {
			cf = cherry.decorated(Features.Decorators.HEIGHTMAP_SQUARE).decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(count, chance / 2, 1)));
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "fruittrees:cherry_" + id, cf);
			allFeatures[index + 3] = cf;
		}
	}

	public static void insertFeatures(BiomeLoadingEvent event) {
		if (!FruitsConfig.worldGen) {
			return;
		}
		ClimateSettings climate = event.getClimate();
		if (climate.precipitation != Precipitation.RAIN) {
			return;
		}
		if (climate.temperatureModifier == TemperatureModifier.FROZEN) {
			return;
		}
		BiomeCategory category = event.getCategory();
		int i;
		switch (category) {
		case PLAINS:
			i = 0;
			break;
		case FOREST:
			i = 1;
			break;
		case JUNGLE:
			i = 2;
			break;
		default:
			return;
		}
		insertFeature(event, allFeatures[i]);
		if (category != BiomeCategory.JUNGLE && FruitTypeExtension.CHERRY != null) {
			insertFeature(event, allFeatures[i + 3]);
		}
	}

	public static void insertFeature(BiomeLoadingEvent event, ConfiguredFeature<?, ?> cf) {
		if (cf != null)
			event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, cf);
	}

	public static ConfiguredFeature<TreeConfiguration, ?> buildTreeFeature(FruitType type, boolean worldGen, BlockStateProvider carpetProvider) {
		BlockStateProvider leavesProvider;
		List<TreeDecorator> decorators;
		if (worldGen) {
			if (carpetProvider == null) {
				decorators = ImmutableList.of(new BeehiveDecorator(0.05F));
			} else {
				decorators = ImmutableList.of(new BeehiveDecorator(0.05F), new CarpetTreeDecorator(carpetProvider));
			}
			leavesProvider = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(type.leaves.defaultBlockState(), 2).add(type.leaves.defaultBlockState().setValue(FruitLeavesBlock.AGE, 2), 1));
		} else {
			decorators = ImmutableList.of();
			leavesProvider = new SimpleStateProvider(type.leaves.defaultBlockState());
		}
		/* off */
        return Feature.TREE.configured(
                new TreeConfigurationBuilder(
                        new SimpleStateProvider(type.log.defaultBlockState()),
                        new StraightTrunkPlacer(4, 2, 0),
                        leavesProvider,
                        new SimpleStateProvider(type.sapling.get().defaultBlockState()),
                        new FruitBlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                )
                .ignoreVines()
                .decorators(decorators)
                .build()
        );
        /* on */
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void handleBlockColor(ColorHandlerEvent.Block event) {
		BlockState oakLeaves = Blocks.OAK_LEAVES.defaultBlockState();
		BlockColors blockColors = event.getBlockColors();
		blockColors.register((state, world, pos, i) -> {
			if (i == 0) {
				return blockColors.getColor(oakLeaves, world, pos, i);
			}
			if (i == 1) {
				Block block = state.getBlock();
				if (block == CITRON_LEAVES)
					return 0xDDCC58;
				if (block == GRAPEFRUIT_LEAVES)
					return 0xF4502B;
				if (block == LEMON_LEAVES)
					return 0xEBCA4B;
				if (block == LIME_LEAVES)
					return 0xCADA76;
				if (block == MANDARIN_LEAVES)
					return 0xF08A19;
				if (block == ORANGE_LEAVES)
					return 0xF08A19;
				if (block == POMELO_LEAVES)
					return 0xF7F67E;
				if (block == APPLE_LEAVES)
					return 0xFC1C2A;
			}
			return -1;
		}, MANDARIN_LEAVES, LIME_LEAVES, CITRON_LEAVES, POMELO_LEAVES, ORANGE_LEAVES, LEMON_LEAVES, GRAPEFRUIT_LEAVES, APPLE_LEAVES);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void handleItemColor(ColorHandlerEvent.Item event) {
		ItemStack oakLeaves = new ItemStack(Items.OAK_LEAVES);
		ItemColors itemColors = event.getItemColors();
		itemColors.register((stack, i) -> itemColors.getColor(oakLeaves, i), MANDARIN_LEAVES, LIME_LEAVES, CITRON_LEAVES, POMELO_LEAVES, ORANGE_LEAVES, LEMON_LEAVES, GRAPEFRUIT_LEAVES, APPLE_LEAVES);
	}

}
