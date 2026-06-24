package snownee.fruits.cherry.item;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
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
		if (player.level().isClientSide()) {
			// we can't check age on client side
			return InteractionResult.CONSUME;
		}
		if (animal.getAge() <= 0) {
			return InteractionResult.FAIL;
		}
		Consumable consumable = stack.get(DataComponents.CONSUMABLE);
		if (consumable == null) {
			return InteractionResult.FAIL;
		}
		consumable.onConsume(animal.level(), animal, stack);
		animal.playEatingSound();
		return InteractionResult.SUCCESS_SERVER;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(
			ItemStack itemStack,
			TooltipContext context,
			TooltipDisplay display,
			Consumer<Component> builder,
			TooltipFlag tooltipFlag) {
		Hooks.appendEffectTooltip(itemStack, context, builder, tooltipFlag);
		if (FFCommonConfig.redloveFruitUse) {
			builder.accept(Component.empty());
			builder.accept(Component.translatable("tip.fruitfulfun.whenUseOnAnimal").withStyle(ChatFormatting.GRAY));
			builder.accept(Component.translatable("tip.fruitfulfun.redloveFruitUse").withStyle(ChatFormatting.BLUE));
		}
	}
}
