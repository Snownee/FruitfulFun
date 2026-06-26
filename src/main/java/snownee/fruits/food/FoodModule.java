package snownee.fruits.food;

import java.util.Objects;
import java.util.function.Predicate;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.loader.event.InitEvent;

@KiwiModule("food")
@KiwiModule.Optional
@KiwiModule.Category(value = Categories.FOOD_AND_DRINKS, after = "pumpkin_pie")
public class FoodModule extends AbstractModule {

	public static Item.Properties GRAPEFRUIT_PANNA_COTTA_PROP = food(14, 1, false, Effects.SPEED);
	public static final BlockObject<Block> GRAPEFRUIT_PANNA_COTTA = block(
			$ -> new FoodBlock(Block.box(4.5, 0, 4.5, 11.5, 4, 11.5), $),
			() -> Blocks.CAKE);
	public static Item.Properties DONAUWELLE_PROP = food(14, 1, false, Effects.REGENERATION);
	public static final BlockObject<Block> DONAUWELLE = block($ -> new FoodBlock(Block.box(5, 0, 5, 11, 4, 11), $), GRAPEFRUIT_PANNA_COTTA);
	public static Item.Properties HONEY_POMELO_TEA_PROP = food(
			1,
			Hooks.farmersdelight ? 0.3F : 4,
			true,
			Effects.HONEY_POMELO_TEA).craftRemainder(Items.GLASS_BOTTLE);
	public static final BlockObject<Block> HONEY_POMELO_TEA = block(
			$ -> new FoodBlock(Block.box(5, 0, 5, 11, 7.75, 11), $),
			GRAPEFRUIT_PANNA_COTTA);
	public static Item.Properties RICE_WITH_FRUITS_PROP = food(9, 0.6F, false, Effects.COMFORT);
	public static final BlockObject<Block> RICE_WITH_FRUITS = block(
			$ -> {
				FoodBlock block = new FoodBlock(Block.box(4, 0, 2, 12, 5, 14), $);
				block.lockShapeRotation = false;
				return block;
			}, GRAPEFRUIT_PANNA_COTTA);
	public static final ItemObject<Item> LEMON_ROAST_CHICKEN = item($ -> new ModItem($.food(
			new FoodProperties(16, 0.8F, false),
			Effects.NOURISHMENT).craftRemainder(Items.BOWL)));
	public static Item.Properties LEMON_ROAST_CHICKEN_PROP = itemProp().craftRemainder(Items.BOWL);
	public static final BlockObject<FeastBlock> LEMON_ROAST_CHICKEN_BLOCK = block(
			$ -> new FeastBlock(
					Block.box(4, 2, 4, 12, 9, 12),
					FeastBlock.LEFTOVER_SHAPE,
					LEMON_ROAST_CHICKEN,
					$), GRAPEFRUIT_PANNA_COTTA);
	public static final ItemObject<Item> CHORUS_FRUIT_PIE_SLICE = item($ -> new ModItem($.food(new FoodProperties.Builder().nutrition(2)
			.saturationModifier(0.6F)
			.build())));
	public static Item.Properties CHORUS_FRUIT_PIE_PROP = food(8, 0.6F, false, Consumables.DEFAULT_FOOD);
	public static final BlockObject<PieBlock> CHORUS_FRUIT_PIE = block(
			$ -> new PieBlock(
					Block.box(2, 0, 2, 14, 4, 14),
					null,
					CHORUS_FRUIT_PIE_SLICE,
					$), GRAPEFRUIT_PANNA_COTTA);
	public static final KiwiGO<SimpleParticleType> SMOKE = go(() -> new SimpleParticleType(true));
	public static final KiwiGO<ConsumeEffect.Type<ClearHarmfulEffectsConsumeEffect>> CLEAR_HARMFUL_EFFECTS = go(() -> new ConsumeEffect.Type<>(
			ClearHarmfulEffectsConsumeEffect.CODEC,
			ClearHarmfulEffectsConsumeEffect.STREAM_CODEC));

	public FoodModule() {
		Hooks.food = true;
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			Objects.requireNonNull(uid);
			KiwiModules.get(uid).getRegistries(Registries.BLOCK).stream().filter(FoodBlock.class::isInstance).map(Block::asItem).filter(
					Predicate.not(Items.AIR::equals)).forEach($ -> DispenserBlock.registerBehavior($, new FoodDispenseBehavior()));
			KiwiModules.get(uid)
					.getRegistries(Registries.ITEM)
//					.filter($ -> $.components().has(DataComponents.FOOD))
					.forEach($ -> Platform.registerCompostable(1, $));
		});
	}

	public static Item.Properties food(int nutrition, float saturationModifier, boolean canAlwaysEat, Consumable consumable) {
		return itemProp().food(new FoodProperties(nutrition, saturationModifier, canAlwaysEat), consumable);
	}

	public static final class Effects {
		private static final Consumable NOURISHMENT = make("farmersdelight:nourishment", 6000, 0);
		private static final Consumable COMFORT = make("farmersdelight:comfort", 3600, 0);
		private static final Consumable HONEY_POMELO_TEA = Util.make(
				makeBuilder("farmersdelight:comfort", 3600, 0).animation(ItemUseAnimation.DRINK),
				builder -> {
					if (!Hooks.farmersdelight || !Platform.isProduction()) {
						builder.onConsume(ClearHarmfulEffectsConsumeEffect.INSTANCE);
					}
				}).build();
		private static final Consumable REGENERATION = make("regeneration", 120, 0);
		private static final Consumable SPEED = make("speed", 1200, 0);

		private static Consumable make(String effectId, int duration, int amplifier) {
			return makeBuilder(effectId, duration, amplifier).build();
		}

		private static Consumable.Builder makeBuilder(String effectId, int duration, int amplifier) {
			Consumable.Builder builder = Consumable.builder();
			var effect = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(effectId));
			if (effect.isPresent()) {
				var applyEffects = new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(effect.orElseThrow(), duration, amplifier));
				builder.onConsume(applyEffects);
			}
			return builder;
		}
	}

}
