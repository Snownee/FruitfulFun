package snownee.fruits.cherry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.fruits.FruitsMod;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.trees.FruitTree;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.cherry.block.SlidingDoorBlock;
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
import snownee.kiwi.util.DeferredActions;

@KiwiModule("cherry")
@KiwiModule.Optional
@KiwiModule.Subscriber(Bus.MOD)
public class CherryModule extends AbstractModule {

    @SuppressWarnings("hiding")
    public static final class Foods {
        public static final Food CHERRY = new Food.Builder().hunger(3).saturation(0.3f).build();
        public static final Food REDLOVE = new Food.Builder().hunger(4).saturation(0.6f).effect(() -> new EffectInstance(Effects.REGENERATION, 50), 1).build();
    }

    @Group("building_blocks")
    public static final RotatedPillarBlock CHERRY_LOG = new RotatedPillarBlock(blockProp(Blocks.OAK_LOG));
    @Group("building_blocks")
    public static final Block CHERRY_WOOD = new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD));
    @Group("building_blocks")
    public static final Block STRIPPED_CHERRY_LOG = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG));
    @Group("building_blocks")
    public static final Block STRIPPED_CHERRY_WOOD = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD));
    @Group("building_blocks")
    public static final Block CHERRY_PLANKS = new ModBlock(blockProp(Blocks.JUNGLE_PLANKS));
    @Group("building_blocks")
    public static final SlabBlock CHERRY_SLAB = new SlabBlock(blockProp(Blocks.JUNGLE_SLAB));
    @Group("building_blocks")
    public static final StairsBlock CHERRY_STAIRS = new StairsBlock(() -> CHERRY_PLANKS.getDefaultState(), blockProp(Blocks.JUNGLE_STAIRS));
    @Group("decorations")
    public static final FenceBlock CHERRY_FENCE = new FenceBlock(blockProp(Blocks.JUNGLE_FENCE));
    @Group("redstone")
    public static final FenceGateBlock CHERRY_FENCE_GATE = new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE));
    @Group("redstone")
    public static final TrapDoorBlock CHERRY_TRAPDOOR = new TrapDoorBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
    @Group("redstone")
    @RenderLayer(Layer.CUTOUT)
    public static final DoorBlock CHERRY_DOOR = new DoorBlock(blockProp(Blocks.JUNGLE_DOOR));
    @Group("redstone")
    public static final SlidingDoorBlock CHERRY_SLIDING_DOOR = new SlidingDoorBlock(blockProp(Blocks.JUNGLE_DOOR));
    @Group("redstone")
    public static final WoodButtonBlock CHERRY_BUTTON = new WoodButtonBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
    @Group("redstone")
    public static final PressurePlateBlock CHERRY_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.JUNGLE_DOOR));

    public static final BasicParticleType PETAL_CHERRY = new BasicParticleType(false);
    public static final BasicParticleType PETAL_REDLOVE = new BasicParticleType(false);

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final LeavesBlock CHERRY_LEAVES = new CherryLeavesBlock(() -> FruitTypeExtension.CHERRY, blockProp(Blocks.OAK_LEAVES), PETAL_CHERRY);
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final LeavesBlock REDLOVE_LEAVES = new CherryLeavesBlock(() -> FruitTypeExtension.REDLOVE, blockProp(Blocks.OAK_LEAVES), PETAL_REDLOVE);

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final CarpetBlock CHERRY_CARPET = new CarpetBlock(DyeColor.PINK, blockProp(CHERRY_LEAVES));
    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final CarpetBlock REDLOVE_CARPET = new CarpetBlock(DyeColor.WHITE, blockProp(REDLOVE_LEAVES));

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

    public static final SoundEvent OPEN_SOUND = new SoundEvent(new ResourceLocation(FruitsMod.MODID, "block.wooden_door.open"));
    public static final SoundEvent CLOSE_SOUND = new SoundEvent(new ResourceLocation(FruitsMod.MODID, "block.wooden_door.close"));

    public static final BannerPattern HEART = BannerPattern.create("HEART", "heart", "hrt", true);
    public static final BannerPatternItem HEART_BANNER_PATTERN = new BannerPatternItem(HEART, itemProp().maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON));

    static {
        FruitTypeExtension.CHERRY = FruitType.create("CHERRY", CHERRY_LOG, CHERRY_LEAVES, () -> CHERRY_SAPLING, CHERRY);
        FruitTypeExtension.REDLOVE = FruitType.create("REDLOVE", CHERRY_LOG, REDLOVE_LEAVES, () -> REDLOVE_SAPLING, REDLOVE);

        CoreModule.ALL_LEAVES.add(CHERRY_LEAVES);
        CoreModule.ALL_LEAVES.add(REDLOVE_LEAVES);
    }

    @Override
    protected void init(FMLCommonSetupEvent event) {
        try {
            FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
            pot.addPlant(CHERRY_SAPLING.getRegistryName(), () -> POTTED_CHERRY);
            pot.addPlant(REDLOVE_SAPLING.getRegistryName(), () -> POTTED_REDLOVE);
        } catch (Exception e) {
            FruitsMod.logger.catching(e);
        }

        DeferredActions.registerAxeConversion(CHERRY_LOG, STRIPPED_CHERRY_LOG);
        DeferredActions.registerAxeConversion(CHERRY_WOOD, STRIPPED_CHERRY_WOOD);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    protected void clientInit(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(PETAL_CHERRY, PetalParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(PETAL_REDLOVE, PetalParticle.Factory::new);
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
