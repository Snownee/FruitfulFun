package snownee.fruits.bee;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.duck.FFLivingEntity;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.loader.event.InitEvent;
import snownee.lychee.LycheeLootContextParams;
import snownee.lychee.LycheeRegistries;
import snownee.lychee.core.contextual.ContextualConditionType;
import snownee.lychee.core.recipe.LycheeRecipe;
import snownee.lychee.mixin.LootContextParamSetsAccess;

@KiwiModule("bee")
@KiwiModule.Optional
public class BeeModule extends AbstractModule {

	@Name("hybridizing")
	public static final KiwiGO<HybridizingRecipeType> RECIPE_TYPE = go(() -> new HybridizingRecipeType(
			"fruitfulfun:hybridizing",
			HybridizingRecipe.class,
			null));
	@Name("hybridizing")
	public static final KiwiGO<LycheeRecipe.Serializer<HybridizingRecipe>> SERIALIZER = go(HybridizingRecipe.Serializer::new);
	public static final KiwiGO<ContextualConditionType<BeeHasTrait>> BEE_HAS_TRAIT = go(
			BeeHasTrait.Type::new,
			() -> LycheeRegistries.CONTEXTUAL.registry());
	public static ResourceLocation BEE_ONE_CM = FruitfulFun.id("bee_one_cm");
	public static ResourceLocation BEES_BRED = FruitfulFun.id("bees_bred");
	public static final KiwiGO<SoundEvent> BEE_SHEAR = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id("entity.bee.shear")));
	public static final KiwiGO<SoundEvent> START_HAUNTING = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"entity.start_haunting")));
	public static final KiwiGO<SoundEvent> STOP_HAUNTING = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"entity.stop_haunting")));
	@Category(value = Categories.TOOLS_AND_UTILITIES, after = "shears")
	public static final KiwiGO<Item> INSPECTOR = go(() -> new InspectorItem(itemProp()));
	public static final KiwiGO<MutagenItem> MUTAGEN = go(MutagenItem::new);
	public static final KiwiGO<MobEffect> MUTAGEN_EFFECT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xF3DCEB));
	public static final KiwiGO<MobEffect> FRAGILITY = go(() -> new MobEffect(MobEffectCategory.HARMFUL, 0x875A49));
	public static final KiwiGO<SimpleParticleType> GHOST = go(() -> new SimpleParticleType(false));
	public static final String WAXED_MARKER_NAME = "@FruitfulFunWaxed";
	public static final int WAXED_TICKS = 1200;
	public static Set<VillagerProfession> BEEKEEPER_PROFESSIONS;
	public static final TagKey<EntityType<?>> CANNOT_HAUNT = entityTag(FruitfulFun.ID, "cannot_haunt");

	public BeeModule() {
		Hooks.bee = true;
		LootContextParamSetsAccess.callRegister("fruitfulfun:hybridizing", $ -> {
			$.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.BLOCK_STATE).required(
					LycheeLootContextParams.BLOCK_POS).optional(LootContextParams.BLOCK_ENTITY);
		});
	}

	public static boolean isWaxedMarker(Display display) {
		return display.getType() == EntityType.BLOCK_DISPLAY && display.getCustomName() != null &&
				display.getCustomName().getString().equals(WAXED_MARKER_NAME);
	}

	public static void tickWaxedMarker(Display display) {
		Level level = display.level();
		if (level.isClientSide) {
			if (display.random.nextInt(50) == 0) {
				ParticleUtils.spawnParticlesOnBlockFaces(
						level,
						display.blockPosition(),
						ParticleTypes.WAX_ON,
						UniformInt.of(2, 4));
			}
			return;
		}
		if (!Hooks.bee || display.tickCount > WAXED_TICKS) {
			display.discard();
		} else if (display.tickCount % 20 == 0 &&
				!(level.getBlockEntity(display.blockPosition()) instanceof BeehiveBlockEntity)) {
			display.discard();
		}
	}

	public static void addBeekeeperTrades(MerchantOffers merchantOffers, AbstractVillager villager) {
		if (!Hooks.bee || !FFCommonConfig.beehiveTrade) {
			return;
		}
		if (BEEKEEPER_PROFESSIONS == null) {
			ImmutableSet.Builder<VillagerProfession> builder = ImmutableSet.builder();
			for (VillagerProfession profession : BuiltInRegistries.VILLAGER_PROFESSION) {
				if (profession.name().endsWith("beekeeper")) {
					builder.add(profession);
				}
			}
			BEEKEEPER_PROFESSIONS = builder.build();
		}
		if (villager instanceof Villager v) {
			if (v.getVillagerData().getLevel() != 1) {
				return;
			}
			if (!BEEKEEPER_PROFESSIONS.contains(v.getVillagerData().getProfession())) {
				return;
			}
		} else if (villager.getType() == EntityType.WANDERING_TRADER) {
			if (!BEEKEEPER_PROFESSIONS.isEmpty()) {
				return;
			}
		} else {
			return;
		}
		ItemStack input = Items.BEEHIVE.getDefaultInstance();
		input.setHoverName(Component.translatable("tip.fruitfulfun.beehiveTradeInputName"));
		input.getOrCreateTag().putBoolean("FFTrade", true);
		ListTag lore = new ListTag();
		lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("tip.fruitfulfun.beehiveTradeInputHint"))));
		input.getOrCreateTag().getCompound(ItemStack.TAG_DISPLAY).put(ItemStack.TAG_LORE, lore);
		ItemStack output = Items.EMERALD.getDefaultInstance();
		output.getOrCreateTag().putBoolean("FFTrade", true);
		merchantOffers.add(new MerchantOffer(input, output, 1000, 2, 0));
	}

	public static boolean isBeehiveTrade(MerchantOffer merchantOffer) {
		ItemStack cost = merchantOffer.getBaseCostA();
		return cost.is(Items.BEEHIVE) && cost.getTag() != null && cost.getTag().getBoolean("FFTrade");
	}

	public static boolean isHauntingNormalEntity(@Nullable Player player, @Nullable Entity target) {
		if (!Hooks.bee || player == null) {
			return false;
		}
		HauntingManager manager = ((FFPlayer) player).fruits$hauntingManager();
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

	@Override
	protected void preInit() {
		CommonProxy.initBeeModule();
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			PotionBrewing.ALLOWED_CONTAINERS.add(Ingredient.of(BeeModule.MUTAGEN.get()));
		});
	}

	public static int getBeesValue(List<GeneData> dataList) {
		if (dataList.isEmpty()) {
			return 0;
		}
		int value = 0;
		for (GeneData geneData : dataList) {
			int singleValue = 0;
			for (Trait trait : geneData.getTraits()) {
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
			Set<Trait> first = dataList.get(0).getTraits();
			for (int i = 1; i < dataList.size(); i++) {
				if (!first.equals(dataList.get(i).getTraits())) {
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
		if (hauntedBy == null || !hauntedBy.canChangeDimensions()) {
			return;
		}
		if (hauntedBy.portalEntrancePos == null || destination.dimension() == Level.NETHER) {
			hauntedBy.portalEntrancePos = entity.portalEntrancePos;
		}
		Entity newSpectator = hauntedBy.changeDimension(destination);
		if (newSpectator instanceof FFPlayer) {
			((FFPlayer) newSpectator).fruits$setHauntingTarget(newEntity);
		}
	}
}
