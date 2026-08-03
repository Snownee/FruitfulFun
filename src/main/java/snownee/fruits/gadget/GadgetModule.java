package snownee.fruits.gadget;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBrewingStand;
import snownee.fruits.gadget.brewer.RemoteBrewerContainerData;
import snownee.fruits.gadget.datagen.SetBuzzyPowerFunction;
import snownee.fruits.gadget.detector.RainDetectorBlock;
import snownee.fruits.gadget.detector.RainDetectorBlockEntity;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.KiwiEntityTypeBuilder;

@KiwiModule("gadget")
@KiwiModule.Optional
public class GadgetModule extends AbstractModule {
	@KiwiModule.Category(value = Categories.FUNCTIONAL_BLOCKS, after = "beehive")
	public static final KiwiGO<Block> BUZZY_CRAFTER = go(() -> new BuzzyCrafterBlock(blockProp(Blocks.BEEHIVE)));
	@KiwiModule.Name("buzzy_crafter")
	public static final KiwiGO<BlockEntityType<BuzzyCrafterBlockEntity>> BUZZY_CRAFTER_ENTITY = blockEntity(
			BuzzyCrafterBlockEntity::new,
			null,
			BuzzyCrafterBlock.class);
	@KiwiModule.Name("buzzy_crafter")
	public static final KiwiGO<PoiType> BUZZY_CRAFTER_POI = go(() -> new PoiType(
			Set.copyOf(BUZZY_CRAFTER.getOrCreate()
					.getStateDefinition()
					.getPossibleStates()), 0, 1));
	public static final TagKey<Block> SUSTAIN_CRAFTER_ITEM = blockTag(FruitfulFun.ID, "sustain_crafter_item");

	@KiwiModule.Category
	public static final KiwiGO<Item> VAC_GUN_CASING = go(() -> new ModItem(itemProp().stacksTo(1).rarity(Rarity.RARE)));
	public static final KiwiGO<VacGunItem> VAC_GUN = go(VacGunItem::new);
	public static final KiwiGO<SoundEvent> GUN_SHOOT_ITEM = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"item.gun.shoot_item")));
	public static final KiwiGO<SoundEvent> GUN_WORKING = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id("item.gun.working")));
	public static final KiwiGO<SoundEvent> GUN_STOP = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id("item.gun.stop")));
	public static final TagKey<Block> VCD_PERFORM_USING = blockTag(FruitfulFun.ID, "vcd_perform_using");
	public static final TagKey<Block> VCD_PERFORM_BREAKING = blockTag(FruitfulFun.ID, "vcd_perform_breaking");
	public static final TagKey<EntityType<?>> VCD_MOVABLE = entityTag(FruitfulFun.ID, "vcd_movable");
	public static final KiwiGO<EntityType<VacItemProjectile>> ITEM_PROJECTILE = go(() -> KiwiEntityTypeBuilder.<VacItemProjectile>create()
			.dimensions(EntityDimensions.scalable(0.25f, 0.25f))
			.trackRangeChunks(4)
			.trackedUpdateRate(10)
			.entityFactory(VacItemProjectile::new)
			.build());
	public static final KiwiGO<ParticleType<AirVortexParticleOption>> AIR_VORTEX = go(() -> new ParticleType<>(
			true,
			AirVortexParticleOption.DESERIALIZER) {
		@Override
		public Codec<AirVortexParticleOption> codec() {
			return AirVortexParticleOption.CODEC;
		}
	});

	@KiwiModule.Category(value = Categories.COMBAT, after = "shield")
	public static final KiwiGO<BuzzyShieldItem> BUZZY_SHIELD = go(() -> new BuzzyShieldItem(itemProp().stacksTo(1)));
	public static final KiwiGO<EntityType<SummonedBee>> SUMMONED_BEE = go(() -> KiwiEntityTypeBuilder.<SummonedBee>createMob()
			.dimensions(EntityDimensions.scalable(0.525f, 0.45f))
			.trackRangeChunks(8)
			.defaultAttributes(SummonedBee::createAttributes)
			.entityFactory(SummonedBee::new)
			.build());

	public static final KiwiGO<MobEffect> PHANTOM_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xAAAAFF));
	public static final KiwiGO<MobEffect> WANDERING_TRADER_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xFFAA00));
	public static final KiwiGO<MobEffect> WEAK_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xAAAAAA));
	//	public static final KiwiGO<MobEffect> HEAVY_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0x555555));
	public static final KiwiGO<ScentType> PHANTOM = go(() -> new ScentType(() -> List.of(new MobEffectInstance(
			PHANTOM_SCENT.getOrCreate(),
			600,
			0,
			true,
			false,
			false))));
	public static final KiwiGO<ScentType> WANDERING_TRADER = go(() -> new ScentType(() -> List.of(new MobEffectInstance(
			WANDERING_TRADER_SCENT.getOrCreate(),
			600,
			0,
			true,
			false,
			false))));
	public static final KiwiGO<ScentType> ENDER = go(() -> new ScentType(List::of, 0x6e6bca, 1));
	public static final KiwiGO<ScentType> WEAK = go(() -> new ScentType(() -> List.of(
			new MobEffectInstance(WEAK_SCENT.getOrCreate(), 600, 0, true, false, false),
			new MobEffectInstance(MobEffects.WEAKNESS, 600),
			new MobEffectInstance(CoreModule.FRAGILITY.getOrCreate(), 600))));
	public static final KiwiGO<ScentType> PEACE = go(() -> new ScentType(List::of, 0xee2c28, 2));
	//	public static final KiwiGO<ScentType> HEAVY = go(() -> new ScentType(List.of(new MobEffectInstance(
//			HEAVY_SCENT.getOrCreate(),
//			600,
//			0,
//			true,
//			false,
//			false))));
	@KiwiModule.Category(value = Categories.FUNCTIONAL_BLOCKS)
	public static final KiwiGO<ScentedCandleBlock> PHANTOM_CANDLE = go(() -> new ScentedCandleBlock(
			blockProp(Blocks.CANDLE),
			PHANTOM.getOrCreate()));
	public static final KiwiGO<ScentedCandleBlock> WANDERING_TRADER_CANDLE = go(() -> new ScentedCandleBlock(
			blockProp(Blocks.CANDLE),
			WANDERING_TRADER.getOrCreate()));
	public static final KiwiGO<ScentedCandleBlock> ENDER_CANDLE = go(() -> new ScentedCandleBlock(
			blockProp(Blocks.CANDLE),
			ENDER.getOrCreate()));
	public static final KiwiGO<ScentedCandleBlock> WEAK_CANDLE = go(() -> new ScentedCandleBlock(
			blockProp(Blocks.CANDLE),
			WEAK.getOrCreate()));
	public static final KiwiGO<ScentedCandleBlock> PEACE_CANDLE = go(() -> new ScentedCandleBlock(
			blockProp(Blocks.CANDLE),
			PEACE.getOrCreate()));
	//	public static final KiwiGO<ScentedCandleBlock> HEAVY_CANDLE = go(() -> new ScentedCandleBlock(
//			blockProp(Blocks.CANDLE),
//			HEAVY.getOrCreate()));
	public static final KiwiGO<LootItemFunctionType> SET_BUZZY_POWER = go(() -> new LootItemFunctionType(new SetBuzzyPowerFunction.Serializer()));

	@KiwiModule.Name("scented_candle")
	public static final KiwiGO<BlockEntityType<ScentedCandleBlockEntity>> SCENTED_CANDLE_ENTITY = blockEntity(
			ScentedCandleBlockEntity::new,
			null,
			ScentedCandleBlock.class);
	@KiwiModule.Category(value = Categories.REDSTONE_BLOCKS, after = "daylight_detector")
	public static final KiwiGO<Block> RAIN_DETECTOR = go(() -> new RainDetectorBlock(blockProp(Blocks.DAYLIGHT_DETECTOR)));
	@KiwiModule.Name("rain_detector")
	public static final KiwiGO<BlockEntityType<RainDetectorBlockEntity>> RAIN_DETECTOR_ENTITY = blockEntity(
			RainDetectorBlockEntity::new,
			null,
			RainDetectorBlock.class);
	@KiwiModule.Category(value = Categories.FUNCTIONAL_BLOCKS, after = "brewing_stand")
	@KiwiModule.RenderLayer(KiwiModule.RenderLayer.Layer.CUTOUT)
	public static final KiwiGO<Block> BREWER = go(() -> new BrewingStandBlock(blockProp(Blocks.BREWING_STAND)));
	@KiwiModule.Name("brewer")
	public static final KiwiGO<MenuType<BrewingStandMenu>> BREWER_MENU = go(() -> new MenuType<>(
			(containerId, inventory) -> new BrewingStandMenu(
					containerId,
					inventory,
					new SimpleContainer(5),
					new RemoteBrewerContainerData(3)),
			FeatureFlags.VANILLA_SET));

	public GadgetModule() {
		Hooks.gadget = true;
	}

	public static void doBrew(Level level, BlockPos pos, NonNullList<ItemStack> items, BrewingStandBlockEntity entity) {
		ItemStack ingredient = items.get(3);
		int ingredientHash = hash(ingredient) * 31;
		int[] recipeHash = new int[3];

		for (int dest = 0; dest < 3; dest++) {
			ItemStack source = items.get(dest);
			ItemStack mixed = PotionBrewing.mix(ingredient, source);
			items.set(dest, mixed);
			if (!ItemStack.isSameItemSameTags(source, mixed)) {
				recipeHash[dest] = hash(source) + ingredientHash;
			}
		}
		Hooks.popItemBelow(entity, false, 0, 1, 2);

		ingredient.shrink(1);
		Item remainder = ingredient.getItem().getCraftingRemainingItem();
		if (remainder != null) {
			items.set(3, new ItemStack(remainder));
			Hooks.popItemBelow(entity, true, 3);
		}

		items.set(3, ingredient);
		level.levelEvent(1035, pos, 0);
		((FFBrewingStand) entity).fruits$updateRecipeHash(recipeHash);
	}

	public static int hash(ItemStack stack) {
		return Objects.hash(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getTag());
	}

	@Override
	protected void preInit() {
		CommonProxy.initGadgetModule();
		Hooks.scentEffects.addAll(KiwiModules.get(uid).getRegistries(ForgeRegistries.MOB_EFFECTS));
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			Holder<PoiType> holder = BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(BUZZY_CRAFTER_POI.get());
			PoiTypes.registerBlockStates(holder, holder.value().matchingStates());
		});
	}
}
