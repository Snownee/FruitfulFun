package snownee.fruits.bee;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.BeeHasTrait;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.Mutagen;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.bee.genetics.TransformBees;
import snownee.fruits.duck.FFLivingEntity;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.loader.event.InitEvent;
import snownee.lychee.LootContextKeys;
import snownee.lychee.LycheeRegistries;
import snownee.lychee.RecipeTypes;
import snownee.lychee.mixin.LootContextParamSetsAccess;
import snownee.lychee.util.action.PostActionType;
import snownee.lychee.util.contextual.ContextualConditionType;

@KiwiModule(value = "bee", modId = FruitfulFun.ID, dependencies = "@core")
@KiwiModule.Optional
public class BeeModule extends AbstractModule {

	@Name("hybridizing")
	public static final KiwiGO<HybridizingRecipeType> RECIPE_TYPE = go(() -> new HybridizingRecipeType(
			"fruitfulfun:hybridizing",
			HybridizingRecipe.class,
			null));
	@Name("hybridizing")
	public static final KiwiGO<RecipeSerializer<HybridizingRecipe>> RECIPE_SERIALIZER = go(() -> new RecipeSerializer<>(
			HybridizingRecipe.CODEC,
			HybridizingRecipe.STREAM_CODEC));
	public static final KiwiGO<ContextualConditionType<BeeHasTrait>> BEE_HAS_TRAIT = go(
			BeeHasTrait.Type::new,
			LycheeRegistries.CONTEXTUAL.key());
	public static final KiwiGO<PostActionType<TransformBees>> TRANSFORM_BEES = go(
			TransformBees.Type::new,
			LycheeRegistries.POST_ACTION.key());
	public static Identifier BEE_ONE_CM = FruitfulFun.id("bee_one_cm");
	public static Identifier BEES_BRED = FruitfulFun.id("bees_bred");
	public static final KiwiGO<SoundEvent> BEE_SHEAR = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id("entity.bee.shear")));
	public static final KiwiGO<SoundEvent> START_HAUNTING = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"entity.start_haunting")));
	public static final KiwiGO<SoundEvent> STOP_HAUNTING = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"entity.stop_haunting")));
	@Category(value = Categories.TOOLS_AND_UTILITIES, after = "shears")
	public static final ItemObject<Item> INSPECTOR = item(InspectorItem::new);
	public static final ItemObject<MutagenItem> MUTAGEN = item(MutagenItem::new);
	public static final KiwiGO<MobEffect> MUTAGEN_EFFECT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xF3DCEB));
	@Name("mutagen")
	public static final KiwiGO<DataComponentType<Mutagen>> MUTAGEN_CONTENT = go(
			() -> DataComponentType.<Mutagen>builder().persistent(
					Mutagen.CODEC).networkSynchronized(Mutagen.STREAM_CODEC).build(),
			Registries.DATA_COMPONENT_TYPE);
	public static final KiwiGO<DataComponentType<Unit>> MERCHANT_OFFER = go(
			() -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(Unit.STREAM_CODEC).build(),
			Registries.DATA_COMPONENT_TYPE);
	public static final KiwiGO<DataComponentType<String>> MERCHANT_OFFER_ADVANCEMENT = go(
			() -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build(),
			Registries.DATA_COMPONENT_TYPE);
	public static final KiwiGO<DataComponentType<BoundEntity>> BOUND_ENTITY = go(
			() -> DataComponentType.<BoundEntity>builder()
					.persistent(BoundEntity.CODEC)
					.networkSynchronized(BoundEntity.STREAM_CODEC)
					.build(),
			Registries.DATA_COMPONENT_TYPE);
	public static final KiwiGO<SimpleParticleType> GHOST = go(() -> new SimpleParticleType(false));
	public static final String WAXED_MARKER_NAME = "@FruitfulFunWaxed";
	public static final int WAXED_TICKS = 1200;
	public static @Nullable Set<VillagerProfession> BEEKEEPER_PROFESSIONS;
	public static final TagKey<EntityType<?>> CANNOT_HAUNT = entityTag("cannot_haunt");
	public static final TagKey<Biome> UNLIMITED_BEE_RIDING = tag(Registries.BIOME, "unlimited_bee_riding");
	private static Set<Item> ALLOGAMOUS_ITEMS = Set.of();
	@Name("gameplay/bee_rideable")
	public static final KiwiGO<EnvironmentAttribute<Boolean>> BEE_RIDEABLE = go(() -> EnvironmentAttribute.builder(AttributeTypes.BOOLEAN)
			.defaultValue(true)
			.build());

	public BeeModule() {
		Hooks.bee = true;
		LootContextParamSetsAccess.callRegister(
				"fruitfulfun:hybridizing", $ -> {
					$.required(LootContextParams.ORIGIN)
							.required(LootContextParams.THIS_ENTITY)
							.required(LootContextParams.BLOCK_STATE)
							.required(LootContextKeys.BLOCK_POS)
							.optional(LootContextParams.BLOCK_ENTITY);
				});
	}

	public static boolean isWaxedMarker(Display display) {
		return display.getType() == EntityType.BLOCK_DISPLAY && display.getCustomName() != null &&
				display.getCustomName().getString().equals(WAXED_MARKER_NAME);
	}

	public static void tickWaxedMarker(Display display) {
		Level level = display.level();
		if (level.isClientSide()) {
			if (display.random.nextInt(50) == 0) {
				ParticleUtils.spawnParticlesOnBlockFaces(level, display.blockPosition(), ParticleTypes.WAX_ON, UniformInt.of(2, 4));
			}
			return;
		}
		if (!Hooks.bee || display.tickCount > WAXED_TICKS) {
			display.discard();
		} else if (display.tickCount % 20 == 0 && !(level.getBlockEntity(display.blockPosition()) instanceof BeehiveBlockEntity)) {
			display.discard();
		}
	}

	public static void addBeekeeperTrades(MerchantOffers offers, AbstractVillager villager) {
		if (BEEKEEPER_PROFESSIONS == null) {
			ImmutableSet.Builder<VillagerProfession> builder = ImmutableSet.builder();
			for (Holder<VillagerProfession> profession : BuiltInRegistries.VILLAGER_PROFESSION.asHolderIdMap()) {
				if (profession.getRegisteredName().endsWith("beekeeper")) {
					builder.add(profession.value());
				}
			}
			BEEKEEPER_PROFESSIONS = builder.build();
		}
		if (villager instanceof Villager v) {
			if (!BEEKEEPER_PROFESSIONS.contains(v.getVillagerData().profession().value())) {
				return;
			}
		} else if (villager.getType() == EntityType.WANDERING_TRADER) {
			if (!BEEKEEPER_PROFESSIONS.isEmpty()) {
				return;
			}
		} else {
			return;
		}
		if (offers.stream().anyMatch(BeeModule::isBeehiveTrade)) {
			return;
		}
		ItemStack output = Items.EMERALD.getDefaultInstance();
		output.set(MERCHANT_OFFER.get(), Unit.INSTANCE);
		offers.add(new MerchantOffer(new ItemCost(Items.BEEHIVE), output, 1000, 2, 0));
	}

	public static boolean isBeehiveTrade(MerchantOffer offer) {
		return Hooks.bee && offer.getResult().has(MERCHANT_OFFER.get());
	}

	public static boolean isHauntingNormalEntity(@Nullable Player player, @Nullable Entity target) {
		if (!Hooks.bee || player == null) {
			return false;
		}
		HauntingManager manager = FFPlayer.of(player).fruits$hauntingManager();
		if (manager == null || manager.isGhostBee || manager.target == null) {
			return false;
		}
		return target == null || target == manager.target;
	}

	public static void spawnEntityParticles(Entity entity) {
		RandomSource random = entity.random;
		if (random.nextInt(10) != 0) {
			return;
		}
		AABB box = entity.getBoundingBox();
		double x = box.minX + (box.maxX - box.minX) * random.nextDouble();
		double y = box.minY + 0.1;
		double z = box.minZ + (box.maxZ - box.minZ) * random.nextDouble();
		entity.level().addParticle(GHOST.get(), x, y, z, 0, 0, 0);
	}

	public static boolean isAllogamous(ItemStack stack) {
		return ALLOGAMOUS_ITEMS.contains(stack.getItem());
	}

	public static boolean canBreed(Bee bee) {
		if (Hooks.bee && BeeAttributes.of(bee).hasTrait(Trait.GHOST)) {
			return false;
		}
		return !bee.isBaby();
	}

	public static Component randomName(UUID uuid) {
		RandomSource random = RandomSource.createThreadLocalInstance(uuid.hashCode() >> 2);
		return Component.translatable(
				"fruitfulfun.beeName",
				Component.translatable("fruitfulfun.beeName." + random.nextInt(150)),
				Component.translatable("fruitfulfun.beeName." + random.nextInt(150)));
	}

	@Override
	protected void addEntries() {
		CommonProxy.initBeeModule();
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			RecipeTypes.ALL.add(RECIPE_TYPE.get());

			ImmutableSet.Builder<Item> allogamousItems = ImmutableSet.builder();
			for (FruitType fruitType : FFRegistries.FRUIT_TYPE) {
				if (fruitType.allogamous) {
					allogamousItems.add(fruitType.leaves.get().asItem());
					allogamousItems.add(fruitType.sapling.get().asItem());
				}
			}
			ALLOGAMOUS_ITEMS = allogamousItems.build();
		});
	}

	public static int getBeesValue(List<GeneData> dataList) {
		if (dataList.isEmpty()) {
			return 0;
		}
		int value = 0;
		for (GeneData geneData : dataList) {
			int singleValue = 0;
			for (Trait trait : geneData.traits()) {
				singleValue += trait.value();
			}
			if (geneData.hasTrait(Trait.FASTER) && geneData.hasTrait(Trait.MOUNTABLE)) {
				singleValue += geneData.hasTrait(Trait.RAIN_CAPABLE) ? 4 : 2;
			} else if (geneData.hasTrait(Trait.ADVANCED_POLLINATION) && geneData.hasTrait(Trait.WITHER_TOLERANT)) {
				singleValue += 3;
			}
			value += Math.max(0, singleValue);
		}
		combo:
		if (value > 0 && dataList.size() >= 3) {
			Set<Trait> first = dataList.getFirst().traits();
			for (int i = 1; i < dataList.size(); i++) {
				if (!first.equals(dataList.get(i).traits())) {
					break combo;
				}
			}
			value += (int) (value * 0.5F);
		}
		value += dataList.size();
		return value;
	}

	public static void changeDimension(ServerLevel destination, Entity entity, @Nullable Entity newEntity) {
		if (!Hooks.bee || !FFCommonConfig.hauntingCrossDimensional || newEntity == null) {
			return;
		}
		if (!(entity instanceof FFLivingEntity)) {
			return;
		}
		Player hauntedBy = ((FFLivingEntity) entity).fruits$getHauntedBy();
//		if (hauntedBy == null || !hauntedBy.canChangeDimensions()) {
//			return;
//		}
//		if (hauntedBy.portalEntrancePos == null || destination.dimension() == Level.NETHER) {
//			hauntedBy.portalEntrancePos = entity.portalEntrancePos;
//		}
//		Entity newSpectator = hauntedBy.changeDimension(destination);
//		if (newSpectator instanceof FFPlayer ffPlayer) {
//			ffPlayer.fruits$setHauntingTarget(newEntity);
//		}
	}
}
