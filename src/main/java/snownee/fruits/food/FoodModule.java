package snownee.fruits.food;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.util.FoodBuilderExtension;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.RenderLayer;
import snownee.kiwi.KiwiModule.RenderLayer.Layer;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.VanillaActions;

@KiwiModule("food")
@KiwiModule.Optional
@KiwiModule.Category(value = Categories.FOOD_AND_DRINKS, after = "pumpkin_pie")
public class FoodModule extends AbstractModule {

	public static final TagKey<Item> PANDA_FOOD = itemTag(FruitfulFun.ID, "panda_food");
	public static Item.Properties GRAPEFRUIT_PANNA_COTTA_PROP = itemProp().food(basicFood(14, 1)
			.effect(Effects.SPEED, 1)
			.build());
	public static final KiwiGO<Block> GRAPEFRUIT_PANNA_COTTA = go(() -> new FoodBlock(Block.box(4.5, 0, 4.5, 11.5, 4, 11.5)));
	public static Item.Properties DONAUWELLE_PROP = itemProp().food(basicFood(14, 1)
			.effect(Effects.REGENERATION, 1)
			.build());
	public static final KiwiGO<Block> DONAUWELLE = go(() -> new FoodBlock(Block.box(5, 0, 5, 11, 4, 11)));
	public static Item.Properties HONEY_POMELO_TEA_PROP = itemProp().food(basicFood(1, Hooks.farmersdelight ? 0.3F : 4)
			.effect(Effects.COMFORT, 1)
			.builder()
			.alwaysEat()
			.build()).craftRemainder(Items.GLASS_BOTTLE);
	@RenderLayer(Layer.TRANSLUCENT)
	public static final KiwiGO<Block> HONEY_POMELO_TEA = go(() -> new FoodBlock(Block.box(5, 0, 5, 11, 7.75, 11)));
	public static Item.Properties RICE_WITH_FRUITS_PROP = itemProp().food(basicFood(9, 0.6F)
			.effect(Effects.COMFORT, 1)
			.build());
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<Block> RICE_WITH_FRUITS = go(() -> {
		FoodBlock block = new FoodBlock(Block.box(4, 0, 2, 12, 5, 14));
		block.lockShapeRotation = false;
		return block;
	});
	public static Item.Properties LEMON_ROAST_CHICKEN_PROP = itemProp().craftRemainder(Items.BOWL);
	public static final KiwiGO<Item> LEMON_ROAST_CHICKEN = go(() -> new FoodItem(itemProp().food(basicFood(16, 0.8F)
			.effect(Effects.NOURISHMENT, 1)
			.build()).craftRemainder(Items.BOWL)));
	public static final KiwiGO<FeastBlock> LEMON_ROAST_CHICKEN_BLOCK = go(() -> new FeastBlock(Block.box(4, 2, 4, 12, 9, 12),
			FeastBlock.LEFTOVER_SHAPE, LEMON_ROAST_CHICKEN));
	public static final KiwiGO<Item> CHORUS_FRUIT_PIE_SLICE = go(() -> new FoodItem(itemProp().food(new FoodProperties.Builder()
			.nutrition(2)
			.saturationMod(0.6F)
			.build())));
	public static Item.Properties CHORUS_FRUIT_PIE_PROP = itemProp().food(new FoodProperties.Builder()
			.nutrition(8)
			.saturationMod(0.6F)
			.build());
	@RenderLayer(Layer.CUTOUT)
	public static final KiwiGO<PieBlock> CHORUS_FRUIT_PIE = go(() -> new PieBlock(Block.box(2, 0, 2, 14, 4, 14),
			null, CHORUS_FRUIT_PIE_SLICE));
	public static final KiwiGO<SimpleParticleType> SMOKE = go(() -> new SimpleParticleType(true));

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
		});
	}

	private static FoodBuilderExtension basicFood(int nutrition, float saturation) {
		return FoodBuilderExtension.of(new FoodProperties.Builder()
				.nutrition(nutrition)
				.saturationMod(saturation));
	}

	public static final class Effects {
		private static final Supplier<MobEffectInstance> NOURISHMENT = make("farmersdelight:nourishment", 6000, 0);
		private static final Supplier<MobEffectInstance> COMFORT = make("farmersdelight:comfort", 3600, 0);
		private static final Supplier<MobEffectInstance> REGENERATION = make("regeneration", 120, 0);
		private static final Supplier<MobEffectInstance> SPEED = make("speed", 1200, 0);

		@Nullable
		private static Supplier<MobEffectInstance> make(String id, int duration, int amplifier) {
			ResourceLocation resourceLocation = new ResourceLocation(id);
			if (!Platform.isModLoaded(resourceLocation.getNamespace())) {
				return null;
			}
			return () -> {
				MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(resourceLocation);
				if (effect == null) {
					effect = MobEffects.DARKNESS; // something is wrong, hope someone will notice it
				}
				return new MobEffectInstance(effect, duration, amplifier);
			};
		}
	}

}
