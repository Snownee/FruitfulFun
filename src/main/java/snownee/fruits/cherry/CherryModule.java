package snownee.fruits.cherry;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.cherry.item.RedloveItem;
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
	@Category(value = Categories.FUNCTIONAL_BLOCKS, after = "cherry_hanging_sign")
	public static final KiwiGO<Item> REDLOVE_SIGN_ITEM = go(() -> new SignItem(itemProp().stacksTo(Items.CHERRY_SIGN.getMaxStackSize()), REDLOVE_SIGN.get(), REDLOVE_WALL_SIGN.get()));
	@NoItem
	public static final KiwiGO<Block> REDLOVE_HANGING_SIGN = go(() -> new CeilingHangingSignBlock(blockProp(Blocks.CHERRY_HANGING_SIGN), REDLOVE_WOOD_TYPE));
	@NoItem
	public static final KiwiGO<Block> REDLOVE_WALL_HANGING_SIGN = go(() -> new WallHangingSignBlock(blockProp(Blocks.CHERRY_WALL_HANGING_SIGN), REDLOVE_WOOD_TYPE));
	@Name("redlove_hanging_sign")
	public static final KiwiGO<Item> REDLOVE_HANGING_SIGN_ITEM = go(() -> new HangingSignItem(REDLOVE_HANGING_SIGN.get(), REDLOVE_WALL_HANGING_SIGN.get(), itemProp().stacksTo(Items.CHERRY_HANGING_SIGN.getMaxStackSize())));
	@Category(value = {Categories.BUILDING_BLOCKS, Categories.NATURAL_BLOCKS}, after = {"cherry_button", "cherry_log"})
	public static final KiwiGO<Block> REDLOVE_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.CHERRY_LOG)));
	@Category(value = Categories.BUILDING_BLOCKS, after = "fruitfulfun:redlove_log")
	public static final KiwiGO<Block> REDLOVE_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.CHERRY_WOOD)));
	public static final KiwiGO<Block> STRIPPED_REDLOVE_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_CHERRY_LOG)));
	public static final KiwiGO<Block> STRIPPED_REDLOVE_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_CHERRY_WOOD)));
	public static final KiwiGO<Block> REDLOVE_PLANKS = go(() -> new ModBlock(blockProp(Blocks.CHERRY_PLANKS)));
	public static final KiwiGO<Block> REDLOVE_STAIRS = go(() -> new StairBlock(REDLOVE_PLANKS.getOrCreate().defaultBlockState(), blockProp(Blocks.CHERRY_STAIRS)));
	public static final KiwiGO<Block> REDLOVE_SLAB = go(() -> new SlabBlock(blockProp(Blocks.CHERRY_SLAB)));
	public static final KiwiGO<Block> REDLOVE_FENCE = go(() -> new FenceBlock(blockProp(Blocks.CHERRY_FENCE)));
	public static final KiwiGO<Block> REDLOVE_FENCE_GATE = go(() -> new FenceGateBlock(blockProp(Blocks.CHERRY_FENCE_GATE), REDLOVE_WOOD_TYPE));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> REDLOVE_DOOR = go(() -> new DoorBlock(blockProp(Blocks.CHERRY_DOOR), REDLOVE_SET_TYPE));
	public static final KiwiGO<Block> REDLOVE_SLIDING_DOOR = go(() -> new SlidingDoorBlock(blockProp(Blocks.CHERRY_DOOR), REDLOVE_SET_TYPE));
	public static final KiwiGO<Block> REDLOVE_TRAPDOOR = go(() -> new TrapDoorBlock(blockProp(Blocks.CHERRY_TRAPDOOR), REDLOVE_SET_TYPE));
	public static final KiwiGO<Block> REDLOVE_PRESSURE_PLATE = go(() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.CHERRY_DOOR), REDLOVE_SET_TYPE));
	public static final KiwiGO<Block> REDLOVE_BUTTON = go(() -> Blocks.woodenButton(REDLOVE_SET_TYPE));
	public static final KiwiGO<SimpleParticleType> PETAL_CHERRY = go(() -> new SimpleParticleType(false));
	public static final KiwiGO<SimpleParticleType> PETAL_REDLOVE = go(() -> new SimpleParticleType(false));
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final KiwiGO<FruitLeavesBlock> CHERRY_LEAVES = go(() -> new CherryLeavesBlock(CherryFruitTypes.CHERRY, blockProp(Blocks.CHERRY_LEAVES).mapColor(MapColor.COLOR_PINK), PETAL_CHERRY.getOrCreate()));
	public static final KiwiGO<FruitLeavesBlock> REDLOVE_LEAVES = go(() -> new CherryLeavesBlock(CherryFruitTypes.REDLOVE, blockProp(Blocks.CHERRY_LEAVES).mapColor(MapColor.CRIMSON_NYLIUM), PETAL_REDLOVE.getOrCreate()));
	@Category(value = Categories.NATURAL_BLOCKS, after = "pink_petals")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<PinkPetalsBlock> PEACH_PINK_PETALS = go(() -> new PinkPetalsBlock(blockProp(Blocks.PINK_PETALS)));
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_sapling")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> CHERRY_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CherryFruitTypes.CHERRY.getOrCreate()), blockProp(Blocks.CHERRY_SAPLING).mapColor(MapColor.COLOR_PINK)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> REDLOVE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CherryFruitTypes.REDLOVE.getOrCreate()), blockProp(Blocks.CHERRY_SAPLING).mapColor(MapColor.CRIMSON_NYLIUM)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_CHERRY = go(() -> new FlowerPotBlock(CHERRY_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_CHERRY_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_REDLOVE = go(() -> new FlowerPotBlock(REDLOVE_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_CHERRY_SAPLING)));
	@Category(value = Categories.FOOD_AND_DRINKS, after = "chorus_fruit")
	public static final KiwiGO<Item> CHERRY = go(() -> new ModItem(itemProp().food(Foods.CHERRY)));
	public static final KiwiGO<Item> REDLOVE = go(() -> new RedloveItem(itemProp().food(Foods.REDLOVE)));
	public static final KiwiGO<BannerPattern> HEART = go(() -> new BannerPattern("hrt"));
	public static final TagKey<BannerPattern> HEART_TAG = tag(Registries.BANNER_PATTERN, FruitfulFun.ID, "pattern_item/heart");
	@Category(value = Categories.INGREDIENTS, after = "piglin_banner_pattern")
	public static final KiwiGO<Item> HEART_BANNER_PATTERN = go(() -> new BannerPatternItem(HEART_TAG, itemProp().stacksTo(Items.MOJANG_BANNER_PATTERN.getMaxStackSize()).rarity(Rarity.UNCOMMON)));
	@Category(value = Categories.INGREDIENTS, after = "turtle_helmet")
	public static final KiwiGO<Item> CHERRY_CROWN = go(() -> new FlowerCrownItem(itemProp(), PETAL_CHERRY.getOrCreate()));
	public static final KiwiGO<Item> REDLOVE_CROWN = go(() -> new FlowerCrownItem(itemProp(), PETAL_REDLOVE.getOrCreate()));
	public static final KiwiGO<SoundEvent> EQUIP_CROWN = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FruitfulFun.ID, "item.armor.equip_crown")));

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
			VanillaActions.setFireInfo(PEACH_PINK_PETALS.get(), 60, 100);
		});
	}

	public static final class Foods {
		public static final FoodProperties CHERRY = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties REDLOVE = new FoodProperties.Builder().nutrition(4).saturationMod(0.6f).effect(new MobEffectInstance(MobEffects.REGENERATION, 50), 1).build();
	}
}
