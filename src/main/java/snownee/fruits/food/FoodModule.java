package snownee.fruits.food;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.CraftingHelper;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Kiwi;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.recipe.AlternativesIngredientSerializer;
import snownee.kiwi.recipe.TryParseCondition;

@KiwiModule("food")
@KiwiModule.Optional
@KiwiModule.Category("food")
public class FoodModule extends AbstractModule {

	public static final class Foods {
		public static final FoodProperties GRAPEFRUIT_PANNA_COTTA = new FoodProperties.Builder().nutrition(1).saturationMod(1).build();
		public static final FoodProperties DONAUWELLE = new FoodProperties.Builder().nutrition(1).saturationMod(1).build();
		public static final FoodProperties HONEY_POMELO_TEA = new FoodProperties.Builder().nutrition(1).saturationMod(1).build();
		public static final FoodProperties RICE_WITH_FRUITS = new FoodProperties.Builder().nutrition(1).saturationMod(1).build();
		public static final FoodProperties LEMON_ROAST_CHICKEN = new FoodProperties.Builder().nutrition(1).saturationMod(1).build();
	}

	public static final Item GRAPEFRUIT_PANNA_COTTA = new Item(itemProp().food(Foods.GRAPEFRUIT_PANNA_COTTA));
	public static final Item DONAUWELLE = new Item(itemProp().food(Foods.DONAUWELLE));
	public static final Item HONEY_POMELO_TEA = new Item(itemProp().food(Foods.HONEY_POMELO_TEA).craftRemainder(Items.GLASS_BOTTLE));
	public static final Item RICE_WITH_FRUITS = new Item(itemProp().food(Foods.RICE_WITH_FRUITS));
	public static final Item LEMON_ROAST_CHICKEN = new Item(itemProp().food(Foods.LEMON_ROAST_CHICKEN).craftRemainder(Items.BOWL));

	@Override
	protected void init(InitEvent event) {
		CraftingHelper.register(TryParseCondition.Serializer.INSTANCE);
		CraftingHelper.register(new ResourceLocation(Kiwi.MODID, "alternatives"), AlternativesIngredientSerializer.INSTANCE);
	}

}
