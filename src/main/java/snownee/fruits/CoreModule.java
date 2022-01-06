package snownee.fruits;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerPatternItem;
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
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.fruits.cherry.FruitTypeExtension;
import snownee.fruits.cherry.block.SlidingDoorEntity;
import snownee.fruits.cherry.client.SlidingDoorRenderer;
import snownee.fruits.datagen.CommonBlockTagsProvider;
import snownee.fruits.datagen.CommonItemTagsProvider;
import snownee.fruits.datagen.CoreBlockLoot;
import snownee.fruits.levelgen.foliageplacers.FruitBlobFoliagePlacer;
import snownee.fruits.levelgen.treedecorators.CarpetTreeDecorator;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.data.provider.KiwiLootTableProvider;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.VanillaActions;

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

	@Category("food")
	public static Item MANDARIN = new ModItem(itemProp().food(Foods.MANDARIN));
	@Category("food")
	public static Item LIME = new ModItem(itemProp().food(Foods.LIME));
	@Category("food")
	public static Item CITRON = new ModItem(itemProp().food(Foods.CITRON));
	@Category("food")
	public static Item POMELO = new ModItem(itemProp().food(Foods.POMELO));
	@Category("food")
	public static Item ORANGE = new ModItem(itemProp().food(Foods.ORANGE));
	@Category("food")
	public static Item LEMON = new ModItem(itemProp().food(Foods.LEMON));
	@Category("food")
	public static Item GRAPEFRUIT = new ModItem(itemProp().food(Foods.GRAPEFRUIT));
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
	public static SaplingBlock MANDARIN_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.MANDARIN), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock LIME_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.LIME), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock CITRON_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.CITRON), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock POMELO_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.POMELO), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock ORANGE_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.ORANGE), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock LEMON_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.LEMON), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock GRAPEFRUIT_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.GRAPEFRUIT), blockProp(Blocks.OAK_SAPLING));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock APPLE_SAPLING = new SaplingBlock(new FruitTreeGrower(() -> FruitType.APPLE), blockProp(Blocks.OAK_SAPLING));

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

	public static final Set<Block> ALL_LEAVES = Sets.newConcurrentHashSet();
	public static BlockEntityType<FruitTreeBlockEntity> FRUIT_TREE = new BlockEntityType<>(FruitTreeBlockEntity::new, ALL_LEAVES, null);

	@Name("carpet")
	public static TreeDecoratorType<CarpetTreeDecorator> CARPET_DECORATOR = Registry.register(Registry.TREE_DECORATOR_TYPES, "fruittrees:carpet", new TreeDecoratorType<>(CarpetTreeDecorator.CODEC));

	@Name("blob")
	public static FoliagePlacerType<FruitBlobFoliagePlacer> BLOB_PLACER = Registry.register(Registry.FOLIAGE_PLACER_TYPES, "fruittrees:blob", new FoliagePlacerType<>(FruitBlobFoliagePlacer.CODEC));

	public static final BannerPattern SNOWFLAKE = BannerPattern.create("SNOWFLAKE", "snowflake", "sno", true);
	@Category("misc")
	public static final BannerPatternItem SNOWFLAKE_BANNER_PATTERN = new BannerPatternItem(SNOWFLAKE, itemProp().stacksTo(1).rarity(Rarity.UNCOMMON));

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
	@Category("decorations")
	public static SignItem CITRUS_SIGN_ITEM = new SignItem(itemProp().stacksTo(16), CITRUS_SIGN, CITRUS_WALL_SIGN);

	// sqrt(vec(3, 4, 3))
	public static FruitDropGameEvent FRUIT_DROP = new FruitDropGameEvent("fruittrees:fruit_drop", 6);
	public static CancellableGameEvent LEAVES_TRAMPLE = new CancellableGameEvent("fruittrees:leaves_trample", 6);

	private ConfiguredFeature<?, ?> TREES_CF;

	public CoreModule() {
		MinecraftForge.EVENT_BUS.addListener(CoreModule::insertFeatures);
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
			pot.addPlant(MANDARIN_SAPLING.getRegistryName(), () -> POTTED_MANDARIN);
			pot.addPlant(LIME_SAPLING.getRegistryName(), () -> POTTED_LIME);
			pot.addPlant(CITRON_SAPLING.getRegistryName(), () -> POTTED_CITRON);
			pot.addPlant(POMELO_SAPLING.getRegistryName(), () -> POTTED_POMELO);
			pot.addPlant(ORANGE_SAPLING.getRegistryName(), () -> POTTED_ORANGE);
			pot.addPlant(LEMON_SAPLING.getRegistryName(), () -> POTTED_LEMON);
			pot.addPlant(GRAPEFRUIT_SAPLING.getRegistryName(), () -> POTTED_GRAPEFRUIT);
			pot.addPlant(APPLE_SAPLING.getRegistryName(), () -> POTTED_APPLE);

			VanillaActions.registerAxeConversion(CITRUS_LOG, STRIPPED_CITRUS_LOG);
			VanillaActions.registerAxeConversion(CITRUS_WOOD, STRIPPED_CITRUS_WOOD);
			for (FruitType type : FruitType.values()) {
				VanillaActions.registerCompostable(0.5f, type.fruit);
				VanillaActions.registerCompostable(0.3f, type.leaves);
				VanillaActions.registerCompostable(0.3f, type.sapling.get());
				VanillaActions.registerVillagerPickupable(type.fruit);
				VanillaActions.registerVillagerCompostable(type.fruit);

				type.makeFeature();
			}

			if (FruitsConfig.worldGen) {
				ImmutableList.Builder<Supplier<PlacedFeature>> builder = ImmutableList.builder();
				for (FruitType type : List.of(FruitType.CITRON, FruitType.LIME, FruitType.MANDARIN)) {
					if (type.featureWG != null) {
						builder.add(() -> type.featureWG.placed());
					}
				}
				TREES_CF = Feature.SIMPLE_RANDOM_SELECTOR.configured(new SimpleRandomFeatureConfiguration(builder.build()));
				FeatureUtils.register("fruittrees:base_trees", TREES_CF);
				allFeatures = new PlacedFeature[Hooks.cherry ? 5 : 3];

				makePlacedFeature("002", FruitsConfig.treesGenChunksInPlains, 0);
				makePlacedFeature("005", FruitsConfig.treesGenChunksInForest, 1);
				makePlacedFeature("1", FruitsConfig.treesGenChunksInJungle, 2);
			}

			WoodType.register(CITRUS_WOODTYPE);
		}).whenComplete((v, ex) -> {
			if (ex != null)
				FruitsMod.logger.catching(ex);
		}); // WTF??? workaround handle it

	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientInit(ModelRegistryEvent event) {
		Sheets.addWoodType(CITRUS_WOODTYPE);
	}

	@SubscribeEvent
	protected void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SLIDING_DOOR, SlidingDoorRenderer::new);
	}

	private static PlacedFeature[] allFeatures;

	private void makePlacedFeature(String id, int chunks, int index) {
		SimpleWeightedRandomList<IntProvider> simpleweightedrandomlist = SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(0), chunks - 1).add(ConstantInt.of(1), 1).build();
		CountPlacement placement = CountPlacement.of(new WeightedListInt(simpleweightedrandomlist));
		allFeatures[index] = TREES_CF.placed(VegetationPlacements.treePlacement(placement, LEMON_SAPLING));
		PlacementUtils.register("fruittrees:trees_" + id, allFeatures[index]);
		if (index != 2 && Hooks.cherry) { //TODO hardcode
			allFeatures[index + 3] = FruitTypeExtension.CHERRY.featureWG.placed(VegetationPlacements.treePlacement(placement, LEMON_SAPLING));
			PlacementUtils.register("fruittrees:cherry_" + id, allFeatures[index + 3]);
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
		event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, allFeatures[i]);
		if (category != BiomeCategory.JUNGLE && Hooks.cherry) {
			event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, allFeatures[i + 3]);
		}
	}

	public static ConfiguredFeature<TreeConfiguration, ?> buildTreeFeature(FruitType type, boolean worldGen, Block carpet) {
		BlockStateProvider leavesProvider;
		List<TreeDecorator> decorators;
		if (worldGen) {
			if (carpet == null) {
				decorators = ImmutableList.of(new BeehiveDecorator(0.05F));
			} else {
				decorators = ImmutableList.of(new BeehiveDecorator(0.05F), new CarpetTreeDecorator(BlockStateProvider.simple(carpet)));
			}
			leavesProvider = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(type.leaves.defaultBlockState(), 2).add(type.leaves.defaultBlockState().setValue(FruitLeavesBlock.AGE, 2), 1));
		} else {
			decorators = ImmutableList.of();
			leavesProvider = BlockStateProvider.simple(type.leaves);
		}
		StringBuffer buf = new StringBuffer("fruittrees:");
		buf.append(type.name().toLowerCase(Locale.ENGLISH));
		if (worldGen) {
			buf.append("_wg");
		}
		/* off */
        return FeatureUtils.register(buf.toString(), Feature.TREE.configured(
                new TreeConfigurationBuilder(
                        BlockStateProvider.simple(type.log),
                        new StraightTrunkPlacer(4, 2, 0),
                        leavesProvider,
                        new FruitBlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                )
                .ignoreVines()
                .decorators(decorators)
                .build()
        ));
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

	@Override
	public void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {
			generator.addProvider(new KiwiLootTableProvider(generator).add(CoreBlockLoot::new, LootContextParamSets.BLOCK));
			CommonBlockTagsProvider blockTagsProvider = new CommonBlockTagsProvider(generator, event.getExistingFileHelper());
			generator.addProvider(blockTagsProvider);
			generator.addProvider(new CommonItemTagsProvider(generator, blockTagsProvider, event.getExistingFileHelper()));
		}
	}

}
