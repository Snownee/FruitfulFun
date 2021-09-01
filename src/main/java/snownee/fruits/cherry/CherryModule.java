package snownee.fruits.cherry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.Name;
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
		public static final FoodProperties CHERRY = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties REDLOVE = new FoodProperties.Builder().nutrition(4).saturationMod(0.6f).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 50), 1).build();
	}

	@Category("building_blocks")
	public static RotatedPillarBlock CHERRY_LOG = new RotatedPillarBlock(blockProp(Blocks.OAK_LOG));
	@Category("building_blocks")
	public static Block CHERRY_WOOD = new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD));
	@Category("building_blocks")
	public static Block STRIPPED_CHERRY_LOG = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG));
	@Category("building_blocks")
	public static Block STRIPPED_CHERRY_WOOD = new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD));
	@Category("building_blocks")
	public static Block CHERRY_PLANKS = new ModBlock(blockProp(Blocks.JUNGLE_PLANKS));
	@Category("building_blocks")
	public static SlabBlock CHERRY_SLAB = new SlabBlock(blockProp(Blocks.JUNGLE_SLAB));
	@Category("building_blocks")
	public static StairBlock CHERRY_STAIRS = new StairBlock(() -> CHERRY_PLANKS.defaultBlockState(), blockProp(Blocks.JUNGLE_STAIRS));
	@Category("decorations")
	public static FenceBlock CHERRY_FENCE = new FenceBlock(blockProp(Blocks.JUNGLE_FENCE));
	@Category("redstone")
	public static FenceGateBlock CHERRY_FENCE_GATE = new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE));
	@Category("redstone")
	public static TrapDoorBlock CHERRY_TRAPDOOR = new TrapDoorBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
	@Category("redstone")
	@RenderLayer(Layer.CUTOUT)
	public static DoorBlock CHERRY_DOOR = new DoorBlock(blockProp(Blocks.JUNGLE_DOOR));
	@Category("redstone")
	public static SlidingDoorBlock CHERRY_SLIDING_DOOR = new SlidingDoorBlock(blockProp(Blocks.JUNGLE_DOOR));
	@Category("redstone")
	public static WoodButtonBlock CHERRY_BUTTON = new WoodButtonBlock(blockProp(Blocks.JUNGLE_TRAPDOOR));
	@Category("redstone")
	public static PressurePlateBlock CHERRY_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.JUNGLE_DOOR));

	public static final SimpleParticleType PETAL_CHERRY = new SimpleParticleType(false);
	public static final SimpleParticleType PETAL_REDLOVE = new SimpleParticleType(false);

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static LeavesBlock CHERRY_LEAVES = new CherryLeavesBlock(() -> FruitTypeExtension.CHERRY, blockProp(Blocks.OAK_LEAVES).color(MaterialColor.COLOR_PINK), PETAL_CHERRY);
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static LeavesBlock REDLOVE_LEAVES = new CherryLeavesBlock(() -> FruitTypeExtension.REDLOVE, blockProp(Blocks.OAK_LEAVES).color(MaterialColor.CRIMSON_NYLIUM), PETAL_REDLOVE);

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static CarpetBlock CHERRY_CARPET = new CarpetBlock(blockProp(CHERRY_LEAVES));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static CarpetBlock REDLOVE_CARPET = new CarpetBlock(blockProp(REDLOVE_LEAVES));

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock CHERRY_SAPLING = new SaplingBlock(new FruitTree(() -> FruitTypeExtension.CHERRY), blockProp(Blocks.OAK_SAPLING).color(MaterialColor.COLOR_PINK));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static SaplingBlock REDLOVE_SAPLING = new SaplingBlock(new FruitTree(() -> FruitTypeExtension.REDLOVE), blockProp(Blocks.OAK_SAPLING).color(MaterialColor.CRIMSON_NYLIUM));

	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_CHERRY = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> CHERRY_SAPLING, blockProp(Blocks.POTTED_OAK_SAPLING));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static FlowerPotBlock POTTED_REDLOVE = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> REDLOVE_SAPLING, blockProp(Blocks.POTTED_OAK_SAPLING));

	public static Item CHERRY = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.CHERRY));
	public static Item REDLOVE = new ModItem(itemProp().tab(CreativeModeTab.TAB_FOOD).food(Foods.REDLOVE));

	public static final BannerPattern HEART = BannerPattern.create("HEART", "heart", "hrt", true);
	public static final BannerPatternItem HEART_BANNER_PATTERN = new BannerPatternItem(HEART, itemProp().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON));

	public static final WoodType CHERRY_WOODTYPE = WoodType.create("fruittrees_cherry");
	@NoItem
	public static StandingSignBlock CHERRY_SIGN = new StandingSignBlock(blockProp(Blocks.JUNGLE_SIGN), CHERRY_WOODTYPE);
	@NoItem
	public static WallSignBlock CHERRY_WALL_SIGN = new WallSignBlock(blockProp(Blocks.JUNGLE_WALL_SIGN), CHERRY_WOODTYPE);
	@Name("cherry_sign")
	public static SignItem CHERRY_SIGN_ITEM = new SignItem(itemProp().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), CHERRY_SIGN, CHERRY_WALL_SIGN);

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

		event.enqueueWork(() -> WoodType.register(CHERRY_WOODTYPE));
	}

	@Override
	protected void clientInit(FMLClientSetupEvent event) {
		event.enqueueWork(() -> Sheets.addWoodType(CHERRY_WOODTYPE));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	protected void clientInit(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(PETAL_CHERRY, PetalParticle.Factory::new);
		Minecraft.getInstance().particleEngine.register(PETAL_REDLOVE, PetalParticle.Factory::new);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void handleBlockColor(ColorHandlerEvent.Block event) {
		BlockState birchLeaves = Blocks.BIRCH_LEAVES.defaultBlockState();
		BlockColors blockColors = event.getBlockColors();
		blockColors.register((state, world, pos, i) -> {
			if (i == 0) {
				return blockColors.getColor(birchLeaves, world, pos, i);
			}
			if (i == 1) {
				int stage = state.getValue(FruitLeavesBlock.AGE);
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
