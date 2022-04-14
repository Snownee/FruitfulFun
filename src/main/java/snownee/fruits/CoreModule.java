package snownee.fruits;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
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
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.datagen.provider.KiwiLootTableProvider;
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
	public static final KiwiGO<Item> MANDARIN = go(() -> new ModItem(itemProp().food(Foods.MANDARIN)));
	@Category("food")
	public static final KiwiGO<Item> LIME = go(() -> new ModItem(itemProp().food(Foods.LIME)));
	@Category("food")
	public static final KiwiGO<Item> CITRON = go(() -> new ModItem(itemProp().food(Foods.CITRON)));
	@Category("food")
	public static final KiwiGO<Item> POMELO = go(() -> new ModItem(itemProp().food(Foods.POMELO)));
	@Category("food")
	public static final KiwiGO<Item> ORANGE = go(() -> new ModItem(itemProp().food(Foods.ORANGE)));
	@Category("food")
	public static final KiwiGO<Item> LEMON = go(() -> new ModItem(itemProp().food(Foods.LEMON)));
	@Category("food")
	public static final KiwiGO<Item> GRAPEFRUIT = go(() -> new ModItem(itemProp().food(Foods.GRAPEFRUIT)));
	public static final KiwiGO<Item> EMPOWERED_CITRON = go(() -> new ModItem(itemProp().rarity(Rarity.RARE).food(Foods.EMPOWERED_CITRON)) {
		@Override
		public boolean isFoil(ItemStack stack) {
			return true;
		}
	});

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> MANDARIN_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.MANDARIN, blockProp(Blocks.OAK_LEAVES)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> LIME_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.LIME, blockProp(Blocks.OAK_LEAVES)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> CITRON_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.CITRON, blockProp(Blocks.OAK_LEAVES)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> POMELO_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.POMELO, blockProp(Blocks.OAK_LEAVES)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> ORANGE_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.ORANGE, blockProp(Blocks.OAK_LEAVES)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> LEMON_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.LEMON, blockProp(Blocks.OAK_LEAVES)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> GRAPEFRUIT_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.GRAPEFRUIT, blockProp(Blocks.OAK_LEAVES)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> APPLE_LEAVES = go(() -> new FruitLeavesBlock(() -> FruitType.APPLE, blockProp(Blocks.OAK_LEAVES)));

	@Category("building_blocks")
	public static final KiwiGO<Block> CITRUS_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.JUNGLE_LOG)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CITRUS_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD)));
	@Category("building_blocks")
	public static final KiwiGO<Block> STRIPPED_CITRUS_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG)));
	@Category("building_blocks")
	public static final KiwiGO<Block> STRIPPED_CITRUS_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CITRUS_PLANKS = go(() -> new ModBlock(blockProp(Blocks.JUNGLE_PLANKS)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CITRUS_SLAB = go(() -> new SlabBlock(blockProp(Blocks.JUNGLE_SLAB)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CITRUS_STAIRS = go(() -> new StairBlock(() -> CITRUS_PLANKS.defaultBlockState(), blockProp(Blocks.JUNGLE_STAIRS)));
	@Category("decorations")
	public static final KiwiGO<Block> CITRUS_FENCE = go(() -> new FenceBlock(blockProp(Blocks.JUNGLE_FENCE)));
	@Category("redstone")
	public static final KiwiGO<Block> CITRUS_FENCE_GATE = go(() -> new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE)));
	@Category("redstone")
	public static final KiwiGO<Block> CITRUS_TRAPDOOR = go(() -> new TrapDoorBlock(blockProp(Blocks.JUNGLE_TRAPDOOR)));
	@Category("redstone")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> CITRUS_DOOR = go(() -> new DoorBlock(blockProp(Blocks.JUNGLE_DOOR)));
	@Category("redstone")
	public static final KiwiGO<Block> CITRUS_BUTTON = go(() -> new WoodButtonBlock(blockProp(Blocks.JUNGLE_TRAPDOOR)));
	@Category("redstone")
	public static final KiwiGO<Block> CITRUS_PRESSURE_PLATE = go(() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.JUNGLE_DOOR)));

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> MANDARIN_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.MANDARIN), blockProp(Blocks.OAK_SAPLING)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> LIME_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.LIME), blockProp(Blocks.OAK_SAPLING)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> CITRON_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.CITRON), blockProp(Blocks.OAK_SAPLING)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> POMELO_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.POMELO), blockProp(Blocks.OAK_SAPLING)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> ORANGE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.ORANGE), blockProp(Blocks.OAK_SAPLING)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> LEMON_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.LEMON), blockProp(Blocks.OAK_SAPLING)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> GRAPEFRUIT_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.GRAPEFRUIT), blockProp(Blocks.OAK_SAPLING)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> APPLE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(FruitType.APPLE), blockProp(Blocks.OAK_SAPLING)));

	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_MANDARIN = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MANDARIN_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_LIME = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, LIME_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_CITRON = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CITRON_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_POMELO = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, POMELO_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_ORANGE = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ORANGE_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_LEMON = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, LEMON_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_GRAPEFRUIT = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GRAPEFRUIT_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_APPLE = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, APPLE_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING)));

	public static final TagKey<Block> ALL_LEAVES = blockTag(FruitsMod.ID, "leaves");
	public static final KiwiGO<BlockEntityType<FruitTreeBlockEntity>> FRUIT_TREE = blockEntity(FruitTreeBlockEntity::new, null, ALL_LEAVES);

	@Name("carpet")
	public static final KiwiGO<TreeDecoratorType<CarpetTreeDecorator>> CARPET_DECORATOR = go(() -> new TreeDecoratorType<>(CarpetTreeDecorator.CODEC));

	@Name("blob")
	public static final KiwiGO<FoliagePlacerType<FruitBlobFoliagePlacer>> BLOB_PLACER = go(() -> new FoliagePlacerType<>(FruitBlobFoliagePlacer.CODEC));

	public static final BannerPattern SNOWFLAKE = BannerPattern.create("SNOWFLAKE", "snowflake", "sno", true);
	@SuppressWarnings("deprecation")
	@Category("misc")
	public static final KiwiGO<Item> SNOWFLAKE_BANNER_PATTERN = go(() -> new BannerPatternItem(SNOWFLAKE, itemProp().stacksTo(Items.MOJANG_BANNER_PATTERN.getMaxStackSize()).rarity(Rarity.UNCOMMON)));

	public static final KiwiGO<SoundEvent> OPEN_SOUND = go(() -> new SoundEvent(new ResourceLocation(FruitsMod.ID, "block.wooden_door.open")));
	public static final KiwiGO<SoundEvent> CLOSE_SOUND = go(() -> new SoundEvent(new ResourceLocation(FruitsMod.ID, "block.wooden_door.close")));

	/* off */
	public static final EntityType<SlidingDoorEntity> SLIDING_DOOR = EntityType.Builder
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
	public static final KiwiGO<Block> CITRUS_SIGN = go(() -> new StandingSignBlock(blockProp(Blocks.OAK_SIGN), CITRUS_WOODTYPE));
	@NoItem
	public static final KiwiGO<Block> CITRUS_WALL_SIGN = go(() -> new WallSignBlock(blockProp(Blocks.OAK_WALL_SIGN), CITRUS_WOODTYPE));
	@SuppressWarnings("deprecation")
	@Name("citrus_sign")
	@Category("decorations")
	public static final KiwiGO<Item> CITRUS_SIGN_ITEM = go(() -> new SignItem(itemProp().stacksTo(Items.OAK_SIGN.getMaxStackSize()), CITRUS_SIGN.get(), CITRUS_WALL_SIGN.get()));

	// sqrt(vec(3, 4, 3))
	public static final KiwiGO<FruitDropGameEvent> FRUIT_DROP = go(() -> new FruitDropGameEvent("fruittrees:fruit_drop", 6));
	public static final KiwiGO<CancellableGameEvent> LEAVES_TRAMPLE = go(() -> new CancellableGameEvent("fruittrees:leaves_trample", 6));

	private Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> TREES_CF;

	public CoreModule() {
		MinecraftForge.EVENT_BUS.addListener(CoreModule::insertFeatures);
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
			pot.addPlant(MANDARIN_SAPLING.get().getRegistryName(), POTTED_MANDARIN);
			pot.addPlant(LIME_SAPLING.get().getRegistryName(), POTTED_LIME);
			pot.addPlant(CITRON_SAPLING.get().getRegistryName(), POTTED_CITRON);
			pot.addPlant(POMELO_SAPLING.get().getRegistryName(), POTTED_POMELO);
			pot.addPlant(ORANGE_SAPLING.get().getRegistryName(), POTTED_ORANGE);
			pot.addPlant(LEMON_SAPLING.get().getRegistryName(), POTTED_LEMON);
			pot.addPlant(GRAPEFRUIT_SAPLING.get().getRegistryName(), POTTED_GRAPEFRUIT);
			pot.addPlant(APPLE_SAPLING.get().getRegistryName(), POTTED_APPLE);

			VanillaActions.registerAxeConversion(CITRUS_LOG.get(), STRIPPED_CITRUS_LOG.get());
			VanillaActions.registerAxeConversion(CITRUS_WOOD.get(), STRIPPED_CITRUS_WOOD.get());
			for (FruitType type : FruitType.values()) {
				VanillaActions.registerCompostable(0.5f, type.fruit.get());
				VanillaActions.registerCompostable(0.3f, type.leaves.get());
				VanillaActions.registerCompostable(0.3f, type.sapling.get());
				VanillaActions.registerVillagerPickupable(type.fruit.get());
				VanillaActions.registerVillagerCompostable(type.fruit.get());

				type.makeFeature();
			}

			if (FruitsConfig.worldGen) {
				List<Holder<PlacedFeature>> list = Lists.newArrayList();
				for (FruitType type : List.of(FruitType.CITRON, FruitType.LIME, FruitType.MANDARIN)) {
					if (type.featureWG != null) {
						list.add(PlacementUtils.inlinePlaced(type.featureWG));
					}
				}
				TREES_CF = FeatureUtils.register("fruittrees:base_trees", Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(list)));
				allFeatures = new Holder[Hooks.cherry ? 5 : 3];

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

	private static Holder<PlacedFeature>[] allFeatures;

	private void makePlacedFeature(String id, int chunks, int index) {
		SimpleWeightedRandomList<IntProvider> simpleweightedrandomlist = SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(0), chunks - 1).add(ConstantInt.of(1), 1).build();
		CountPlacement placement = CountPlacement.of(new WeightedListInt(simpleweightedrandomlist));
		allFeatures[index] = PlacementUtils.register("fruittrees:trees_" + id, TREES_CF, VegetationPlacements.treePlacement(placement, CoreModule.LEMON_SAPLING.get()));
		if (index != 2 && Hooks.cherry) { //TODO hardcode
			allFeatures[index + 3] = PlacementUtils.register("fruittrees:cherry_" + id, FruitTypeExtension.CHERRY.featureWG, VegetationPlacements.treePlacement(placement, CoreModule.LEMON_SAPLING.get()));
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

	public static Holder<ConfiguredFeature<TreeConfiguration, ?>> buildTreeFeature(FruitType type, boolean worldGen, Supplier<Block> carpet) {
		BlockStateProvider leavesProvider;
		List<TreeDecorator> decorators;
		if (worldGen) {
			if (carpet == null) {
				decorators = ImmutableList.of(new BeehiveDecorator(0.05F));
			} else {
				decorators = ImmutableList.of(new BeehiveDecorator(0.05F), new CarpetTreeDecorator(BlockStateProvider.simple(carpet.get())));
			}
			leavesProvider = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(type.leaves.defaultBlockState(), 2).add(type.leaves.defaultBlockState().setValue(FruitLeavesBlock.AGE, 2), 1));
		} else {
			decorators = ImmutableList.of();
			leavesProvider = BlockStateProvider.simple(type.leaves.get());
		}
		StringBuffer buf = new StringBuffer("fruittrees:");
		buf.append(type.name().toLowerCase(Locale.ENGLISH));
		if (worldGen) {
			buf.append("_wg");
		}
		/* off */
        return FeatureUtils.register(buf.toString(), Feature.TREE,
                new TreeConfigurationBuilder(
                        BlockStateProvider.simple(type.log.get()),
                        new StraightTrunkPlacer(4, 2, 0),
                        leavesProvider,
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
				if (CITRON_LEAVES.is(state))
					return 0xDDCC58;
				if (GRAPEFRUIT_LEAVES.is(state))
					return 0xF4502B;
				if (LEMON_LEAVES.is(state))
					return 0xEBCA4B;
				if (LIME_LEAVES.is(state))
					return 0xCADA76;
				if (MANDARIN_LEAVES.is(state))
					return 0xF08A19;
				if (ORANGE_LEAVES.is(state))
					return 0xF08A19;
				if (POMELO_LEAVES.is(state))
					return 0xF7F67E;
				if (APPLE_LEAVES.is(state))
					return 0xFC1C2A;
			}
			return -1;
		}, MANDARIN_LEAVES.get(), LIME_LEAVES.get(), CITRON_LEAVES.get(), POMELO_LEAVES.get(), ORANGE_LEAVES.get(), LEMON_LEAVES.get(), GRAPEFRUIT_LEAVES.get(), APPLE_LEAVES.get());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void handleItemColor(ColorHandlerEvent.Item event) {
		ItemStack oakLeaves = new ItemStack(Items.OAK_LEAVES);
		ItemColors itemColors = event.getItemColors();
		itemColors.register((stack, i) -> itemColors.getColor(oakLeaves, i), MANDARIN_LEAVES.get(), LIME_LEAVES.get(), CITRON_LEAVES.get(), POMELO_LEAVES.get(), ORANGE_LEAVES.get(), LEMON_LEAVES.get(), GRAPEFRUIT_LEAVES.get(), APPLE_LEAVES.get());
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
