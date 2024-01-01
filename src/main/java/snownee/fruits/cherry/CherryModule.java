package snownee.fruits.cherry;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.cherry.item.WreathItem;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.VanillaActions;

@KiwiModule("cherry")
public class CherryModule extends AbstractModule {

	public static final BlockSetType REDLOVE_SET_TYPE = new BlockSetType("fruitfulfun:redlove", true, SoundType.CHERRY_WOOD, SoundEvents.CHERRY_WOOD_DOOR_CLOSE, SoundEvents.CHERRY_WOOD_DOOR_OPEN, SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_ON);
	public static final WoodType REDLOVE_WOOD_TYPE = new WoodType(REDLOVE_SET_TYPE.name(), REDLOVE_SET_TYPE);
	@NoItem
	public static final KiwiGO<Block> REDLOVE_SIGN = go(() -> new StandingSignBlock(blockProp(Blocks.CHERRY_SIGN), REDLOVE_WOOD_TYPE));
	@NoItem
	public static final KiwiGO<Block> REDLOVE_WALL_SIGN = go(() -> new WallSignBlock(blockProp(Blocks.CHERRY_WALL_SIGN), REDLOVE_WOOD_TYPE));
	@Name("redlove_sign")
	@Category(Categories.FUNCTIONAL_BLOCKS)
	public static final KiwiGO<Item> REDLOVE_SIGN_ITEM = go(() -> new SignItem(itemProp().stacksTo(Items.CHERRY_SIGN.getMaxStackSize()), REDLOVE_SIGN.get(), REDLOVE_WALL_SIGN.get()));
	@NoItem
	public static final KiwiGO<Block> REDLOVE_HANGING_SIGN = go(() -> new CeilingHangingSignBlock(blockProp(Blocks.CHERRY_HANGING_SIGN), REDLOVE_WOOD_TYPE));
	@NoItem
	public static final KiwiGO<Block> REDLOVE_WALL_HANGING_SIGN = go(() -> new WallHangingSignBlock(blockProp(Blocks.CHERRY_WALL_HANGING_SIGN), REDLOVE_WOOD_TYPE));
	@Name("redlove_hanging_sign")
	@Category(Categories.FUNCTIONAL_BLOCKS)
	public static final KiwiGO<Item> REDLOVE_HANGING_SIGN_ITEM = go(() -> new HangingSignItem(REDLOVE_HANGING_SIGN.get(), REDLOVE_WALL_HANGING_SIGN.get(), itemProp().stacksTo(Items.CHERRY_HANGING_SIGN.getMaxStackSize())));
	@Category(value = {Categories.BUILDING_BLOCKS, Categories.NATURAL_BLOCKS}, after = {"cherry_button", "cherry_log"})
	public static final KiwiGO<Block> REDLOVE_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.CHERRY_LOG)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.CHERRY_WOOD)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> STRIPPED_REDLOVE_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_CHERRY_LOG)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> STRIPPED_REDLOVE_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_CHERRY_WOOD)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_PLANKS = go(() -> new ModBlock(blockProp(Blocks.CHERRY_PLANKS)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_STAIRS = go(() -> new StairBlock(REDLOVE_PLANKS.getOrCreate().defaultBlockState(), blockProp(Blocks.CHERRY_STAIRS)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_SLAB = go(() -> new SlabBlock(blockProp(Blocks.CHERRY_SLAB)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_FENCE = go(() -> new FenceBlock(blockProp(Blocks.CHERRY_FENCE)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_FENCE_GATE = go(() -> new FenceGateBlock(blockProp(Blocks.CHERRY_FENCE_GATE), REDLOVE_WOOD_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> REDLOVE_DOOR = go(() -> new DoorBlock(blockProp(Blocks.CHERRY_DOOR), REDLOVE_SET_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_SLIDING_DOOR = go(() -> new SlidingDoorBlock(blockProp(Blocks.CHERRY_DOOR), REDLOVE_SET_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_TRAPDOOR = go(() -> new TrapDoorBlock(blockProp(Blocks.CHERRY_TRAPDOOR), REDLOVE_SET_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_PRESSURE_PLATE = go(() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.CHERRY_DOOR), REDLOVE_SET_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_BUTTON = go(() -> Blocks.woodenButton(REDLOVE_SET_TYPE));
	public static final KiwiGO<SimpleParticleType> PETAL_CHERRY = go(() -> new SimpleParticleType(false));
	public static final KiwiGO<SimpleParticleType> PETAL_REDLOVE = go(() -> new SimpleParticleType(false));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> CHERRY_LEAVES = go(() -> new CherryLeavesBlock(CherryFruitTypes.CHERRY, blockProp(Blocks.CHERRY_LEAVES).mapColor(MapColor.COLOR_PINK), PETAL_CHERRY.getOrCreate()));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> REDLOVE_LEAVES = go(() -> new CherryLeavesBlock(CherryFruitTypes.REDLOVE, blockProp(Blocks.CHERRY_LEAVES).mapColor(MapColor.CRIMSON_NYLIUM), PETAL_REDLOVE.getOrCreate()));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<PinkPetalsBlock> PEACH_PINK_PETALS = go(() -> new PinkPetalsBlock(blockProp(Blocks.PINK_PETALS)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> CHERRY_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CherryFruitTypes.CHERRY.getOrCreate()), blockProp(Blocks.CHERRY_SAPLING).mapColor(MapColor.COLOR_PINK)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> REDLOVE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CherryFruitTypes.REDLOVE.getOrCreate()), blockProp(Blocks.CHERRY_SAPLING).mapColor(MapColor.CRIMSON_NYLIUM)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_CHERRY = go(() -> new FlowerPotBlock(CHERRY_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_CHERRY_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_REDLOVE = go(() -> new FlowerPotBlock(REDLOVE_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_CHERRY_SAPLING)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> CHERRY = go(() -> new ModItem(itemProp().food(Foods.CHERRY)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> REDLOVE = go(() -> new ModItem(itemProp().food(Foods.REDLOVE)));
	public static final KiwiGO<BannerPattern> HEART = go(() -> new BannerPattern("hrt"));
	public static final TagKey<BannerPattern> HEART_TAG = tag(Registries.BANNER_PATTERN, FruitfulFun.ID, "pattern_item/heart");
	@Category(Categories.INGREDIENTS)
	public static final KiwiGO<Item> HEART_BANNER_PATTERN = go(() -> new BannerPatternItem(HEART_TAG, itemProp().stacksTo(Items.MOJANG_BANNER_PATTERN.getMaxStackSize()).rarity(Rarity.UNCOMMON)));
	@Category(Categories.TOOLS_AND_UTILITIES)
	public static final KiwiGO<Item> CHERRY_WREATH = go(() -> new WreathItem(itemProp()));
	@Category(Categories.TOOLS_AND_UTILITIES)
	public static final KiwiGO<Item> REDLOVE_WREATH = go(() -> new WreathItem(itemProp()));

	public CherryModule() {
		Hooks.cherry = true;
	}

	@Override
	protected void preInit() {
		CoreModule.createPoiTypes(this);
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			BlockSetType.register(REDLOVE_SET_TYPE);

			VanillaActions.registerAxeConversion(REDLOVE_LOG.get(), STRIPPED_REDLOVE_LOG.get());
			VanillaActions.registerAxeConversion(REDLOVE_WOOD.get(), STRIPPED_REDLOVE_WOOD.get());

			VanillaActions.registerCompostable(0.3F, PEACH_PINK_PETALS.get());
		});
	}

	public static final class Foods {
		public static final FoodProperties CHERRY = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties REDLOVE = new FoodProperties.Builder().nutrition(4).saturationMod(0.6f).effect(new MobEffectInstance(MobEffects.REGENERATION, 50), 1).build();
	}
}
