package snownee.fruits.food;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import snownee.fruits.Hooks;
import snownee.fruits.mixin.LivingEntityAccess;
import snownee.kiwi.item.ModItem;

public class FoodItem extends ModItem {

	public FoodItem(Properties builder) {
		super(builder);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return FoodModule.HONEY_POMELO_TEA.is(this) ? UseAnim.DRINK : UseAnim.EAT;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		Player player = entity instanceof Player ? (Player) entity : null;
		if (FoodModule.HONEY_POMELO_TEA.is(this)) {
			if (player instanceof ServerPlayer) {
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
			}
			if (player != null) {
				player.getFoodData().eat(stack.getItem(), stack, player);
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			}
			((LivingEntityAccess) entity).callAddEatEffect(stack, level, entity);
			if (!Hooks.farmersdelight && !level.isClientSide) {
				ItemStack milk = Items.MILK_BUCKET.getDefaultInstance();
				entity.getActiveEffectsMap().values().stream().filter($ -> !$.getEffect().isBeneficial() && $.isCurativeItem(milk)).map(MobEffectInstance::getEffect).toList().forEach(player::removeEffect);
			}
			entity.gameEvent(GameEvent.DRINK);
			if (player == null || !player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		} else {
			stack = super.finishUsingItem(stack, level, entity);
		}

		if (hasCraftingRemainingItem() && (player == null || !player.getAbilities().instabuild)) {
			if (stack.isEmpty()) {
				return getCraftingRemainingItem().getDefaultInstance();
			} else if (player != null) {
				player.addItem(getCraftingRemainingItem().getDefaultInstance());
			}
		}
		return stack;
	}

}
