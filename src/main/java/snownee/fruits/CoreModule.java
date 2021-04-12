package snownee.fruits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.Climate;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.Biome.TemperatureModifier;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.SingleRandomFeature;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.trees.FruitTree;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.FruitTypeExtension;
import snownee.fruits.tile.FruitTreeTile;
import snownee.fruits.world.gen.foliageplacer.FruitBlobFoliagePlacer;
import snownee.fruits.world.gen.treedecorator.CarpetTreeDecorator;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Group;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.Name;
import snownee.kiwi.NoItem;
import snownee.kiwi.RenderLayer;
import snownee.kiwi.RenderLayer.Layer;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.util.DeferredActions;

//TODO: 1.17: Forge 1.16.5-36.0.60 - Add support for custom WoodTypes
@KiwiModule
@KiwiModule.Subscriber(Bus.MOD)
public final class CoreModule extends AbstractModule { // TODO block map colors?

    @SuppressWarnings("hiding")
    public static final class Foods {
        public static final Food MANDARIN = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food LIME = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food CITRON = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food POMELO = new Food.Builder().hunger(4).saturation(0.3f).build();
        public static final Food ORANGE = new Food.Builder().hunger(3).saturation(0.5f).build();
        public static final Food LEMON = new Food.Builder().hunger(2).saturation(1f).fastToEat().build();
        public static final Food GRAPEFRUIT = new Food.Builder().hunger(6).saturation(0.4f).build();
        public static final Food EMPOWERED_CITRON = new Food.Builder().hunger(3).saturation(5f).build();
    }

    public static final Item MANDARIN = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.MANDARIN));
    public static final Item LIME = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.LIME));
    public static final Item CITRON = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.CITRON));
    public static final Item POMELO = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.POMELO));
    public static final Item ORANGE = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.ORANGE));
    public static final Item LEMON = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.LEMON));
    public static final Item GRAPEFRUIT = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.GRAPEFRUIT));
    public static final Item EMPOWERED_CITRON = new ModItem(itemProp().rarity(Rarity.RARE).food(Foods.EMPOWERED_CITRON)) {
        @Override
        public boolean hasEffect(ItemStack stack) {
            return true;
        }
    };

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock MANDARIN_LEAVES = new FruitLeavesBlock(() -> FruitType.MANDARIN, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock LIME_LEAVES = new FruitLeavesBlock(() -> FruitType.LIME, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock CITRON_LEAVES = new FruitLeavesBlock(() -> FruitType.CITRON, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock POMELO_LEAVES = new FruitLeavesBlock(() -> FruitType.POMELO, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock ORANGE_LEAVES = new FruitLeavesBlock(() -> FruitType.ORANGE, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock LEMON_LEAVES = new FruitLeavesBlock(() -> FruitType.LEMON, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock GRAPEFRUIT_LEAVES = new FruitLeavesBlock(() -> FruitType.GRAPEFRUIT, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock APPLE_LEAVES = new FruitLeavesBlock(() -> FruitType.APPLE, blockProp(Blocks.OAK_LEAVES));

    @Group("building_blocks")
    public static final RotatedPillarBlock CITRUS_LOG = new RotatedPillarBlock(blockProp(Blocks.JUNGLE_LOG));
    @Group("building_blocks")
    public static final Block CITRUS_WOOD = new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD));
    @Group("building_blocks")
    public static final Block STRIPPED_CITRUS_LOG = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG));
    @Group("building_blocks")
    public static final Block STRIPPED_CITRUS_WOOD = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD));
    @Group("building_blocks")
    public static final Block CITRUS_PLANKS = new ModBlock(blockProp(Blocks.JUNGLE_PLANKS));
    @Group("building_blocks")
    public static final SlabBlock CITRUS_SLAB = new SlabBlock(blockProp(Blocks.JUNGLE_SLAB));
    @Group("building_blocks")
    public static final StairsBlock CITRUS_STAIRS = new StairsBlock(() -> CITRUS_PLANKS.getDefaultState(), blockProp(Blocks.JUNGLE_STAIRS));
    @Group("decorations")
    public static final FenceBlock CITRUS_FENCE = new FenceBlock(blockProp(Blocks.JUNGLE_FENCE));
    @Group("redstone")
    public static final FenceGateBlock CITRUS_FENCE_GATE = new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE));
    @Group("redstone")
    public static final TrapDoorBlock CITRUS_TRAPDOOR = new TrapDoorBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
    @Group("redstone")
    @RenderLayer(Layer.CUTOUT)
    public static final DoorBlock CITRUS_DOOR = new DoorBlock(blockProp(Blocks.JUNGLE_DOOR));
    @Group("redstone")
    public static final WoodButtonBlock CITRUS_BUTTON = new WoodButtonBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
    @Group("redstone")
    public static final PressurePlateBlock CITRUS_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.JUNGLE_DOOR));

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock MANDARIN_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.MANDARIN), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock LIME_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.LIME), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock CITRON_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.CITRON), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock POMELO_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.POMELO), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock ORANGE_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.ORANGE), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock LEMON_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.LEMON), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock GRAPEFRUIT_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.GRAPEFRUIT), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock APPLE_SAPLING = new SaplingBlock(new FruitTree(() -> FruitType.APPLE), blockProp(Blocks.OAK_SAPLING));

    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_MANDARIN = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> MANDARIN_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_LIME = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> LIME_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_CITRON = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> CITRON_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_POMELO = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> POMELO_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_ORANGE = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> ORANGE_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_LEMON = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> LEMON_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_GRAPEFRUIT = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> GRAPEFRUIT_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_APPLE = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> APPLE_SAPLING, blockProp(Blocks.POTTED_JUNGLE_SAPLING));

    public static final Set<Block> ALL_LEAVES = Collections.synchronizedSet(Sets.newHashSet(Arrays.asList(MANDARIN_LEAVES, LIME_LEAVES, CITRON_LEAVES, POMELO_LEAVES, ORANGE_LEAVES, LEMON_LEAVES, GRAPEFRUIT_LEAVES, APPLE_LEAVES)));
    public static final TileEntityType<FruitTreeTile> FRUIT_TREE = new TileEntityType<>(FruitTreeTile::new, ALL_LEAVES, null);

    @Name("carpet")
    public static final TreeDecoratorType<CarpetTreeDecorator> CARPET_DECORATOR = new TreeDecoratorType<>(CarpetTreeDecorator.CODEC);

    @Name("blob")
    public static final FoliagePlacerType<FruitBlobFoliagePlacer> BLOB_PLACER = new FoliagePlacerType<>(FruitBlobFoliagePlacer.CODEC);

    public static final BannerPattern SNOWFLAKE = BannerPattern.create("SNOWFLAKE", "snowflake", "sno", true);
    public static final BannerPatternItem SNOWFLAKE_BANNER_PATTERN = new BannerPatternItem(SNOWFLAKE, itemProp().maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON));

    public static final INamedTag<Item> FOX_BREEDABLES = itemTag(FruitsMod.MODID, "fox_breedables");

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
                cherry = buildTreeFeature(FruitTypeExtension.CHERRY, true, new SimpleBlockStateProvider(CherryModule.CHERRY_CARPET.getDefaultState()));
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
    }

    private List<Supplier<ConfiguredFeature<?, ?>>> trees;
    private ConfiguredFeature<?, ?> cherry;
    private static ConfiguredFeature<?, ?>[] allFeatures;

    private void makeFeature(String id, int count, float chance, int index) {
        if (count == 0 && chance == 0)
            return;
        ConfiguredFeature<?, ?> cf = Feature.SIMPLE_RANDOM_SELECTOR.withConfiguration(new SingleRandomFeature(trees)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(count, chance, 1)));
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "fruittrees:trees_" + id, cf);
        allFeatures[index] = cf;
        if (chance > 0 && cherry != null) {
            cf = cherry.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(count, chance / 2, 1)));
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "fruittrees:cherry_" + id, cf);
            allFeatures[index + 3] = cf;
        }
    }

    public static void insertFeatures(BiomeLoadingEvent event) {
        if (!FruitsConfig.worldGen) {
            return;
        }
        Climate climate = event.getClimate();
        if (climate.precipitation != RainType.RAIN) {
            return;
        }
        if (climate.temperatureModifier == TemperatureModifier.FROZEN) {
            return;
        }
        Category category = event.getCategory();
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
        if (category != Category.JUNGLE && FruitTypeExtension.CHERRY != null) {
            insertFeature(event, allFeatures[i + 3]);
        }
    }

    public static void insertFeature(BiomeLoadingEvent event, ConfiguredFeature<?, ?> cf) {
        if (cf != null)
            event.getGeneration().withFeature(Decoration.VEGETAL_DECORATION, cf);
    }

    public static ConfiguredFeature<BaseTreeFeatureConfig, ?> buildTreeFeature(FruitType type, boolean worldGen, BlockStateProvider carpetProvider) {
        BlockStateProvider leavesProvider;
        List<TreeDecorator> decorators;
        if (worldGen) {
            if (carpetProvider == null) {
                decorators = ImmutableList.of(new BeehiveTreeDecorator(0.05F));
            } else {
                decorators = ImmutableList.of(new BeehiveTreeDecorator(0.05F), new CarpetTreeDecorator(carpetProvider));
            }
            leavesProvider = new WeightedBlockStateProvider().addWeightedBlockstate(type.leaves.getDefaultState(), 2).addWeightedBlockstate(type.leaves.getDefaultState().with(FruitLeavesBlock.AGE, 2), 1);
        } else {
            decorators = ImmutableList.of();
            leavesProvider = new SimpleBlockStateProvider(type.leaves.getDefaultState());
        }
        /* off */
        return Feature.TREE.withConfiguration(
                new BaseTreeFeatureConfig.Builder(
                        new SimpleBlockStateProvider(type.log.getDefaultState()),
                        leavesProvider,
                        new FruitBlobFoliagePlacer(FeatureSpread.create(2), FeatureSpread.create(0), 3),
                        new StraightTrunkPlacer(4, 2, 0),
                        new TwoLayerFeature(1, 0, 1)
                )
                .setIgnoreVines()
                .setDecorators(decorators)
                .build()
        );
        /* on */
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void handleBlockColor(ColorHandlerEvent.Block event) {
        BlockState oakLeaves = Blocks.OAK_LEAVES.getDefaultState();
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
