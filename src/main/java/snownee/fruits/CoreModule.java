package snownee.fruits;

import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
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
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
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
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.fruits.levelgen.foliageplacers.Fruitify;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.ModuleInfo;
import snownee.kiwi.NamedEntry;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.KiwiEntityTypeBuilder;
import snownee.kiwi.util.VanillaActions;

@KiwiModule
public final class CoreModule extends AbstractModule {

	public static final BlockSetType CITRUS_SET_TYPE = new BlockSetType("fruitfulfun:citrus");
	public static final WoodType CITRUS_WOOD_TYPE = new WoodType(CITRUS_SET_TYPE.name(), CITRUS_SET_TYPE);
	@NoItem
	public static final KiwiGO<Block> CITRUS_SIGN = go(() -> new StandingSignBlock(blockProp(Blocks.OAK_SIGN), CITRUS_WOOD_TYPE));
	@NoItem
	public static final KiwiGO<Block> CITRUS_WALL_SIGN = go(() -> new WallSignBlock(blockProp(Blocks.OAK_WALL_SIGN), CITRUS_WOOD_TYPE));
	@Name("citrus_sign")
	@Category(value = Categories.FUNCTIONAL_BLOCKS, after = "cherry_hanging_sign")
	public static final KiwiGO<Item> CITRUS_SIGN_ITEM = go(() -> new SignItem(itemProp().stacksTo(Items.OAK_SIGN.getMaxStackSize()), CITRUS_SIGN.get(), CITRUS_WALL_SIGN.get()));
	@NoItem
	public static final KiwiGO<Block> CITRUS_HANGING_SIGN = go(() -> new CeilingHangingSignBlock(blockProp(Blocks.OAK_HANGING_SIGN), CITRUS_WOOD_TYPE));
	@NoItem
	public static final KiwiGO<Block> CITRUS_WALL_HANGING_SIGN = go(() -> new WallHangingSignBlock(blockProp(Blocks.OAK_WALL_HANGING_SIGN), CITRUS_WOOD_TYPE));
	@Name("citrus_hanging_sign")
	public static final KiwiGO<Item> CITRUS_HANGING_SIGN_ITEM = go(() -> new HangingSignItem(CITRUS_HANGING_SIGN.get(), CITRUS_WALL_HANGING_SIGN.get(), itemProp().stacksTo(Items.OAK_HANGING_SIGN.getMaxStackSize())));
	@Category(value = Categories.FOOD_AND_DRINKS, after = "chorus_fruit")
	public static final KiwiGO<Item> TANGERINE = go(() -> new ModItem(itemProp().food(Foods.TANGERINE)));
	public static final KiwiGO<Item> LIME = go(() -> new ModItem(itemProp().food(Foods.LIME)));
	public static final KiwiGO<Item> CITRON = go(() -> new ModItem(itemProp().food(Foods.CITRON)));
	public static final KiwiGO<Item> POMELO = go(() -> new ModItem(itemProp().food(Foods.POMELO)));
	public static final KiwiGO<Item> ORANGE = go(() -> new ModItem(itemProp().food(Foods.ORANGE)));
	public static final KiwiGO<Item> LEMON = go(() -> new ModItem(itemProp().food(Foods.LEMON)));
	public static final KiwiGO<Item> GRAPEFRUIT = go(() -> new ModItem(itemProp().food(Foods.GRAPEFRUIT)));
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final KiwiGO<FruitLeavesBlock> TANGERINE_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.TANGERINE, blockProp(Blocks.OAK_LEAVES)));
	public static final KiwiGO<FruitLeavesBlock> LIME_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.LIME, blockProp(Blocks.OAK_LEAVES)));
	public static final KiwiGO<FruitLeavesBlock> CITRON_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.CITRON, blockProp(Blocks.OAK_LEAVES)));
	public static final KiwiGO<FruitLeavesBlock> POMELO_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.POMELO, blockProp(Blocks.OAK_LEAVES)));
	public static final KiwiGO<FruitLeavesBlock> ORANGE_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.ORANGE, blockProp(Blocks.OAK_LEAVES)));
	public static final KiwiGO<FruitLeavesBlock> LEMON_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.LEMON, blockProp(Blocks.OAK_LEAVES)));
	public static final KiwiGO<FruitLeavesBlock> GRAPEFRUIT_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.GRAPEFRUIT, blockProp(Blocks.OAK_LEAVES)));
	public static final KiwiGO<FruitLeavesBlock> APPLE_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.APPLE, blockProp(Blocks.OAK_LEAVES)));
	@Category(value = {Categories.BUILDING_BLOCKS, Categories.NATURAL_BLOCKS}, after = {"cherry_button", "cherry_log"})
	public static final KiwiGO<Block> CITRUS_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.OAK_LOG)));
	@Category(value = Categories.BUILDING_BLOCKS, after = "fruitfulfun:citrus_log")
	public static final KiwiGO<Block> CITRUS_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.OAK_WOOD)));
	public static final KiwiGO<Block> STRIPPED_CITRUS_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_OAK_LOG)));
	public static final KiwiGO<Block> STRIPPED_CITRUS_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_OAK_WOOD)));
	public static final KiwiGO<Block> CITRUS_PLANKS = go(() -> new ModBlock(blockProp(Blocks.OAK_PLANKS)));
	public static final KiwiGO<Block> CITRUS_STAIRS = go(() -> new StairBlock(CITRUS_PLANKS.getOrCreate().defaultBlockState(), blockProp(Blocks.OAK_STAIRS)));
	public static final KiwiGO<Block> CITRUS_SLAB = go(() -> new SlabBlock(blockProp(Blocks.OAK_SLAB)));
	public static final KiwiGO<Block> CITRUS_FENCE = go(() -> new FenceBlock(blockProp(Blocks.OAK_FENCE)));
	public static final KiwiGO<Block> CITRUS_FENCE_GATE = go(() -> new FenceGateBlock(blockProp(Blocks.OAK_FENCE_GATE), CITRUS_WOOD_TYPE));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> CITRUS_DOOR = go(() -> new DoorBlock(blockProp(Blocks.OAK_DOOR), CITRUS_SET_TYPE));
	public static final KiwiGO<Block> CITRUS_TRAPDOOR = go(() -> new TrapDoorBlock(blockProp(Blocks.OAK_TRAPDOOR), CITRUS_SET_TYPE));
	public static final KiwiGO<Block> CITRUS_PRESSURE_PLATE = go(() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.OAK_DOOR), CITRUS_SET_TYPE));
	public static final KiwiGO<Block> CITRUS_BUTTON = go(() -> Blocks.woodenButton(CITRUS_SET_TYPE));
	@Category(value = Categories.NATURAL_BLOCKS, after = "cherry_sapling")
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> TANGERINE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.TANGERINE.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> LIME_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.LIME.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> CITRON_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.CITRON.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> POMELO_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.POMELO.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> ORANGE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.ORANGE.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> LEMON_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.LEMON.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> GRAPEFRUIT_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.GRAPEFRUIT.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> APPLE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.APPLE.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_TANGERINE = go(() -> new FlowerPotBlock(TANGERINE_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_LIME = go(() -> new FlowerPotBlock(LIME_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_CITRON = go(() -> new FlowerPotBlock(CITRON_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_POMELO = go(() -> new FlowerPotBlock(POMELO_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_ORANGE = go(() -> new FlowerPotBlock(ORANGE_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_LEMON = go(() -> new FlowerPotBlock(LEMON_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_GRAPEFRUIT = go(() -> new FlowerPotBlock(GRAPEFRUIT_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	@RenderLayer(Layer.CUTOUT)
	@NoItem
	public static final KiwiGO<Block> POTTED_APPLE = go(() -> new FlowerPotBlock(APPLE_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_OAK_SAPLING)));
	public static final TagKey<Block> ALL_LEAVES = blockTag(FruitfulFun.ID, "leaves");
	public static final KiwiGO<FoliagePlacerType<Fruitify>> FRUITIFY = go(() -> new FoliagePlacerType<>(Fruitify.CODEC));
	public static final KiwiGO<BannerPattern> SNOWFLAKE = go(() -> new BannerPattern("sno"));
	public static final TagKey<BannerPattern> SNOWFLAKE_TAG = tag(Registries.BANNER_PATTERN, FruitfulFun.ID, "pattern_item/snowflake");
	public static final KiwiGO<BlockEntityType<FruitTreeBlockEntity>> FRUIT_TREE = blockEntity(FruitTreeBlockEntity::new, null, FruitLeavesBlock.class);
	@Category(value = Categories.INGREDIENTS, after = "piglin_banner_pattern")
	public static final KiwiGO<Item> SNOWFLAKE_BANNER_PATTERN = go(() -> new BannerPatternItem(SNOWFLAKE_TAG, itemProp().stacksTo(Items.MOJANG_BANNER_PATTERN.getMaxStackSize()).rarity(Rarity.UNCOMMON)));
	public static final KiwiGO<SoundEvent> OPEN_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FruitfulFun.ID, "block.wooden_door.open")));
	public static final KiwiGO<SoundEvent> CLOSE_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FruitfulFun.ID, "block.wooden_door.close")));
	/* off */
	public static final KiwiGO<EntityType<SlidingDoorEntity>> SLIDING_DOOR = go(() -> KiwiEntityTypeBuilder.<SlidingDoorEntity>create()
			.entityFactory(SlidingDoorEntity::new)
			.dimensions(EntityDimensions.scalable(0.01f, 0.01f))
			.fireImmune()
			.disableSummon()
			.build()
	);
	/* on */
	public static final TagKey<PoiType> POI_TYPE = AbstractModule.tag(Registries.POINT_OF_INTEREST_TYPE, FruitfulFun.ID, "trees");

	@Override
	protected void preInit() {
		createPoiTypes(this);
		CommonProxy.addBuiltinPacks();
	}

	public static void createPoiTypes(AbstractModule module) {
		ModuleInfo info = KiwiModules.get(module.uid);
		info.getRegistryEntries(BuiltInRegistries.BLOCK)
				.filter($ -> $.entry instanceof FruitLeavesBlock)
				.forEach($ -> {
					Preconditions.checkArgument($.name.getPath().endsWith("_leaves"));
					ResourceLocation id = $.name.withPath($.name.getPath().substring(0, $.name.getPath().length() - 7));
					FruitLeavesBlock block = (FruitLeavesBlock) $.entry;
					info.register(new PoiType(block.getStateDefinition().getPossibleStates().stream()
									.filter(BlockBehaviour.BlockStateBase::hasBlockEntity)
									.collect(Collectors.toSet()), 40, 10),
							id, BuiltInRegistries.POINT_OF_INTEREST_TYPE, null);
				});
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			BlockSetType.register(CITRUS_SET_TYPE);

			VanillaActions.registerAxeConversion(CITRUS_LOG.get(), STRIPPED_CITRUS_LOG.get());
			VanillaActions.registerAxeConversion(CITRUS_WOOD.get(), STRIPPED_CITRUS_WOOD.get());
			for (Holder<FruitType> holder : FFRegistries.FRUIT_TYPE.asHolderIdMap()) {
				ResourceLocation id = holder.unwrapKey().orElseThrow().location();
				FruitType type = holder.value();
				type.receiveKey(id);
				type.poiType = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolderOrThrow(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id));
				PoiTypes.registerBlockStates(type.poiType, type.poiType.value().matchingStates());
				VanillaActions.registerCompostable(0.5f, type.fruit.get());
				VanillaActions.registerCompostable(0.3f, type.leaves.get());
				VanillaActions.registerCompostable(0.3f, type.sapling.get());
				VanillaActions.registerVillagerCollectable(type.fruit.get());
				VanillaActions.registerVillagerCompostable(type.fruit.get());
				VanillaActions.registerVillagerFood(type.fruit.get(), 1);
			}

			KiwiModules.get().stream()
					.filter($ -> $.module.uid.getNamespace().equals(FruitfulFun.ID))
					.flatMap($ -> $.getRegistryEntries(BuiltInRegistries.BLOCK))
					.forEach(CoreModule::setFlammability);
		});
	}

	private static void setFlammability(NamedEntry<Block> entry) {
		Block block = entry.entry;
		String path = entry.name.getPath();
		if (block instanceof LeavesBlock) {
			VanillaActions.setFireInfo(block, 30, 60);
		} else if (block instanceof RotatedPillarBlock) { // logs
			VanillaActions.setFireInfo(block, 5, 5);
		} else if (block instanceof SlabBlock || block instanceof StairBlock || block instanceof FenceBlock || block instanceof FenceGateBlock || path.contains("planks")) {
			VanillaActions.setFireInfo(block, 5, 20);
		}
	}

	public static final class Foods {
		public static final FoodProperties TANGERINE = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties LIME = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties CITRON = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).build();
		public static final FoodProperties POMELO = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build();
		public static final FoodProperties ORANGE = new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).build();
		public static final FoodProperties LEMON = new FoodProperties.Builder().nutrition(2).saturationMod(1f).fast().build();
		public static final FoodProperties GRAPEFRUIT = new FoodProperties.Builder().nutrition(6).saturationMod(0.4f).build();
	}
}
