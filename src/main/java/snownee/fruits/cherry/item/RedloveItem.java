package snownee.fruits.cherry.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FFCommonConfig;
import snownee.kiwi.item.ModItem;

public class RedloveItem extends ModItem {
	public RedloveItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (!FFCommonConfig.redloveFruitUse || entity.isBaby() || !(entity instanceof Animal animal)) {
			// age > 0 means it's on breeding cooldown
			return InteractionResult.PASS;
		}
		if (player.level().isClientSide) {
			// we can't check age on client side
			return InteractionResult.CONSUME;
		}
		if (animal.getAge() <= 0) {
			return InteractionResult.FAIL;
		}
		stack.shrink(1);
		int age = animal.getAge();
		int skip = Math.max(age / 3, 600);
		animal.setAge(Math.max(0, age - skip));
		animal.eat(animal.level(), stack);
		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResult.sidedSuccess(player.level().isClientSide);
	}
}
