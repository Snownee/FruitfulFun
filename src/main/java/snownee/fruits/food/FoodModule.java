package snownee.fruits.food;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.loader.event.InitEvent;

@KiwiModule("food")
@KiwiModule.Optional
@KiwiModule.Category(Categories.FOOD_AND_DRINKS)
public class FoodModule extends AbstractModule {

	public static final TagKey<Item> PANDA_FOOD = itemTag(FruitfulFun.ID, "panda_food");
	public static final KiwiGO<Block> GRAPEFRUIT_PANNA_COTTA = go(() -> new FoodBlock(Block.box(4.5, 0, 4.5, 11.5, 4, 11.5)));
	public static final KiwiGO<Block> DONAUWELLE = go(() -> new FoodBlock(Block.box(5, 0, 5, 11, 4, 11)));
	@RenderLayer(Layer.TRANSLUCENT)
	public static final KiwiGO<Block> HONEY_POMELO_TEA = go(() -> new FoodBlock(Block.box(5, 0, 5, 11, 7.75, 11)));
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> RICE_WITH_FRUITS = go(() -> {
		FoodBlock block = new FoodBlock(Block.box(4, 0, 2, 12, 5, 14));
		block.lockShapeRotation = false;
		return block;
	});
	public static final KiwiGO<Item> LEMON_ROAST_CHICKEN = go(() -> new FoodItem(itemProp().food(new FoodProperties.Builder()
			.nutrition(16)
			.saturationMod(0.8F)
			.effect(Foods.NOURISHED, 1)
			.build()
	).craftRemainder(Items.BOWL)));
	public static final KiwiGO<Block> LEMON_ROAST_CHICKEN_BLOCK = go(() -> new FeastBlock(Block.box(4, 2, 4, 12, 9, 12), LEMON_ROAST_CHICKEN));
	/* off */
	public static Item.Properties GRAPEFRUIT_PANNA_COTTA_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(14)
			.saturationMod(1)
			.effect(Foods.SPEED, 1)
			.build()
	);
	public static Item.Properties DONAUWELLE_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(14)
			.saturationMod(1)
			.effect(Foods.REGENERATION, 1)
			.build()
	);
	public static Item.Properties HONEY_POMELO_TEA_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(1)
			.saturationMod(Hooks.farmersdelight ? 0.3F : 4)
			.effect(Foods.COMFORT, 1)
			.alwaysEat()
			.build()
	).craftRemainder(Items.GLASS_BOTTLE);
	public static Item.Properties RICE_WITH_FRUITS_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(9)
			.saturationMod(0.6F)
			.effect(Foods.COMFORT, 1)
			.build()
	);
	public static Item.Properties LEMON_ROAST_CHICKEN_PROP = itemProp().craftRemainder(Items.BOWL);
	public FoodModule() {
		Hooks.food = true;
	}
	/* on */

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			Panda.PANDA_ITEMS = Panda.PANDA_ITEMS.or(itemEntity -> {
				ItemStack itemstack = itemEntity.getItem();
				return itemEntity.isAlive() && !itemEntity.hasPickUpDelay() && itemstack.is(FoodModule.PANDA_FOOD);
			});
		});
	}

//	@Override
//	protected void gatherData(GatherDataEvent event) {
//		DataGenerator generator = event.getGenerator();
//		generator.addProvider(event.includeServer(), new KiwiLootTableProvider(uid, generator).add(FoodBlockLoot::new, LootContextParamSets.BLOCK));
//	}

	public static final class Foods {
		private static final MobEffectInstance NOURISHED = make("farmersdelight:nourished", 6000, 0);
		private static final MobEffectInstance COMFORT = make("farmersdelight:comfort", 3600, 0);
		private static final MobEffectInstance REGENERATION = make("regeneration", 120, 0);
		private static final MobEffectInstance SPEED = make("speed", 1200, 0);

		private static MobEffectInstance make(String id, int duration, int amplifier) {
			MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(id));
			if (effect == null) {
				return null;
			}
			return new MobEffectInstance(effect, duration, amplifier);
		}
	}

}
