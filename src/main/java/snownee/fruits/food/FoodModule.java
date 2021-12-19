package snownee.fruits.food;

import java.util.function.Supplier;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Kiwi;
import snownee.kiwi.KiwiModule;

@KiwiModule("food")
@KiwiModule.Optional
@KiwiModule.Group("food")
public class FoodModule extends AbstractModule {

	public static final class Foods {
		private static Supplier<EffectInstance> makeSupplier(String id, int duration, int amplifier) {
			return Lazy.of(() -> {
				Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(id));
				if (effect == null) {
					return null;
				}
				return new EffectInstance(effect, duration, amplifier);
			});
		}

		private static final Supplier<EffectInstance> NOURISHED_PROVIDER = makeSupplier("farmersdelight:nourished", 6000, 0);
		private static final Supplier<EffectInstance> COMFORT_PROVIDER = makeSupplier("farmersdelight:comfort", 3600, 0);
		private static final Supplier<EffectInstance> REGENERATION_PROVIDER = makeSupplier("regeneration", 120, 0);
		private static final Supplier<EffectInstance> SPEED_PROVIDER = makeSupplier("speed", 1200, 0);

		public static final Food GRAPEFRUIT_PANNA_COTTA = new Food.Builder().hunger(14).saturation(1).effect(SPEED_PROVIDER, 1).build();
		public static final Food DONAUWELLE = new Food.Builder().hunger(14).saturation(1).effect(REGENERATION_PROVIDER, 1).build();
		public static final Food HONEY_POMELO_TEA = new Food.Builder().hunger(1).saturation(0.3F).effect(COMFORT_PROVIDER, 1).build();
		public static final Food RICE_WITH_FRUITS = new Food.Builder().hunger(9).saturation(0.6F).effect(COMFORT_PROVIDER, 1).build();
		public static final Food LEMON_ROAST_CHICKEN = new Food.Builder().hunger(12).saturation(0.9F).effect(NOURISHED_PROVIDER, 1).build();
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
