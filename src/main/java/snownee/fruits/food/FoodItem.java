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
				entity.getActiveEffectsMap().values().stream()
						.filter($ -> !$.getEffect().isBeneficial() && CommonProxy.isCurativeItem($, milk))
						.map(MobEffectInstance::getEffect)
						.forEach(entity::removeEffect);
			}
			entity.gameEvent(GameEvent.DRINK);
			if (player == null || !player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		} else {
			entity.eat(level, stack);
//			if (FFCommonConfig.chorusFruitPieMaxTeleportDistance > 0 && !level.isClientSide && FoodModule.CHORUS_FRUIT_PIE.is(stack)) {
//				HitResult pick = entity.pick(FFCommonConfig.chorusFruitPieMaxTeleportDistance, 1.0F, false);
//				if (pick instanceof BlockHitResult hit) {
//					Direction direction = hit.getDirection();
//					Vec3 vec3 = Vec3.atBottomCenterOf(hit.getBlockPos().relative(direction));
//					AABB bb = entity.getBoundingBox();
//					double x = vec3.x() + direction.getStepX() * 1.01 * bb.getXsize() / 2;
//					double y = vec3.y();
//					double z = vec3.z() + direction.getStepZ() * 1.01 * bb.getZsize() / 2;
//					if (direction == Direction.DOWN) {
//						y -= bb.getYsize();
//					}
//					bb = bb.move(x - entity.getX(), y - entity.getY(), z - entity.getZ());
//					if (level.noCollision(entity, bb)) {
//						if (entity.isPassenger()) {
//							entity.dismountTo(x, y, z);
//						} else {
//							entity.teleportTo(x, y, z);
//						}
//						entity.resetFallDistance();
//						if (player != null) {
//							player.getCooldowns().addCooldown(stack.getItem(), 20);
//						}
//					}
//				}
//			}
		}

		ItemStack remainder = CommonProxy.getRecipeRemainder(stack);
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
		return FoodModule.HONEY_POMELO_TEA.is(this) ? SoundEvents.GENERIC_DRINK : SoundEvents.GENERIC_EAT;
	}
}
