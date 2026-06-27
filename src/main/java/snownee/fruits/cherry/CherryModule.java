package snownee.fruits.cherry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.ShelfBlock;
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
import snownee.fruits.FFBoats;
import snownee.fruits.FFFruitTypes;
import snownee.fruits.FFTreeGrowers;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.SlidingDoorBlock;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.cherry.item.RedloveItem;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.loader.event.InitEvent;

@KiwiModule(value = "cherry", modId = FruitfulFun.ID, dependencies = "@fruit_types")
public class CherryModule extends AbstractModule {

	public static final BlockSetType REDLOVE_SET_TYPE = new BlockSetType(
			"fruitfulfun:redlove",
			true,
			true,
			true,
			BlockSetType.PressurePlateSensitivity.EVERYTHING,
			SoundType.CHERRY_WOOD,
			SoundEvents.CHERRY_WOOD_DOOR_CLOSE,
			SoundEvents.CHERRY_WOOD_DOOR_OPEN,
			SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE,
			SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN,
			SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.CHERRY_WOOD_BUTTON_CLICK_OFF,
			SoundEvents.CHERRY_WOOD_BUTTON_CLICK_ON);
	public static final WoodType REDLOVE_WOOD_TYPE = new WoodType(REDLOVE_SET_TYPE.name(), REDLOVE_SET_TYPE);
	@NoItem
	public static final BlockObject<Block> REDLOVE_SIGN = block($ -> new StandingSignBlock(REDLOVE_WOOD_TYPE, $), () -> Blocks.CHERRY_SIGN);
	@NoItem
	public static final BlockObject<Block> REDLOVE_WALL_SIGN = block(
			$ -> new WallSignBlock(REDLOVE_WOOD_TYPE, $),
			() -> Blocks.CHERRY_WALL_SIGN);
	@Name("redlove_sign")
	@Category(value = Categories.FUNCTIONAL_BLOCKS, after = "cherry_hanging_sign")
	public static final ItemObject<Item> REDLOVE_SIGN_ITEM = item($ -> new SignItem(
			REDLOVE_SIGN.get(),
			REDLOVE_WALL_SIGN.get(),
			$.stacksTo(16)));
	@NoItem
	public static final BlockObject<Block> REDLOVE_HANGING_SIGN = block(
			$ -> new CeilingHangingSignBlock(REDLOVE_WOOD_TYPE, $),
			() -> Blocks.CHERRY_HANGING_SIGN);
	@NoItem
	public static final BlockObject<Block> REDLOVE_WALL_HANGING_SIGN = block(
			$ -> new WallHangingSignBlock(REDLOVE_WOOD_TYPE, $),
			() -> Blocks.CHERRY_WALL_HANGING_SIGN);
	@Name("redlove_hanging_sign")
	public static final ItemObject<Item> REDLOVE_HANGING_SIGN_ITEM = item($ -> new HangingSignItem(
			REDLOVE_HANGING_SIGN.get(),
			REDLOVE_WALL_HANGING_SIGN.get(),
			$.stacksTo(16)));
	@Category(value = {Categories.BUILDING_BLOCKS, Categories.NATURAL_BLOCKS}, after = {"cherry_button", "cherry_log"})
	public static final BlockObject<Block> REDLOVE_LOG = block(RotatedPillarBlock::new, () -> Blocks.CHERRY_LOG);
	@Category(value = Categories.BUILDING_BLOCKS, after = "fruitfulfun:redlove_log")
	public static final BlockObject<Block> REDLOVE_WOOD = block(RotatedPillarBlock::new, () -> Blocks.CHERRY_WOOD);
	public static final BlockObject<Block> STRIPPED_REDLOVE_LOG = block(RotatedPillarBlock::new, () -> Blocks.STRIPPED_CHERRY_LOG);
	public static final BlockObject<Block> STRIPPED_REDLOVE_WOOD = block(RotatedPillarBlock::new, () -> Blocks.STRIPPED_CHERRY_WOOD);
	public static final BlockObject<Block> REDLOVE_PLANKS = block(ModBlock::new, () -> Blocks.CHERRY_PLANKS);
	public static final BlockObject<Block> REDLOVE_STAIRS = block(
			$ -> new StairBlock(REDLOVE_PLANKS.getOrCreate().defaultBlockState(), $),
			() -> Blocks.CHERRY_STAIRS);
	public static final BlockObject<Block> REDLOVE_SLAB = block(SlabBlock::new, () -> Blocks.CHERRY_SLAB);
	public static final BlockObject<Block> REDLOVE_FENCE = block(FenceBlock::new, () -> Blocks.CHERRY_FENCE);
	public static final BlockObject<Block> REDLOVE_FENCE_GATE = block(
			$ -> new FenceGateBlock(REDLOVE_WOOD_TYPE, $),
			() -> Blocks.CHERRY_FENCE_GATE);
	public static final BlockObject<Block> REDLOVE_DOOR = block($ -> new DoorBlock(REDLOVE_SET_TYPE, $), () -> Blocks.CHERRY_DOOR);
	public static final BlockObject<Block> REDLOVE_SLIDING_DOOR = block(
			$ -> new SlidingDoorBlock(REDLOVE_SET_TYPE, $),
			() -> Blocks.CHERRY_DOOR);
	public static final BlockObject<Block> REDLOVE_TRAPDOOR = block(
			$ -> new TrapDoorBlock(REDLOVE_SET_TYPE, $),
			() -> Blocks.CHERRY_TRAPDOOR);
	public static final BlockObject<Block> REDLOVE_PRESSURE_PLATE = block(
			$ -> new PressurePlateBlock(REDLOVE_SET_TYPE, $),
			() -> Blocks.CHERRY_PRESSURE_PLATE);
	public static final BlockObject<Block> REDLOVE_BUTTON = block(
			$ -> new ButtonBlock(REDLOVE_SET_TYPE, 30, $),
			() -> Blocks.CHERRY_BUTTON);
	@Category(value = Categories.TOOLS_AND_UTILITIES, after = "cherry_boat")
	public static final ItemObject<BoatItem> REDLOVE_BOAT = item($ -> new BoatItem(FFBoats.REDLOVE_BOAT.getOrCreate(), $.stacksTo(1)));
	@Category(value = Categories.FUNCTIONAL_BLOCKS, after = "cherry_shelf")
	public static final BlockObject<ShelfBlock> REDLOVE_SHELF = block(ShelfBlock::new, () -> Blocks.CHERRY_SHELF);
	public static final KiwiGO<SimpleParticleType> PETAL_CHERRY = go(() -> new SimpleParticleType(false));
	public static final KiwiGO<SimpleParticleType> PETAL_REDLOVE = go(() -> new SimpleParticleType(false));
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final BlockObject<FruitLeavesBlock> CHERRY_LEAVES = block(
			$ -> new CherryLeavesBlock(FFFruitTypes.CHERRY.holderOrThrow(), PETAL_CHERRY.getOrCreate(), $.mapColor(MapColor.COLOR_PINK)),
			() -> Blocks.CHERRY_LEAVES);
	public static final BlockObject<FruitLeavesBlock> REDLOVE_LEAVES = block(
			$ -> new CherryLeavesBlock(
					FFFruitTypes.REDLOVE.holderOrThrow(),
					PETAL_REDLOVE.getOrCreate(),
					$.mapColor(MapColor.CRIMSON_NYLIUM)),
			() -> Blocks.CHERRY_LEAVES);
	@Category(value = Categories.NATURAL_BLOCKS, after = "pink_petals")
	public static final BlockObject<FlowerBedBlock> PEACH_PINK_PETALS = block(FlowerBedBlock::new, () -> Blocks.PINK_PETALS);
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_sapling")
	public static final BlockObject<SaplingBlock> CHERRY_SAPLING = block(
			$ -> new SaplingBlock(
					FFTreeGrowers.CHERRY,
					$.mapColor(MapColor.COLOR_PINK)), () -> Blocks.CHERRY_SAPLING);
	public static final BlockObject<SaplingBlock> REDLOVE_SAPLING = block(
			$ -> new SaplingBlock(
					FFTreeGrowers.REDLOVE,
					$.mapColor(MapColor.CRIMSON_NYLIUM)), () -> Blocks.CHERRY_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_CHERRY = block(
			$ -> new FlowerPotBlock(CHERRY_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_CHERRY_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_REDLOVE = block(
			$ -> new FlowerPotBlock(REDLOVE_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_CHERRY_SAPLING);
	@Category(value = Categories.FOOD_AND_DRINKS, after = "chorus_fruit")
	public static final ItemObject<Item> CHERRY = item($ -> new ModItem($.food(Foods.CHERRY)));
	public static final ItemObject<Item> REDLOVE = item($ -> new RedloveItem($.food(
			Foods.REDLOVE,
			Consumable.builder()
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 50)))
					.onConsume(SpeedUpBreedingCooldownConsumeEffect.INSTANCE)
					.build())));
	public static final TagKey<BannerPattern> HEART_TAG = tag(Registries.BANNER_PATTERN, "pattern_item/heart");
	@Category(value = Categories.INGREDIENTS, after = "piglin_banner_pattern")
	public static final ItemObject<Item> HEART_BANNER_PATTERN = CoreModule.bannerPattern(HEART_TAG);
	public static final KiwiGO<SoundEvent> EQUIP_CROWN = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"item.armor.equip_crown")));
	@Category(value = Categories.INGREDIENTS, after = "turtle_helmet")
	public static final ItemObject<FlowerCrownItem> CHERRY_CROWN = flowerCrown(PETAL_CHERRY);
	public static final ItemObject<FlowerCrownItem> REDLOVE_CROWN = flowerCrown(PETAL_REDLOVE);
	public static final KiwiGO<ConsumeEffect.Type<SpeedUpBreedingCooldownConsumeEffect>> SPEED_UP_BREEDING_COOLDOWN = go(() -> new ConsumeEffect.Type<>(
			SpeedUpBreedingCooldownConsumeEffect.CODEC,
			SpeedUpBreedingCooldownConsumeEffect.STREAM_CODEC));

	@Override
	protected void addEntries() {
		CoreModule.createPoiTypes(this);
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			BlockSetType.register(REDLOVE_SET_TYPE);

			Platform.registerAxeConversion(REDLOVE_LOG.get(), STRIPPED_REDLOVE_LOG.get());
			Platform.registerAxeConversion(REDLOVE_WOOD.get(), STRIPPED_REDLOVE_WOOD.get());

			Platform.registerCompostable(0.3F, PEACH_PINK_PETALS.get());
			Platform.setFireInfo(PEACH_PINK_PETALS.get(), 60, 100);

			DispenserBlock.registerBehavior(REDLOVE_BOAT.get(), new BoatDispenseItemBehavior(FFBoats.REDLOVE_BOAT.get()));
		});
	}

	public static ItemObject<FlowerCrownItem> flowerCrown(KiwiGO<SimpleParticleType> particle) {
		return item($ -> new FlowerCrownItem(
				$.delayedComponent(
						DataComponents.EQUIPPABLE,
						_ -> Equippable.builder(EquipmentSlot.HEAD).setEquipSound(EQUIP_CROWN.holderOrThrow()).build()),
				particle.getOrCreate()));
	}

	public static final class Foods {
		public static final FoodProperties CHERRY = new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).build();
		public static final FoodProperties REDLOVE = new FoodProperties.Builder().nutrition(4).saturationModifier(0.6f).build();
	}
}
