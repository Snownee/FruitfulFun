package snownee.fruits;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.trees.FruitTree;
import snownee.fruits.tile.FruitTreeTile;
import snownee.fruits.world.gen.feature.FruitTreeFeature;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Group;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.NoItem;
import snownee.kiwi.RenderLayer;
import snownee.kiwi.RenderLayer.Layer;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;

@KiwiModule
@KiwiModule.Subscriber({ Bus.MOD, Bus.FORGE })
public final class MainModule extends AbstractModule {

    public static final class Foods {
        public static final Food MANDARIN = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food LIME = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food CITRON = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food POMELO = new Food.Builder().hunger(5).saturation(0.3f).build();
        public static final Food ORANGE = new Food.Builder().hunger(3).saturation(0.5f).build();
        public static final Food LEMON = new Food.Builder().hunger(4).saturation(0.3f).fastToEat().build();
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
    public static final FruitLeavesBlock MANDARIN_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.MANDARIN, blockProp(Blocks.OAK_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock LIME_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.LIME, blockProp(Blocks.OAK_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock CITRON_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.CITRON, blockProp(Blocks.OAK_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock POMELO_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.POMELO, blockProp(Blocks.OAK_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock ORANGE_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.ORANGE, blockProp(Blocks.OAK_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock LEMON_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.LEMON, blockProp(Blocks.OAK_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock GRAPEFRUIT_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.GRAPEFRUIT, blockProp(Blocks.OAK_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final FruitLeavesBlock APPLE_LEAVES = init(new FruitLeavesBlock(() -> Fruits.Type.APPLE, blockProp(Blocks.OAK_LEAVES)));

    public static final Set<Block> ALL_LEAVES = Sets.newConcurrentHashSet(Arrays.asList(MANDARIN_LEAVES, LIME_LEAVES, CITRON_LEAVES, POMELO_LEAVES, ORANGE_LEAVES, LEMON_LEAVES, GRAPEFRUIT_LEAVES, APPLE_LEAVES));
    public static final TileEntityType<FruitTreeTile> FRUIT_TREE = new TileEntityType(() -> new FruitTreeTile(), ALL_LEAVES, null);

    @Group("building_blocks")
    public static final LogBlock CITRUS_LOG = init(new LogBlock(MaterialColor.DIRT, blockProp(Blocks.JUNGLE_LOG)));
    @Group("building_blocks")
    public static final Block CITRUS_WOOD = init(new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD)));
    @Group("building_blocks")
    public static final Block STRIPPED_CITRUS_LOG = init(new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG)));
    @Group("building_blocks")
    public static final Block STRIPPED_CITRUS_WOOD = init(new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD)));
    @Group("building_blocks")
    public static final Block CITRUS_PLANKS = new ModBlock(blockProp(Blocks.JUNGLE_PLANKS));
    @Group("building_blocks")
    public static final SlabBlock CITRUS_SLAB = init(new SlabBlock(blockProp(Blocks.JUNGLE_SLAB)));
    @Group("building_blocks")
    public static final StairsBlock CITRUS_STAIRS = init(new StairsBlock(() -> CITRUS_PLANKS.getDefaultState(), blockProp(Blocks.JUNGLE_STAIRS)));
    @Group("decorations")
    public static final FenceBlock CITRUS_FENCE = init(new FenceBlock(blockProp(Blocks.JUNGLE_FENCE)));
    @Group("redstone")
    public static final FenceGateBlock CITRUS_FENCE_GATE = init(new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE)));
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
    public static final SaplingBlock MANDARIN_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.MANDARIN), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock LIME_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.LIME), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock CITRON_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.CITRON), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock POMELO_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.POMELO), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock ORANGE_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.ORANGE), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock LEMON_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.LEMON), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock GRAPEFRUIT_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.GRAPEFRUIT), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock APPLE_SAPLING = new SaplingBlock(new FruitTree(() -> Fruits.Type.APPLE), blockProp(Blocks.OAK_SAPLING));

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

    public static final FruitTreeFeature FEATURE = new FruitTreeFeature(TreeFeatureConfig::func_227338_a_);

    public MainModule() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FruitsConfig.spec, "fruits.toml");
    }

    @Override
    protected void init(FMLCommonSetupEvent event) {
        FruitsConfig.refresh();

        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        pot.addPlant(MANDARIN_SAPLING.getRegistryName(), () -> POTTED_MANDARIN);
        pot.addPlant(LIME_SAPLING.getRegistryName(), () -> POTTED_LIME);
        pot.addPlant(CITRON_SAPLING.getRegistryName(), () -> POTTED_CITRON);
        pot.addPlant(POMELO_SAPLING.getRegistryName(), () -> POTTED_POMELO);
        pot.addPlant(ORANGE_SAPLING.getRegistryName(), () -> POTTED_ORANGE);
        pot.addPlant(LEMON_SAPLING.getRegistryName(), () -> POTTED_LEMON);
        pot.addPlant(GRAPEFRUIT_SAPLING.getRegistryName(), () -> POTTED_GRAPEFRUIT);
        pot.addPlant(APPLE_SAPLING.getRegistryName(), () -> POTTED_APPLE);

        if (AxeItem.BLOCK_STRIPPING_MAP instanceof ImmutableMap) {
            AxeItem.BLOCK_STRIPPING_MAP = Maps.newHashMap(AxeItem.BLOCK_STRIPPING_MAP);
        }
        AxeItem.BLOCK_STRIPPING_MAP.put(CITRUS_LOG, STRIPPED_CITRUS_LOG);
        AxeItem.BLOCK_STRIPPING_MAP.put(CITRUS_WOOD, STRIPPED_CITRUS_WOOD);

        for (Fruits.Type type : Fruits.Type.values()) {
            ComposterBlock.CHANCES.put(type.fruit, 0.5f);
            ComposterBlock.CHANCES.put(type.leaves.asItem(), 0.3f);
            ComposterBlock.CHANCES.put(type.sapling.get().asItem(), 0.3f);
        }
    }

    @Override
    protected void postInit() {
        List<Fruits.Type> types = Arrays.asList(Fruits.Type.CITRON, Fruits.Type.LIME, Fruits.Type.MANDARIN);
        for (Biome biome : Biome.BIOMES) {
            Biome.RainType rainType = biome.getPrecipitation();
            if (rainType != Biome.RainType.RAIN) {
                continue;
            }
            Biome.Category category = biome.getCategory();
            int count = 0;
            float chance = 0;
            switch (category) {
            case JUNGLE:
                count += 1;
                break;
            case FOREST:
                chance += 0.05f;
                break;
            case PLAINS:
                chance += 0.02f;
                break;
            default:
                continue;
            }
            Biome.TempCategory temp = biome.getTempCategory();
            switch (temp) {
            case WARM:
                chance += 0.05f;
                break;
            case MEDIUM:
                break;
            default:
                continue;
            }
            if (count > 0 || chance > 0) {
                for (Fruits.Type type : types) {
                    ConfiguredFeature<?, ?> cf = buildTreeFeature(type, true);
                    cf = cf.func_227228_a_(Placement.COUNT_EXTRA_HEIGHTMAP.func_227446_a_(new AtSurfaceWithExtraConfig(count, chance, 1)));
                    biome.addFeature(Decoration.VEGETAL_DECORATION, cf);
                }
            }
        }
    }

    public static ConfiguredFeature<TreeFeatureConfig, ?> buildTreeFeature(Fruits.Type type, boolean worldGen) {
        BlockStateProvider leavesProvider;
        if (worldGen) {
            leavesProvider = new WeightedBlockStateProvider().func_227407_a_(type.leaves.getDefaultState(), 2).func_227407_a_(type.leaves.getDefaultState().with(FruitLeavesBlock.AGE, 2), 1);
        } else {
            leavesProvider = new SimpleBlockStateProvider(type.leaves.getDefaultState());
        }
        return FEATURE.func_225566_b_((new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(type.log.getDefaultState()), leavesProvider, new BlobFoliagePlacer(2, 0))).func_225569_d_(4).func_227354_b_(2).func_227360_i_(3).func_227352_a_().setSapling(type.sapling.get()).func_225568_b_());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void handleBlockColor(ColorHandlerEvent.Block event) {
        BlockState oakLeaves = Blocks.OAK_LEAVES.getDefaultState();
        BlockColors blockColors = event.getBlockColors();
        blockColors.register((state, world, pos, i) -> {
            if (i == 0) {
                return blockColors.func_228054_a_(oakLeaves, world, pos, i);
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

    private static final ResourceLocation OAK_LEAVES_LOOT_TABLE = new ResourceLocation("blocks/oak_leaves");

    /*    @SubscribeEvent
    public void tweakLootTable(LootTableLoadEvent event) {
        if (event.getName().equals(OAK_LEAVES_LOOT_TABLE)) {
            LootTable table = event.getTable();
            if (table.isFrozen()) {
                return;
            }
            LootPool pool = table.getPool("main");
            table.removePool("main");
        }
    }*/

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isRemote() && event.getState().getBlock() == Blocks.OAK_LEAVES) {
            if (event.getWorld().getRandom().nextFloat() < FruitsConfig.oakLeavesDropsAppleSapling) {
                Block.spawnAsEntity(event.getWorld().getWorld(), event.getPos(), new ItemStack(APPLE_SAPLING));
            }
        }
    }

}
