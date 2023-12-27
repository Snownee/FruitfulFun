package snownee.fruits.food;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import snownee.fruits.Hooks;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.item.ModItem;

public class FoodItem extends ModItem {

	public FoodItem(Item.Properties builder) {
		super(builder);
	}

	public static ItemStack finishUsing(ItemStack stack, Level level, LivingEntity entity) {
		if (!stack.isEdible()) {
			return stack;
		}
		Player player = entity instanceof Player ? (Player) entity : null;
		// HoneyBottleItem
		if (FoodModule.HONEY_POMELO_TEA.is(stack)) {
			if (player instanceof ServerPlayer) {
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			}
			if (player != null) {
				player.getFoodData().eat(stack.getItem(), stack);
			}
			entity.addEatEffect(stack, level, entity);
			if (!Hooks.farmersdelight && !level.isClientSide) {
				ItemStack milk = Items.MILK_BUCKET.getDefaultInstance();
				entity.getActiveEffectsMap().values().stream().filter($ -> !$.getEffect().isBeneficial() && CommonProxy.isCurativeItem($, milk)).map(MobEffectInstance::getEffect).toList().forEach(entity::removeEffect);
			}
			entity.gameEvent(GameEvent.DRINK);
			if (player == null || !player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		} else {
			entity.eat(level, stack);
		}

		ItemStack remainder = stack.getRecipeRemainder();
		if (!remainder.isEmpty() && (player == null || !player.getAbilities().instabuild)) {
			if (stack.isEmpty()) {
				return remainder;
			} else if (player != null && !player.addItem(remainder)) {
				player.drop(remainder, false);
			}
		}
		return stack;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		return finishUsing(stack, level, entity);
	}

	@Override
	public SoundEvent getEatingSound() {
		return SoundEvents.GENERIC_DRINK;
	}
}
