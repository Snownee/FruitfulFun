package snownee.fruits.bee;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.MutagenItem;
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
	public static ResourceLocation BEE_ONE_CM = new ResourceLocation(FruitfulFun.ID, "bee_one_cm");
	public static ResourceLocation BEES_BRED = new ResourceLocation(FruitfulFun.ID, "bees_bred");
	public static final KiwiGO<SoundEvent> BEE_SHEAR = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(
			FruitfulFun.ID,
			"entity.bee.shear")));
	@Category(value = Categories.TOOLS_AND_UTILITIES, after = "shears")
	public static final KiwiGO<Item> INSPECTOR = go(() -> new InspectorItem(itemProp()));
	public static final KiwiGO<MutagenItem> MUTAGEN = go(MutagenItem::new);
	public static final KiwiGO<MobEffect> MUTAGEN_EFFECT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xF3DCEB));
	public static final String WAXED_MARKER_NAME = "@FruitfulFunWaxed";
	public static final int WAXED_TICKS = 1200;

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
}
