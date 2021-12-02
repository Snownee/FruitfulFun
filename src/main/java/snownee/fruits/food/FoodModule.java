package snownee.fruits.food;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Kiwi;
import snownee.kiwi.KiwiModule;

@KiwiModule("food")
@KiwiModule.Optional
@KiwiModule.Group("food")
public class FoodModule extends AbstractModule {

	public static final class Foods {
		public static final Food GRAPEFRUIT_PANNA_COTTA = new Food.Builder().hunger(1).saturation(1).build();
		public static final Food DONAUWELLE = new Food.Builder().hunger(1).saturation(1).build();
		public static final Food HONEY_POMELO_TEA = new Food.Builder().hunger(1).saturation(1).build();
		public static final Food RICE_WITH_FRUITS = new Food.Builder().hunger(1).saturation(1).build();
		public static final Food LEMON_ROAST_CHICKEN = new Food.Builder().hunger(1).saturation(1).build();
	}

	public static final Item GRAPEFRUIT_PANNA_COTTA = new Item(itemProp().food(Foods.GRAPEFRUIT_PANNA_COTTA));
	public static final Item DONAUWELLE = new Item(itemProp().food(Foods.DONAUWELLE));
	public static final Item HONEY_POMELO_TEA = new Item(itemProp().food(Foods.HONEY_POMELO_TEA).containerItem(Items.GLASS_BOTTLE));
	public static final Item RICE_WITH_FRUITS = new Item(itemProp().food(Foods.RICE_WITH_FRUITS));
	public static final Item LEMON_ROAST_CHICKEN = new Item(itemProp().food(Foods.LEMON_ROAST_CHICKEN).containerItem(Items.BOWL));

	@Override
	protected void init(FMLCommonSetupEvent event) {
		CraftingHelper.register(TryParseCondition.Serializer.INSTANCE);
		CraftingHelper.register(new ResourceLocation(Kiwi.MODID, "alternatives"), AlternativesIngredientSerializer.INSTANCE);
	}

}
