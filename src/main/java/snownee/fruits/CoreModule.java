package snownee.fruits;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.block.entity.SlidingDoorEntity;
import snownee.fruits.levelgen.foliageplacers.Fruitify;
import snownee.fruits.ritual.CollectDragonBreathDispenseBehavior;
import snownee.fruits.util.CommonProxy;
import snownee.fruits.util.ExtinguishFireConsumeEffect;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.KiwiModuleContainer;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.loader.event.InitEvent;

@KiwiModule(modId = FruitfulFun.ID, dependencies = "@fruit_types")
public final class CoreModule extends AbstractModule {

	public static final BlockSetType CITRUS_SET_TYPE = new BlockSetType("fruitfulfun:citrus");
	public static final WoodType CITRUS_WOOD_TYPE = new WoodType(CITRUS_SET_TYPE.name(), CITRUS_SET_TYPE);
	@NoItem
	public static final BlockObject<Block> CITRUS_SIGN = block($ -> new StandingSignBlock(CITRUS_WOOD_TYPE, $), () -> Blocks.OAK_SIGN);
	@NoItem
	public static final BlockObject<Block> CITRUS_WALL_SIGN = block(
			$ -> new WallSignBlock(CITRUS_WOOD_TYPE, $),
			() -> Blocks.OAK_WALL_SIGN);
	@Name("citrus_sign")
	@Category(value = Categories.FUNCTIONAL_BLOCKS, after = "cherry_hanging_sign")
	public static final ItemObject<Item> CITRUS_SIGN_ITEM = item($ -> new SignItem(
			CITRUS_SIGN.get(),
			CITRUS_WALL_SIGN.get(),
			$.stacksTo(16)));
	@NoItem
	public static final BlockObject<Block> CITRUS_HANGING_SIGN = block(
			$ -> new CeilingHangingSignBlock(CITRUS_WOOD_TYPE, $),
			() -> Blocks.OAK_HANGING_SIGN);
	@NoItem
	public static final BlockObject<Block> CITRUS_WALL_HANGING_SIGN = block(
			$ -> new WallHangingSignBlock(CITRUS_WOOD_TYPE, $),
			() -> Blocks.OAK_WALL_HANGING_SIGN);
	@Name("citrus_hanging_sign")
	public static final ItemObject<Item> CITRUS_HANGING_SIGN_ITEM = item($ -> new HangingSignItem(
			CITRUS_HANGING_SIGN.get(),
			CITRUS_WALL_HANGING_SIGN.get(),
			$.stacksTo(16)));
	public static final TagKey<Item> CITRUS_FRUITS = itemTag("c", "fruits/citrus");
	@Category(value = Categories.FOOD_AND_DRINKS, after = "chorus_fruit")
	public static final ItemObject<Item> TANGERINE = citrusFood(Foods.TANGERINE);
	public static final ItemObject<Item> LIME = citrusFood(Foods.LIME);
	public static final ItemObject<Item> CITRON = citrusFood(Foods.CITRON);
	public static final ItemObject<Item> POMELO = citrusFood(Foods.POMELO);
	public static final ItemObject<Item> ORANGE = citrusFood(Foods.ORANGE);
	public static final ItemObject<Item> LEMON = citrusFood(Foods.LEMON, $ -> $.consumeSeconds(0.6F));
	public static final ItemObject<Item> GRAPEFRUIT = citrusFood(Foods.GRAPEFRUIT);
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final BlockObject<FruitLeavesBlock> TANGERINE_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.TANGERINE.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	public static final BlockObject<FruitLeavesBlock> LIME_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.LIME.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	public static final BlockObject<FruitLeavesBlock> CITRON_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.CITRON.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	public static final BlockObject<FruitLeavesBlock> POMELO_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.POMELO.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	public static final BlockObject<FruitLeavesBlock> ORANGE_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.ORANGE.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	public static final BlockObject<FruitLeavesBlock> LEMON_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.LEMON.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	public static final BlockObject<FruitLeavesBlock> GRAPEFRUIT_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.GRAPEFRUIT.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	public static final BlockObject<FruitLeavesBlock> APPLE_LEAVES = block(
			$ -> new FruitLeavesBlock(
					FFFruitTypes.APPLE.holderOrThrow(),
					0.01F,
					$), () -> Blocks.OAK_LEAVES);
	@Category(value = {Categories.BUILDING_BLOCKS, Categories.NATURAL_BLOCKS}, after = {"cherry_button", "cherry_log"})
	public static final BlockObject<Block> CITRUS_LOG = block(RotatedPillarBlock::new, () -> Blocks.OAK_LOG);
	@Category(value = Categories.BUILDING_BLOCKS, after = "fruitfulfun:citrus_log")
	public static final BlockObject<Block> CITRUS_WOOD = block(RotatedPillarBlock::new, () -> Blocks.OAK_WOOD);
	public static final BlockObject<Block> STRIPPED_CITRUS_LOG = block(RotatedPillarBlock::new, () -> Blocks.STRIPPED_OAK_LOG);
	public static final BlockObject<Block> STRIPPED_CITRUS_WOOD = block(RotatedPillarBlock::new, () -> Blocks.STRIPPED_OAK_WOOD);
	public static final BlockObject<Block> CITRUS_PLANKS = block(ModBlock::new, () -> Blocks.OAK_PLANKS);
	public static final BlockObject<Block> CITRUS_STAIRS = block(
			$ -> new StairBlock(CITRUS_PLANKS.getOrCreate().defaultBlockState(), $),
			() -> Blocks.OAK_STAIRS);
	public static final BlockObject<Block> CITRUS_SLAB = block(SlabBlock::new, () -> Blocks.OAK_SLAB);
	public static final BlockObject<Block> CITRUS_FENCE = block(FenceBlock::new, () -> Blocks.OAK_FENCE);
	public static final BlockObject<Block> CITRUS_FENCE_GATE = block(
			$ -> new FenceGateBlock(CITRUS_WOOD_TYPE, $),
			() -> Blocks.OAK_FENCE_GATE);
	public static final BlockObject<Block> CITRUS_DOOR = block($ -> new DoorBlock(CITRUS_SET_TYPE, $), () -> Blocks.OAK_DOOR);
	public static final BlockObject<Block> CITRUS_TRAPDOOR = block($ -> new TrapDoorBlock(CITRUS_SET_TYPE, $), () -> Blocks.OAK_TRAPDOOR);
	public static final BlockObject<Block> CITRUS_PRESSURE_PLATE = block(
			$ -> new PressurePlateBlock(CITRUS_SET_TYPE, $),
			() -> Blocks.OAK_PRESSURE_PLATE);
	public static final BlockObject<Block> CITRUS_BUTTON = block($ -> new ButtonBlock(CITRUS_SET_TYPE, 30, $), () -> Blocks.OAK_BUTTON);
	@Category(value = Categories.TOOLS_AND_UTILITIES, after = "cherry_boat")
	public static final ItemObject<BoatItem> CITRUS_BOAT = item($ -> new BoatItem(FFBoats.CITRUS_BOAT.getOrCreate(), $.stacksTo(1)));
	@Category(value = Categories.FUNCTIONAL_BLOCKS, after = "cherry_shelf")
	public static final BlockObject<ShelfBlock> CITRUS_SHELF = block(ShelfBlock::new, () -> Blocks.OAK_SHELF);
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_sapling")
	public static final BlockObject<SaplingBlock> TANGERINE_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.TANGERINE, $),
			() -> Blocks.OAK_SAPLING);
	public static final BlockObject<SaplingBlock> LIME_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.LIME, $),
			() -> Blocks.OAK_SAPLING);
	public static final BlockObject<SaplingBlock> CITRON_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.CITRON, $),
			() -> Blocks.OAK_SAPLING);
	public static final BlockObject<SaplingBlock> POMELO_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.POMELO, $),
			() -> Blocks.OAK_SAPLING);
	public static final BlockObject<SaplingBlock> ORANGE_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.ORANGE, $),
			() -> Blocks.OAK_SAPLING);
	public static final BlockObject<SaplingBlock> LEMON_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.LEMON, $),
			() -> Blocks.OAK_SAPLING);
	public static final BlockObject<SaplingBlock> GRAPEFRUIT_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.GRAPEFRUIT, $),
			() -> Blocks.OAK_SAPLING);
	public static final BlockObject<SaplingBlock> APPLE_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.APPLE, $),
			() -> Blocks.OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_TANGERINE = block(
			$ -> new FlowerPotBlock(TANGERINE_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_LIME = block(
			$ -> new FlowerPotBlock(LIME_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_CITRON = block(
			$ -> new FlowerPotBlock(CITRON_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_POMELO = block(
			$ -> new FlowerPotBlock(POMELO_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_ORANGE = block(
			$ -> new FlowerPotBlock(ORANGE_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_LEMON = block(
			$ -> new FlowerPotBlock(LEMON_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_GRAPEFRUIT = block(
			$ -> new FlowerPotBlock(GRAPEFRUIT_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	@NoItem
	public static final BlockObject<Block> POTTED_APPLE = block(
			$ -> new FlowerPotBlock(APPLE_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_OAK_SAPLING);
	public static final TagKey<Block> ALL_LEAVES = blockTag("leaves");
	public static final KiwiGO<FoliagePlacerType<Fruitify>> FRUITIFY = go(() -> new FoliagePlacerType<>(Fruitify.CODEC));
	public static final TagKey<BannerPattern> SNOWFLAKE_TAG = tag(Registries.BANNER_PATTERN, "pattern_item/snowflake");
	public static final KiwiGO<BlockEntityType<FruitTreeBlockEntity>> FRUIT_TREE = blockEntity(
			FruitTreeBlockEntity::new,
			FruitLeavesBlock.class);
	@Category(value = Categories.INGREDIENTS, after = "piglin_banner_pattern")
	public static final ItemObject<Item> SNOWFLAKE_BANNER_PATTERN = bannerPattern(SNOWFLAKE_TAG);
	public static final KiwiGO<SoundEvent> OPEN_SOUND = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id("block.wooden_door.open")));
	public static final KiwiGO<SoundEvent> CLOSE_SOUND = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"block.wooden_door.close")));
	/* off */
	public static final KiwiGO<EntityType<SlidingDoorEntity>> SLIDING_DOOR = entity($ -> EntityType.Builder.of(
			SlidingDoorEntity::new,
			MobCategory.MISC).sized(0.01f, 0.01f).fireImmune().noSummon().build($));
	/* on */
	public static final TagKey<PoiType> POI_TYPE = tag(Registries.POINT_OF_INTEREST_TYPE, "trees");
	public static final TagKey<Block> CANDLES = blockTag("candles");
	public static final KiwiGO<MobEffect> FRAGILITY = go(() -> new MobEffect(MobEffectCategory.HARMFUL, 0x875A49));
	public static final KiwiGO<ConsumeEffect.Type<ExtinguishFireConsumeEffect>> EXTINGUISH_FIRE = go(() -> new ConsumeEffect.Type<>(
			ExtinguishFireConsumeEffect.CODEC,
			ExtinguishFireConsumeEffect.STREAM_CODEC));
	public static final TagKey<Instrument> HORN_HARVESTING_INSTRUMENT = tag(Registries.INSTRUMENT, "horn_harvesting_instrument");

	@Override
	protected void addEntries() {
		createPoiTypes(this);
		CommonProxy.addBuiltinPacks();
	}

	public static void createPoiTypes(AbstractModule module) {
		KiwiModuleContainer container = KiwiModules.get(Objects.requireNonNull(module.uid));
		container.getRegistryEntries(Registries.BLOCK).filter($ -> $.getOrCreate() instanceof FruitLeavesBlock).forEach($ -> {
			Preconditions.checkArgument($.key().getPath().endsWith("_leaves"));
			Identifier id = $.key().withPath($.key().getPath().substring(0, $.key().getPath().length() - 7));
			FruitLeavesBlock block = (FruitLeavesBlock) $.get();
			KiwiGO<PoiType> go = go(() -> new PoiType(
					block.getStateDefinition()
							.getPossibleStates()
							.stream()
							.filter(BlockBehaviour.BlockStateBase::hasBlockEntity)
							.collect(Collectors.toSet()), 0, 3));
			go.preRegister(id);
			container.register(go);
		});
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			BlockSetType.register(CITRUS_SET_TYPE);

			Platform.registerAxeConversion(CITRUS_LOG.get(), STRIPPED_CITRUS_LOG.get());
			Platform.registerAxeConversion(CITRUS_WOOD.get(), STRIPPED_CITRUS_WOOD.get());
			for (Holder<FruitType> holder : FFRegistries.FRUIT_TYPE.asHolderIdMap()) {
				Identifier id = holder.unwrapKey().orElseThrow().identifier();
				FruitType type = holder.value();
				type.poiType = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(ResourceKey.create(
						Registries.POINT_OF_INTEREST_TYPE,
						id));
				PoiTypes.registerBlockStates(type.poiType, type.poiType.value().matchingStates());
				Platform.registerCompostable(0.5f, type.fruit.get());
				Platform.registerCompostable(0.3f, type.leaves.get());
				Platform.registerCompostable(0.3f, type.sapling.get());
				Platform.registerVillagerCompostable(type.fruit.get());
				Platform.registerVillagerFood(type.fruit.get(), 1);
			}

			KiwiModules.get()
					.stream()
					.filter($ -> Objects.requireNonNull($.module.uid).getNamespace().equals(FruitfulFun.ID))
					.flatMap($ -> $.getRegistryEntries(Registries.BLOCK))
					.forEach(CoreModule::setFlammability);

			DispenserBlock.registerBehavior(CITRUS_BOAT.get(), new BoatDispenseItemBehavior(FFBoats.CITRUS_BOAT.get()));

			if (FFCommonConfig.dispenserCollectDragonBreath) {
				DispenseItemBehavior original = DispenserBlock.DISPENSER_REGISTRY.get(Items.GLASS_BOTTLE);
				if (original != null) {
					DispenserBlock.registerBehavior(Items.GLASS_BOTTLE, new CollectDragonBreathDispenseBehavior(original));
				}
			}
		});
	}

	private static void setFlammability(KiwiGO<Block> entry) {
		Block block = entry.get();
		String path = entry.key().getPath();
		if (block instanceof LeavesBlock) {
			Platform.setFireInfo(block, 30, 60);
		} else if (block instanceof RotatedPillarBlock) { // logs
			Platform.setFireInfo(block, 5, 5);
		} else if (block instanceof SlabBlock || block instanceof StairBlock || block instanceof FenceBlock ||
				block instanceof FenceGateBlock || path.contains("planks")) {
			Platform.setFireInfo(block, 5, 20);
		}
	}

	public static ItemObject<Item> bannerPattern(TagKey<BannerPattern> tag) {
		return item($ -> new Item($.stacksTo(1)
				.rarity(Rarity.UNCOMMON)
				.delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS, context -> context.getOrThrow(tag))));
	}

	public static ItemObject<Item> citrusFood(FoodProperties foodProperties) {
		return citrusFood(foodProperties, null);
	}

	public static ItemObject<Item> citrusFood(FoodProperties foodProperties, @Nullable Consumer<Consumable.Builder> builderConsumer) {
		Consumable.Builder builder = Consumable.builder();
		builder.onConsume(ExtinguishFireConsumeEffect.INSTANCE);
		if (builderConsumer != null) {
			builderConsumer.accept(builder);
		}
		return item($ -> new ModItem($.food(foodProperties, builder.build())));
	}

	public static void addWanderingTraderTrades(MerchantOffers offers, WanderingTrader trader) {
		ItemStack sapling = Util.getRandom(
						FFRegistries.FRUIT_TYPE.stream().filter($ -> $.tier == 0).map($ -> $.sapling.get()).toList(),
						trader.getRandom())
				.asItem()
				.getDefaultInstance();
		offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, FFCommonConfig.wanderingTraderSaplingPrice), sapling, 5, 1, 1));
	}

	public static final class Foods {
		public static final FoodProperties TANGERINE = new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).build();
		public static final FoodProperties LIME = new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).build();
		public static final FoodProperties CITRON = new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).build();
		public static final FoodProperties POMELO = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).build();
		public static final FoodProperties ORANGE = new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build();
		public static final FoodProperties LEMON = new FoodProperties.Builder().nutrition(2).saturationModifier(1f).build();
		public static final FoodProperties GRAPEFRUIT = new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build();
	}
}
