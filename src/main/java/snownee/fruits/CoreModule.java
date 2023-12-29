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
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.fruits.cherry.block.SlidingDoorEntity;
import snownee.fruits.levelgen.foliageplacers.Fruitify;
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
	@Category(Categories.FUNCTIONAL_BLOCKS)
	public static final KiwiGO<Item> CITRUS_SIGN_ITEM = go(() -> new SignItem(itemProp().stacksTo(Items.OAK_SIGN.getMaxStackSize()), CITRUS_SIGN.get(), CITRUS_WALL_SIGN.get()));
	@NoItem
	public static final KiwiGO<Block> CITRUS_HANGING_SIGN = go(() -> new CeilingHangingSignBlock(blockProp(Blocks.OAK_HANGING_SIGN), CITRUS_WOOD_TYPE));
	@NoItem
	public static final KiwiGO<Block> CITRUS_WALL_HANGING_SIGN = go(() -> new WallHangingSignBlock(blockProp(Blocks.OAK_WALL_HANGING_SIGN), CITRUS_WOOD_TYPE));
	@Name("citrus_hanging_sign")
	@Category(Categories.FUNCTIONAL_BLOCKS)
	public static final KiwiGO<Item> CITRUS_HANGING_SIGN_ITEM = go(() -> new HangingSignItem(CITRUS_HANGING_SIGN.get(), CITRUS_WALL_HANGING_SIGN.get(), itemProp().stacksTo(Items.OAK_HANGING_SIGN.getMaxStackSize())));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> TANGERINE = go(() -> new ModItem(itemProp().food(Foods.TANGERINE)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> LIME = go(() -> new ModItem(itemProp().food(Foods.LIME)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> CITRON = go(() -> new ModItem(itemProp().food(Foods.CITRON)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> POMELO = go(() -> new ModItem(itemProp().food(Foods.POMELO)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> ORANGE = go(() -> new ModItem(itemProp().food(Foods.ORANGE)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> LEMON = go(() -> new ModItem(itemProp().food(Foods.LEMON)));
	@Category(Categories.FOOD_AND_DRINKS)
	public static final KiwiGO<Item> GRAPEFRUIT = go(() -> new ModItem(itemProp().food(Foods.GRAPEFRUIT)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> TANGERINE_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.TANGERINE, blockProp(Blocks.OAK_LEAVES)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> LIME_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.LIME, blockProp(Blocks.OAK_LEAVES)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> CITRON_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.CITRON, blockProp(Blocks.OAK_LEAVES)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> POMELO_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.POMELO, blockProp(Blocks.OAK_LEAVES)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> ORANGE_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.ORANGE, blockProp(Blocks.OAK_LEAVES)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> LEMON_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.LEMON, blockProp(Blocks.OAK_LEAVES)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> GRAPEFRUIT_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.GRAPEFRUIT, blockProp(Blocks.OAK_LEAVES)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<FruitLeavesBlock> APPLE_LEAVES = go(() -> new FruitLeavesBlock(CoreFruitTypes.APPLE, blockProp(Blocks.OAK_LEAVES)));
	@Category(value = {Categories.BUILDING_BLOCKS, Categories.NATURAL_BLOCKS}, after = {"cherry_button", "cherry_log"})
	public static final KiwiGO<Block> CITRUS_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.OAK_LOG)));
	@Category(value = Categories.BUILDING_BLOCKS, after = "fruitfulfun:citrus_log")
	public static final KiwiGO<Block> CITRUS_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.OAK_WOOD)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> STRIPPED_CITRUS_LOG = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_OAK_LOG)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> STRIPPED_CITRUS_WOOD = go(() -> new RotatedPillarBlock(blockProp(Blocks.STRIPPED_OAK_WOOD)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_PLANKS = go(() -> new ModBlock(blockProp(Blocks.OAK_PLANKS)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_STAIRS = go(() -> new StairBlock(CITRUS_PLANKS.getOrCreate().defaultBlockState(), blockProp(Blocks.OAK_STAIRS)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_SLAB = go(() -> new SlabBlock(blockProp(Blocks.OAK_SLAB)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_FENCE = go(() -> new FenceBlock(blockProp(Blocks.OAK_FENCE)));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_FENCE_GATE = go(() -> new FenceGateBlock(blockProp(Blocks.OAK_FENCE_GATE), CITRUS_WOOD_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> CITRUS_DOOR = go(() -> new DoorBlock(blockProp(Blocks.OAK_DOOR), CITRUS_SET_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_TRAPDOOR = go(() -> new TrapDoorBlock(blockProp(Blocks.OAK_TRAPDOOR), CITRUS_SET_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_PRESSURE_PLATE = go(() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProp(Blocks.OAK_DOOR), CITRUS_SET_TYPE));
	@Category(Categories.BUILDING_BLOCKS)
	public static final KiwiGO<Block> CITRUS_BUTTON = go(() -> Blocks.woodenButton(CITRUS_SET_TYPE));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> TANGERINE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.TANGERINE.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> LIME_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.LIME.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> CITRON_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.CITRON.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> POMELO_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.POMELO.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> ORANGE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.ORANGE.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> LEMON_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.LEMON.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@Category(Categories.NATURAL_BLOCKS)
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> GRAPEFRUIT_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(CoreFruitTypes.GRAPEFRUIT.getOrCreate()), blockProp(Blocks.OAK_SAPLING)));
	@Category(Categories.NATURAL_BLOCKS)
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
	@Category(Categories.INGREDIENTS)
	public static final KiwiGO<Item> SNOWFLAKE_BANNER_PATTERN = go(() -> new BannerPatternItem(SNOWFLAKE_TAG, itemProp().stacksTo(Items.MOJANG_BANNER_PATTERN.getMaxStackSize()).rarity(Rarity.UNCOMMON)));
	public static final KiwiGO<SoundEvent> OPEN_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FruitfulFun.ID, "block.wooden_door.open")));
	public static final KiwiGO<SoundEvent> CLOSE_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FruitfulFun.ID, "block.wooden_door.close")));
	/* off */
	public static final KiwiGO<EntityType<SlidingDoorEntity>> SLIDING_DOOR = go(() -> KiwiEntityTypeBuilder.<SlidingDoorEntity>create()
			.entityFactory(SlidingDoorEntity::new)
			.dimensions(EntityDimensions.scalable(1, 2))
			.fireImmune()
			.disableSummon()
			.build()
	);
	/* on */

	@Override
	protected void preInit() {
		createPoiTypes(this);
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
//			TREES_CF = FeatureUtils.register("fruitfulfun:base_trees", Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(list)));
		});
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

	//FIXME: flammability

//	public static final KiwiGO<Codec<MultiFilteredAddFeaturesBiomeModifier>> ADD_FEATURES = go(() -> RecordCodecBuilder.create(builder -> builder.group(
//	/* off */
//			Codec.list(Biome.LIST_CODEC).fieldOf("requires").forGetter(MultiFilteredAddFeaturesBiomeModifier::requires),
//			Codec.list(Biome.LIST_CODEC).fieldOf("excludes").forGetter(MultiFilteredAddFeaturesBiomeModifier::excludes),
//			PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(MultiFilteredAddFeaturesBiomeModifier::features),
//			GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(MultiFilteredAddFeaturesBiomeModifier::step)
//	).apply(builder, MultiFilteredAddFeaturesBiomeModifier::new)), () -> ForgeRegistries.BIOME_MODIFIER_SERIALIZERS.get());
//	/* on */
//	private Holder<ConfiguredFeature<SimpleRandomFeatureConfiguration, ?>> TREES_CF;
//
//	private static <T> DataProvider forDataPackRegistry(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper, RegistryOps<JsonElement> registryOps, ResourceKey<Registry<T>> registryKey, Map<ResourceLocation, T> idToObjectMap) {
//		return JsonCodecProvider.forDatapackRegistry(dataGenerator, existingFileHelper, FruitfulFun.ID, registryOps, registryKey, idToObjectMap);
//	}
//
//	private void makePlacedFeature(String id, int chunks, Holder<ConfiguredFeature<?, ?>> cf, Map<ResourceLocation, PlacedFeature> registry) {
//		SimpleWeightedRandomList<IntProvider> simpleweightedrandomlist = SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(0), chunks - 1).add(ConstantInt.of(1), 1).build();
//		CountPlacement placement = CountPlacement.of(new WeightedListInt(simpleweightedrandomlist));
//		registry.put(RL(id), new PlacedFeature(cf, VegetationPlacements.treePlacement(placement, CoreModule.LEMON_SAPLING.get())));
//	}
//
//	@Override
//	public void gatherData(GatherDataEvent event) {
//		DataGenerator generator = event.getGenerator();
//		boolean includeServer = event.includeServer();
//		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
//		generator.addProvider(includeServer, new KiwiLootTableProvider(uid, generator).add(CoreBlockLoot::new, LootContextParamSets.BLOCK));
//		CommonBlockTagsProvider blockTagsProvider = new CommonBlockTagsProvider(generator, existingFileHelper);
//		generator.addProvider(includeServer, blockTagsProvider);
//		generator.addProvider(includeServer, new CommonItemTagsProvider(generator, blockTagsProvider, existingFileHelper));
//		generator.addProvider(includeServer, new KiwiAdvancementProvider(uid, generator, existingFileHelper).add(new FFAdvancements(existingFileHelper)));
//
//		registerConfiguredFeatures();
//		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
//		var citrusCF = ops.registry(Registry.CONFIGURED_FEATURE_REGISTRY).get().getOrCreateHolderOrThrow(TREES_CF.unwrapKey().get().cast(Registry.CONFIGURED_FEATURE_REGISTRY).get());
//		var cherryCF = ops.registry(Registry.CONFIGURED_FEATURE_REGISTRY).get().getOrCreateHolderOrThrow(CherryFruitTypes.CHERRY.get().featureWG.unwrapKey().get().cast(Registry.CONFIGURED_FEATURE_REGISTRY).get());
//		Map<ResourceLocation, PlacedFeature> allPlaced = Maps.newHashMap();
//		makePlacedFeature("citrus_002", 500, citrusCF, allPlaced);
//		makePlacedFeature("citrus_005", 200, citrusCF, allPlaced);
//		makePlacedFeature("citrus_1", 10, citrusCF, allPlaced);
//		makePlacedFeature("cherry_002", 500, cherryCF, allPlaced);
//		makePlacedFeature("cherry_005", 200, cherryCF, allPlaced);
//		generator.addProvider(includeServer, forDataPackRegistry(generator, existingFileHelper, ops, Registry.PLACED_FEATURE_REGISTRY, allPlaced));
//
//		var biomes = ops.registry(Registry.BIOME_REGISTRY).get();
//		var plains = biomes.getOrCreateTag(Tags.Biomes.IS_PLAINS);
//		var forest = biomes.getOrCreateTag(BiomeTags.IS_FOREST);
//		var jungle = biomes.getOrCreateTag(BiomeTags.IS_OAK);
//		var cold = biomes.getOrCreateTag(Tags.Biomes.IS_COLD);
//		var magical = biomes.getOrCreateTag(Tags.Biomes.IS_MAGICAL);
//		var mushroom = biomes.getOrCreateTag(Tags.Biomes.IS_MUSHROOM);
//		var dead = biomes.getOrCreateTag(Tags.Biomes.IS_DEAD);
//		var dry = biomes.getOrCreateTag(Tags.Biomes.IS_DRY);
//		List<HolderSet<Biome>> excludes = List.of(cold, magical, mushroom, dead, dry);
//
//		/* off */
//		Map<ResourceLocation, BiomeModifier> modifiers = Map.of(
//						RL("citrus_plains"), new MultiFilteredAddFeaturesBiomeModifier(List.of(plains), excludes, HolderSet.direct(Holder::direct, allPlaced.get(RL("citrus_002"))), GenerationStep.Decoration.VEGETAL_DECORATION),
//						RL("citrus_forest"), new MultiFilteredAddFeaturesBiomeModifier(List.of(forest), excludes, HolderSet.direct(Holder::direct, allPlaced.get(RL("citrus_005"))), GenerationStep.Decoration.VEGETAL_DECORATION),
//						RL("citrus_jungle"), new MultiFilteredAddFeaturesBiomeModifier(List.of(jungle), excludes, HolderSet.direct(Holder::direct, allPlaced.get(RL("citrus_1"))), GenerationStep.Decoration.VEGETAL_DECORATION),
//						RL("cherry_plains"), new MultiFilteredAddFeaturesBiomeModifier(List.of(plains), excludes, HolderSet.direct(Holder::direct, allPlaced.get(RL("cherry_002"))), GenerationStep.Decoration.VEGETAL_DECORATION),
//						RL("cherry_forest"), new MultiFilteredAddFeaturesBiomeModifier(List.of(forest), excludes, HolderSet.direct(Holder::direct, allPlaced.get(RL("cherry_005"))), GenerationStep.Decoration.VEGETAL_DECORATION)
//				);
//		/* on */
//		generator.addProvider(includeServer, forDataPackRegistry(generator, existingFileHelper, ops, ForgeRegistries.Keys.BIOME_MODIFIERS, modifiers));
//	}
}
