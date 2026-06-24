package snownee.fruits.food;

import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import snownee.fruits.Hooks;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.Platform;

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
			if (!level.isClientSide() && Hooks.shouldClearHarmfulEffects(stack.getItem())) {
				ItemStack milk = Items.MILK_BUCKET.getDefaultInstance();
				entity.getActiveEffectsMap().values().stream()
						.filter($ -> !$.getEffect().isBeneficial() && Platform.isCurativeItem($, milk))
						.map(MobEffectInstance::getEffect)
						.forEach(entity::removeEffect);
			}
			entity.gameEvent(GameEvent.DRINK);
			if (player == null || !player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		} else {
			entity.eat(level, stack);
		}

		ItemStackTemplate remainder = Platform.getCraftingRemainingItem(stack);
		if (remainder != null && (player == null || !player.getAbilities().instabuild)) {
			if (stack.isEmpty()) {
				return remainder.create();
			} else if (player != null && !player.addItem(remainder.create())) {
				player.drop(remainder.create(), false);
			}
		}
		return stack;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		return finishUsing(stack, level, entity);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		Hooks.appendEffectTooltip(this, worldIn, tooltip, flagIn);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}
