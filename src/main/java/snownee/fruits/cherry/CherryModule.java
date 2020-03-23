package snownee.fruits.cherry;

import java.util.Collections;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.AxeItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.fruits.Fruits;
import snownee.fruits.MainModule;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.trees.FruitTree;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.cherry.client.particle.PetalParticle;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Group;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.NoItem;
import snownee.kiwi.RenderLayer;
import snownee.kiwi.RenderLayer.Layer;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;

@KiwiModule(name = "cherry")
@KiwiModule.Optional
@KiwiModule.Subscriber({ Bus.MOD, Bus.FORGE })
public class CherryModule extends AbstractModule {

    @SuppressWarnings("hiding")
    public static final class Foods {
        public static final Food CHERRY = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food REDLOVE = new Food.Builder().hunger(5).saturation(0.6f).effect(() -> new EffectInstance(Effects.REGENERATION, 50), 1).build();
    }

    @Group("building_blocks")
    public static final LogBlock CHERRY_LOG = new LogBlock(MaterialColor.DIRT, blockProp(Blocks.OAK_LOG));
    @Group("building_blocks")
    public static final Block CHERRY_WOOD = init(new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD)));
    @Group("building_blocks")
    public static final Block STRIPPED_CHERRY_LOG = init(new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG)));
    @Group("building_blocks")
    public static final Block STRIPPED_CHERRY_WOOD = init(new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD)));
    @Group("building_blocks")
    public static final Block CHERRY_PLANKS = new ModBlock(blockProp(Blocks.JUNGLE_PLANKS));
    @Group("building_blocks")
    public static final SlabBlock CHERRY_SLAB = init(new SlabBlock(blockProp(Blocks.JUNGLE_SLAB)));
    @Group("building_blocks")
    public static final StairsBlock CHERRY_STAIRS = init(new StairsBlock(() -> CHERRY_PLANKS.getDefaultState(), blockProp(Blocks.JUNGLE_STAIRS)));
    @Group("decorations")
    public static final FenceBlock CHERRY_FENCE = init(new FenceBlock(blockProp(Blocks.JUNGLE_FENCE)));
    @Group("redstone")
    public static final FenceGateBlock CHERRY_FENCE_GATE = init(new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE)));
    @Group("redstone")
    public static final TrapDoorBlock CHERRY_TRAPDOOR = new TrapDoorBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
    @Group("redstone")
    @RenderLayer(Layer.CUTOUT)
    public static final DoorBlock CHERRY_DOOR = new DoorBlock(blockProp(Blocks.JUNGLE_DOOR));
    @Group("redstone")
    public static final WoodButtonBlock CHERRY_BUTTON = new WoodButtonBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
    @Group("redstone")
    public static final PressurePlateBlock CHERRY_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.JUNGLE_DOOR));

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final LeavesBlock CHERRY_LEAVES = new CherryLeavesBlock(() -> FruitTypeExtension.CHERRY, blockProp(Blocks.OAK_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final LeavesBlock REDLOVE_LEAVES = new CherryLeavesBlock(() -> FruitTypeExtension.REDLOVE, blockProp(Blocks.OAK_LEAVES));

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final CarpetBlock CHERRY_CARPET = init(new CarpetBlock(DyeColor.PINK, blockProp(CHERRY_LEAVES)));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final CarpetBlock REDLOVE_CARPET = init(new CarpetBlock(DyeColor.WHITE, blockProp(REDLOVE_LEAVES)));

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock CHERRY_SAPLING = new SaplingBlock(new FruitTree(() -> FruitTypeExtension.CHERRY), blockProp(Blocks.OAK_SAPLING));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock REDLOVE_SAPLING = new SaplingBlock(new FruitTree(() -> FruitTypeExtension.REDLOVE), blockProp(Blocks.OAK_SAPLING));

    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_CHERRY = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> CHERRY_SAPLING, blockProp(Blocks.POTTED_OAK_SAPLING));
    @RenderLayer(Layer.CUTOUT)
    @NoItem
    public static final FlowerPotBlock POTTED_REDLOVE = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> REDLOVE_SAPLING, blockProp(Blocks.POTTED_OAK_SAPLING));

    public static final Item CHERRY = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.CHERRY));
    public static final Item REDLOVE = new ModItem(itemProp().group(ItemGroup.FOOD).food(Foods.REDLOVE));

    public static final BasicParticleType PETAL = new BasicParticleType(false);

    static {
        FruitTypeExtension.CHERRY = Fruits.Type.create("CHERRY", CHERRY_LOG, CHERRY_LEAVES, () -> CHERRY_SAPLING, CHERRY);
        FruitTypeExtension.REDLOVE = Fruits.Type.create("REDLOVE", CHERRY_LOG, REDLOVE_LEAVES, () -> REDLOVE_SAPLING, REDLOVE);

        MainModule.ALL_LEAVES.add(CHERRY_LEAVES);
        MainModule.ALL_LEAVES.add(REDLOVE_LEAVES);
    }

    @Override
    protected void init(FMLCommonSetupEvent event) {
        try {
            FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
            pot.addPlant(CHERRY_SAPLING.getRegistryName(), () -> POTTED_CHERRY);
            pot.addPlant(REDLOVE_SAPLING.getRegistryName(), () -> POTTED_REDLOVE);

            if (AxeItem.BLOCK_STRIPPING_MAP instanceof ImmutableMap) {
                AxeItem.BLOCK_STRIPPING_MAP = Collections.synchronizedMap(Maps.newHashMap(AxeItem.BLOCK_STRIPPING_MAP));
            }
            AxeItem.BLOCK_STRIPPING_MAP.put(CHERRY_LOG, STRIPPED_CHERRY_LOG);
            AxeItem.BLOCK_STRIPPING_MAP.put(CHERRY_WOOD, STRIPPED_CHERRY_WOOD);
        } catch (Exception e) {
            Fruits.logger.catching(e);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    protected void clientInit(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(PETAL, PetalParticle.Factory::new);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void handleBlockColor(ColorHandlerEvent.Block event) {
        BlockState birchLeaves = Blocks.BIRCH_LEAVES.getDefaultState();
        BlockColors blockColors = event.getBlockColors();
        blockColors.register((state, world, pos, i) -> {
            if (i == 0) {
                return blockColors.getColor(birchLeaves, world, pos, i);
            }
            if (i == 1) {
                int stage = state.get(FruitLeavesBlock.AGE);
                if (stage < 3) {
                    return blockColors.getColor(birchLeaves, world, pos, i);
                }
                Block block = state.getBlock();
                if (block == REDLOVE_LEAVES)
                    return 0xC22626;
                if (block == CHERRY_LEAVES)
                    return 0xE45B55;
            }
            return -1;
        }, CHERRY_LEAVES, REDLOVE_LEAVES);
    }
}
