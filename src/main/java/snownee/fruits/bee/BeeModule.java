package snownee.fruits.bee;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
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
import snownee.lychee.RecipeTypes;
import snownee.lychee.core.contextual.ContextualConditionType;
import snownee.lychee.core.recipe.LycheeRecipe;
import snownee.lychee.mixin.LootContextParamSetsAccess;

@KiwiModule("bee")
@KiwiModule.Optional
public class BeeModule extends AbstractModule {

	@Name("hybridizing")
	public static final KiwiGO<HybridizingRecipeType> RECIPE_TYPE = go(() -> new HybridizingRecipeType("fruitfulfun:hybridizing", HybridizingRecipe.class, null));
	@Name("hybridizing")
	public static final KiwiGO<LycheeRecipe.Serializer<HybridizingRecipe>> SERIALIZER = go(HybridizingRecipe.Serializer::new);
	public static final KiwiGO<ContextualConditionType<BeeHasTrait>> BEE_HAS_TRAIT = go(BeeHasTrait.Type::new, () -> LycheeRegistries.CONTEXTUAL.registry());
	public static ResourceLocation BEE_ONE_CM = new ResourceLocation(FruitfulFun.ID, "bee_one_cm");
	public static ResourceLocation BEES_BRED = new ResourceLocation(FruitfulFun.ID, "bees_bred");
	public static final KiwiGO<SoundEvent> BEE_SHEAR = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FruitfulFun.ID, "entity.bee.shear")));
	@Category(value = Categories.TOOLS_AND_UTILITIES, after = "shears")
	public static final KiwiGO<Item> INSPECTOR = go(() -> new InspectorItem(itemProp()));
	public static final KiwiGO<MutagenItem> MUTAGEN = go(MutagenItem::new);
	public static final KiwiGO<MobEffect> MUTAGEN_EFFECT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xF3DCEB));

	public BeeModule() {
		Hooks.bee = true;
		LootContextParamSetsAccess.callRegister("fruitfulfun:hybridizing", $ -> {
			$.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.BLOCK_STATE).required(LycheeLootContextParams.BLOCK_POS).optional(LootContextParams.BLOCK_ENTITY);
		});
	}

	@Override
	protected void preInit() {
		CommonProxy.initBeeModule();
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			RecipeTypes.ALL.add(RECIPE_TYPE.get());
			PotionBrewing.ALLOWED_CONTAINERS.add(Ingredient.of(BeeModule.MUTAGEN.get()));
		});
	}
}
