package snownee.fruits.cherry;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import snownee.fruits.FruitsMod;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.cherry.block.SlidingDoorBlock;
import snownee.fruits.cherry.client.particle.PetalParticle;
import snownee.fruits.cherry.datagen.CherryBlockLoot;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.datagen.provider.KiwiLootTableProvider;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.VanillaActions;

@KiwiModule("cherry")
@KiwiModule.Subscriber(modBus = true)
public class CherryModule extends AbstractModule {

	public static final class Foods {
		public static final FoodProperties CHERRY = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties REDLOVE = new FoodProperties.Builder().nutrition(4).saturationMod(0.6f).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 50), 1).build();
	}

	@Category("building_blocks")
	public static final KiwiGO<Block> CHERRY_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.OAK_LOG)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CHERRY_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.JUNGLE_WOOD)));
	@Category("building_blocks")
	public static final KiwiGO<Block> STRIPPED_CHERRY_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_LOG)));
	@Category("building_blocks")
	public static final KiwiGO<Block> STRIPPED_CHERRY_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_JUNGLE_WOOD)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CHERRY_PLANKS = go(() -> new ModBlock(blockProp(Blocks.JUNGLE_PLANKS)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CHERRY_SLAB = go(() -> new SlabBlock(blockProp(Blocks.JUNGLE_SLAB)));
	@Category("building_blocks")
	public static final KiwiGO<Block> CHERRY_STAIRS = go(() -> new StairBlock(() -> CHERRY_PLANKS.defaultBlockState(), blockProp(Blocks.JUNGLE_STAIRS)));
	@Category("decorations")
	public static final KiwiGO<Block> CHERRY_FENCE = go(() -> new FenceBlock(blockProp(Blocks.JUNGLE_FENCE)));
	@Category("redstone")
	public static final KiwiGO<Block> CHERRY_FENCE_GATE = go(() -> new FenceGateBlock(blockProp(Blocks.JUNGLE_FENCE_GATE)));
	@Category("redstone")
	public static final KiwiGO<Block> CHERRY_TRAPDOOR = go(() -> new TrapDoorBlock(blockProp(Blocks.JUNGLE_TRAPDOOR)));
	@Category("redstone")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> CHERRY_DOOR = go(() -> new DoorBlock(blockProp(Blocks.JUNGLE_DOOR)));
	@Category("redstone")
	public static final KiwiGO<Block> CHERRY_SLIDING_DOOR = go(() -> new SlidingDoorBlock(blockProp(Blocks.JUNGLE_DOOR)));
	@Category("redstone")
	public static final KiwiGO<Block> CHERRY_BUTTON = go(() -> new WoodButtonBlock(blockProp(Blocks.JUNGLE_TRAPDOOR)));
	@Category("redstone")
	public static final KiwiGO<Block> CHERRY_PRESSURE_PLATE = go(() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.JUNGLE_DOOR)));

	public static final KiwiGO<SimpleParticleType> PETAL_CHERRY = go(() -> new SimpleParticleType(false));
	public static final KiwiGO<SimpleParticleType> PETAL_REDLOVE = go(() -> new SimpleParticleType(false));

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> CHERRY_LEAVES = go(() -> new CherryLeavesBlock(CherryFruitTypes.CHERRY, blockProp(Blocks.OAK_LEAVES).color(MaterialColor.COLOR_PINK), PETAL_CHERRY.get()));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> REDLOVE_LEAVES = go(() -> new CherryLeavesBlock(CherryFruitTypes.REDLOVE, blockProp(Blocks.OAK_LEAVES).color(MaterialColor.CRIMSON_NYLIUM), PETAL_REDLOVE.get()));

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> CHERRY_CARPET = go(() -> new CarpetBlock(blockProp(CHERRY_LEAVES.get())));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> REDLOVE_CARPET = go(() -> new CarpetBlock(blockProp(REDLOVE_LEAVES.get())));

	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> CHERRY_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CherryFruitTypes.CHERRY), blockProp(Blocks.OAK_SAPLING).color(MaterialColor.COLOR_PINK)));
	@Category("decorations")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> REDLOVE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CherryFruitTypes.REDLOVE), blockProp(Blocks.OAK_SAPLING).color(MaterialColor.CRIMSON_NYLIUM)));

	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_CHERRY = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CHERRY_SAPLING, blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_REDLOVE = go(() -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, REDLOVE_SAPLING, blockProp(Blocks.POTTED_OAK_SAPLING)));

	@Category("food")
	public static final KiwiGO<Item> CHERRY = go(() -> new ModItem(itemProp().food(Foods.CHERRY)));
	@Category("food")
	public static final KiwiGO<Item> REDLOVE = go(() -> new ModItem(itemProp().food(Foods.REDLOVE)));

	public static final KiwiGO<BannerPattern> HEART = go(() -> new BannerPattern("hrt"));

	public static final TagKey<BannerPattern> HEART_TAG = tag(Registry.BANNER_PATTERN_REGISTRY, FruitsMod.ID, "pattern_item/heart");

	@SuppressWarnings("deprecation")
	@Category("misc")
	public static final KiwiGO<Item> HEART_BANNER_PATTERN = go(() -> new BannerPatternItem(HEART_TAG, itemProp().stacksTo(Items.MOJANG_BANNER_PATTERN.getMaxStackSize()).rarity(Rarity.UNCOMMON)));

	public static final WoodType CHERRY_WOODTYPE = WoodType.create("fruittrees:cherry");
	@NoItem
	public static final KiwiGO<Block> CHERRY_SIGN = go(() -> new StandingSignBlock(blockProp(Blocks.JUNGLE_SIGN), CHERRY_WOODTYPE));
	@NoItem
	public static final KiwiGO<Block> CHERRY_WALL_SIGN = go(() -> new WallSignBlock(blockProp(Blocks.JUNGLE_WALL_SIGN), CHERRY_WOODTYPE));
	@SuppressWarnings("deprecation")
	@Name("cherry_sign")
	@Category("decorations")
	public static final KiwiGO<Item> CHERRY_SIGN_ITEM = go(() -> new SignItem(itemProp().stacksTo(Items.OAK_SIGN.getMaxStackSize()), CHERRY_SIGN.get(), CHERRY_WALL_SIGN.get()));

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
			pot.addPlant(CHERRY_SAPLING.key(), POTTED_CHERRY);
			pot.addPlant(REDLOVE_SAPLING.key(), POTTED_REDLOVE);

			VanillaActions.registerAxeConversion(CHERRY_LOG.get(), STRIPPED_CHERRY_LOG.get());
			VanillaActions.registerAxeConversion(CHERRY_WOOD.get(), STRIPPED_CHERRY_WOOD.get());

			VanillaActions.registerCompostable(0.1F, CHERRY_CARPET.get());
			VanillaActions.registerCompostable(0.1F, REDLOVE_CARPET.get());

			WoodType.register(CHERRY_WOODTYPE);
		});
	}

	@Override
	public void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeServer(), new KiwiLootTableProvider(generator).add(CherryBlockLoot::new, LootContextParamSets.BLOCK));
	}

	@SubscribeEvent
	protected void clientInit(TextureStitchEvent.Pre event) {
		Sheets.addWoodType(CHERRY_WOODTYPE);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	protected void clientInit(RegisterParticleProvidersEvent event) {
		event.register(PETAL_CHERRY.get(), PetalParticle.Factory::new);
		event.register(PETAL_REDLOVE.get(), PetalParticle.Factory::new);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void handleBlockColor(RegisterColorHandlersEvent.Block event) {
		BlockState birchLeaves = Blocks.BIRCH_LEAVES.defaultBlockState();
		BlockColors blockColors = event.getBlockColors();
		event.register((state, world, pos, i) -> {
			if (i == 0) {
				return blockColors.getColor(birchLeaves, world, pos, i);
			}
			if (i == 1) {
				int stage = state.getValue(FruitLeavesBlock.AGE);
				if (stage < 3) {
					return blockColors.getColor(birchLeaves, world, pos, i);
				}
				if (REDLOVE_LEAVES.is(state))
					return 0xC22626;
				if (CHERRY_LEAVES.is(state))
					return 0xE45B55;
			}
			return -1;
		}, CHERRY_LEAVES.get(), REDLOVE_LEAVES.get());
	}
}
