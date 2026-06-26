package snownee.fruits.gadget;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.datagen.SetBuzzyPowerFunction;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.event.InitEvent;

@KiwiModule(value = "gadget", modId = FruitfulFun.ID, dependencies = "@core")
@KiwiModule.Optional
public class GadgetModule extends AbstractModule {
	@KiwiModule.Category(value = Categories.FUNCTIONAL_BLOCKS, after = "beehive")
	public static final BlockObject<Block> BUZZY_CRAFTER = block(BuzzyCrafterBlock::new, () -> Blocks.BEEHIVE);
	@KiwiModule.Name("buzzy_crafter")
	public static final KiwiGO<BlockEntityType<BuzzyCrafterBlockEntity>> BUZZY_CRAFTER_ENTITY = blockEntity(
			BuzzyCrafterBlockEntity::new,
			BuzzyCrafterBlock.class);
	@KiwiModule.Name("buzzy_crafter")
	public static final KiwiGO<PoiType> BUZZY_CRAFTER_POI = go(() -> new PoiType(
			Set.copyOf(BUZZY_CRAFTER.getOrCreate()
					.getStateDefinition()
					.getPossibleStates()), 0, 1));
	public static final TagKey<Block> SUSTAIN_CRAFTER_ITEM = blockTag("sustain_crafter_item");

	@KiwiModule.Category
	public static final ItemObject<Item> VAC_GUN_CASING = item($ -> new ModItem($.stacksTo(1).rarity(Rarity.RARE)));
	public static final ItemObject<VacGunItem> VAC_GUN = item(VacGunItem::new);
	public static final KiwiGO<SoundEvent> GUN_SHOOT_ITEM = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"item.gun.shoot_item")));
	public static final KiwiGO<SoundEvent> GUN_WORKING = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id("item.gun.working")));
	public static final KiwiGO<SoundEvent> GUN_STOP = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id("item.gun.stop")));
	public static final TagKey<Block> VCD_PERFORM_USING = blockTag("vcd_perform_using");
	public static final TagKey<Block> VCD_PERFORM_BREAKING = blockTag("vcd_perform_breaking");
	public static final TagKey<EntityType<?>> VCD_MOVABLE = entityTag("vcd_movable");
	public static final KiwiGO<EntityType<VacItemProjectile>> ITEM_PROJECTILE = entity($ -> EntityType.Builder.of(
			VacItemProjectile::new,
			MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build($));
	public static final KiwiGO<ParticleType<AirVortexParticleOption>> AIR_VORTEX = go(() -> new ParticleType<>(true) {
		@Override
		public MapCodec<AirVortexParticleOption> codec() {
			return AirVortexParticleOption.CODEC;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, AirVortexParticleOption> streamCodec() {
			return AirVortexParticleOption.STREAM_CODEC;
		}
	});

	@KiwiModule.Category(value = Categories.COMBAT, after = "shield")
	public static final ItemObject<BuzzyShieldItem> BUZZY_SHIELD = item($ -> new BuzzyShieldItem($.stacksTo(1)));
	public static final KiwiGO<EntityType<SummonedBee>> SUMMONED_BEE = entity($ -> EntityType.Builder.of(
			SummonedBee::new,
			MobCategory.CREATURE).sized(0.525f, 0.45f).clientTrackingRange(8).build($));

	public static final KiwiGO<MobEffect> PHANTOM_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xAAAAFF));
	public static final KiwiGO<MobEffect> WANDERING_TRADER_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xFFAA00));
	public static final KiwiGO<MobEffect> WEAK_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xAAAAAA));
	//	public static final KiwiGO<MobEffect> HEAVY_SCENT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0x555555));
	public static final KiwiGO<ScentType> PHANTOM = go(() -> new ScentType(List.of(new MobEffectInstance(
			PHANTOM_SCENT.holderOrThrow(),
			600,
			0,
			true,
			false,
			false))));
	public static final KiwiGO<ScentType> WANDERING_TRADER = go(() -> new ScentType(List.of(new MobEffectInstance(
			WANDERING_TRADER_SCENT.holderOrThrow(),
			600,
			0,
			true,
			false,
			false))));
	public static final KiwiGO<ScentType> ENDER = go(() -> new ScentType(List.of()));
	public static final KiwiGO<ScentType> WEAK = go(() -> new ScentType(List.of(
			new MobEffectInstance(WEAK_SCENT.holderOrThrow(), 600, 0, true, false, false),
			new MobEffectInstance(MobEffects.WEAKNESS, 600),
			new MobEffectInstance(CoreModule.FRAGILITY.holderOrThrow(), 600))));
	//	public static final KiwiGO<ScentType> HEAVY = go(() -> new ScentType(List.of(new MobEffectInstance(
//			HEAVY_SCENT.getOrCreate(),
//			600,
//			0,
//			true,
//			false,
//			false))));
	@KiwiModule.Category(value = Categories.FUNCTIONAL_BLOCKS)
	public static final BlockObject<ScentedCandleBlock> PHANTOM_CANDLE = block(
			$ -> new ScentedCandleBlock($, PHANTOM.getOrCreate()),
			() -> Blocks.CANDLE);
	public static final BlockObject<ScentedCandleBlock> WANDERING_TRADER_CANDLE = block(
			$ -> new ScentedCandleBlock(
					$,
					WANDERING_TRADER.getOrCreate()), () -> Blocks.CANDLE);
	public static final BlockObject<ScentedCandleBlock> ENDER_CANDLE = block(
			$ -> new ScentedCandleBlock($, ENDER.getOrCreate()),
			() -> Blocks.CANDLE);
	public static final BlockObject<ScentedCandleBlock> WEAK_CANDLE = block(
			$ -> new ScentedCandleBlock($, WEAK.getOrCreate()),
			() -> Blocks.CANDLE);
	//	public static final BlockObject<ScentedCandleBlock> HEAVY_CANDLE = block($ -> new ScentedCandleBlock(
	//			$,
	//			HEAVY.getOrCreate()), () -> Blocks.CANDLE);
	public static final KiwiGO<MapCodec<? extends SetBuzzyPowerFunction>> SET_BUZZY_POWER = go(
			() -> SetBuzzyPowerFunction.CODEC,
			Registries.LOOT_FUNCTION_TYPE);
	public static final KiwiGO<DataComponentType<Long>> LAST_PERFECT_BLOCK = go(
			() -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.LONG).build(),
			Registries.DATA_COMPONENT_TYPE);
	public static final KiwiGO<DataComponentType<BuzzyPowerStorage>> BUZZY_POWER_STORAGE = go(
			() -> DataComponentType.<BuzzyPowerStorage>builder()
					.persistent(BuzzyPowerStorage.CODEC)
					.networkSynchronized(BuzzyPowerStorage.STREAM_CODEC)
					.build(), Registries.DATA_COMPONENT_TYPE);

	@KiwiModule.Name("scented_candle")
	public static final KiwiGO<BlockEntityType<ScentedCandleBlockEntity>> SCENTED_CANDLE_ENTITY = blockEntity(
			ScentedCandleBlockEntity::new,
			ScentedCandleBlock.class);

	public GadgetModule() {
		Hooks.gadget = true;
	}

	@Override
	protected void addEntries() {
		CommonProxy.initGadgetModule();
		Hooks.scentEffects.addAll(KiwiModules.get(Objects.requireNonNull(uid)).getRegistries(Registries.MOB_EFFECT));
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			Holder<PoiType> holder = BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(BUZZY_CRAFTER_POI.get());
			PoiTypes.registerBlockStates(holder, holder.value().matchingStates());
		});
	}
}
