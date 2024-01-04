package snownee.fruits.bee;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.MutagenItem;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.loader.event.InitEvent;

@KiwiModule("bee")
@KiwiModule.Optional
public class BeeModule extends AbstractModule {

	@Name("hybridizing")
	public static final RecipeType<HybridizingRecipe> RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "hybridizing";
		}
	};
	@Name("hybridizing")
	public static final RecipeSerializer<HybridizingRecipe> SERIALIZER = new HybridizingRecipe.Serializer();
	public static ResourceLocation BEE_ONE_CM = new ResourceLocation(FruitfulFun.ID, "bee_one_cm");
	public static ResourceLocation BEES_BRED = new ResourceLocation(FruitfulFun.ID, "bees_bred");
	public static final KiwiGO<SoundEvent> BEE_SHEAR = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FruitfulFun.ID, "entity.bee.shear")));
	@KiwiModule.Category(Categories.TOOLS_AND_UTILITIES)
	public static final KiwiGO<MutagenItem> MUTAGEN = go(MutagenItem::new);
	public static final KiwiGO<MobEffect> MUTAGEN_EFFECT = go(() -> new MobEffect(MobEffectCategory.NEUTRAL, 0xF3DCEB));

	public BeeModule() {
		Hooks.bee = true;
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			PotionBrewing.ALLOWED_CONTAINERS.add(Ingredient.of(BeeModule.MUTAGEN.get()));
		});
	}
}
