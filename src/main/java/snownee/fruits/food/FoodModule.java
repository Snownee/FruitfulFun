package snownee.fruits.food;

import java.util.function.Predicate;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.VanillaActions;

@KiwiModule("food")
@KiwiModule.Optional
@KiwiModule.Category(value = Categories.FOOD_AND_DRINKS, after = "pumpkin_pie")
public class FoodModule extends AbstractModule {

	public static final TagKey<Item> PANDA_FOOD = itemTag(FruitfulFun.ID, "panda_food");
	public static Item.Properties GRAPEFRUIT_PANNA_COTTA_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(14)
			.saturationMod(1)
			.effect(Foods.SPEED, 1)
			.build());
	public static final KiwiGO<Block> GRAPEFRUIT_PANNA_COTTA = go(() -> new FoodBlock(Block.box(4.5, 0, 4.5, 11.5, 4, 11.5)));
	public static Item.Properties DONAUWELLE_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(14)
			.saturationMod(1)
			.effect(Foods.REGENERATION, 1)
			.build());
	public static final KiwiGO<Block> DONAUWELLE = go(() -> new FoodBlock(Block.box(5, 0, 5, 11, 4, 11)));
	public static Item.Properties HONEY_POMELO_TEA_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(1)
			.saturationMod(Hooks.farmersdelight ? 0.3F : 4)
			.effect(Foods.COMFORT, 1)
			.alwaysEat()
			.build()).craftRemainder(Items.GLASS_BOTTLE);
	@RenderLayer(Layer.TRANSLUCENT)
	public static final KiwiGO<Block> HONEY_POMELO_TEA = go(() -> new FoodBlock(Block.box(5, 0, 5, 11, 7.75, 11)));
	public static Item.Properties RICE_WITH_FRUITS_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(9)
			.saturationMod(0.6F)
			.effect(Foods.COMFORT, 1)
			.build());
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> RICE_WITH_FRUITS = go(() -> {
		FoodBlock block = new FoodBlock(Block.box(4, 0, 2, 12, 5, 14));
		block.lockShapeRotation = false;
		return block;
	});
	public static Item.Properties LEMON_ROAST_CHICKEN_PROP = itemProp().craftRemainder(Items.BOWL);
	public static final KiwiGO<Item> LEMON_ROAST_CHICKEN = go(() -> new FoodItem(itemProp().food(new FoodProperties.Builder()
			.nutrition(16)
			.saturationMod(0.8F)
			.effect(Foods.NOURISHED, 1)
			.build()).craftRemainder(Items.BOWL)));
	public static final KiwiGO<Block> LEMON_ROAST_CHICKEN_BLOCK = go(() -> new FeastBlock(Block.box(4, 2, 4, 12, 9, 12), LEMON_ROAST_CHICKEN));
	public static Item.Properties CHORUS_FRUIT_PIE_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(8)
			.saturationMod(0.6F)
			.build());
	@KiwiModule.NoCategory
	public static final KiwiGO<Block> CHORUS_FRUIT_PIE = go(() -> new FoodBlock(Block.box(4, 0, 4, 12, 4, 12)));
	public static final KiwiGO<SimpleParticleType> SMOKE = go(() -> new SimpleParticleType(true));
	public static final TagKey<Block> RITUAL_CANDLES = blockTag(FruitfulFun.ID, "ritual_candles");

	public FoodModule() {
		Hooks.food = true;
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			Panda.PANDA_ITEMS = Panda.PANDA_ITEMS.or(itemEntity -> {
				ItemStack itemstack = itemEntity.getItem();
				return itemEntity.isAlive() && !itemEntity.hasPickUpDelay() && itemstack.is(FoodModule.PANDA_FOOD);
			});

			KiwiModules.get(uid).<Block>getRegistries(ForgeRegistries.BLOCKS).stream()
					.filter(FoodBlock.class::isInstance)
					.map(Block::asItem)
					.filter(Predicate.not(Items.AIR::equals))
					.forEach($ -> DispenserBlock.registerBehavior($, new FoodDispenseBehavior()));
			KiwiModules.get(uid).<Item>getRegistries(ForgeRegistries.ITEMS).stream()
					.filter(Item::isEdible)
					.forEach($ -> VanillaActions.registerCompostable(1, $));
			if (FFCommonConfig.dispenserCollectDragonBreath) {
				DispenseItemBehavior original = DispenserBlock.DISPENSER_REGISTRY.get(Items.GLASS_BOTTLE);
				if (original != null) {
					DispenserBlock.registerBehavior(Items.GLASS_BOTTLE, new CollectDragonBreathDispenseBehavior(original));
				}
			}
		});
	}

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
