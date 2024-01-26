package snownee.fruits.food;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import snownee.fruits.Hooks;
import snownee.kiwi.item.ModBlockItem;

public class FoodBlockItem extends ModBlockItem {

	public FoodBlockItem(Block block, Item.Properties builder) {
		super(block, builder);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return FoodModule.HONEY_POMELO_TEA.is(stack) ? UseAnim.DRINK : UseAnim.EAT;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		return FoodItem.finishUsing(stack, level, entity);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		FoodProperties foodProperties = ctx.getItemInHand().getItem().getFoodProperties();
		if (player == null || foodProperties == null || ctx.isSecondaryUseActive() || !player.canEat(foodProperties.canAlwaysEat())) {
			return place(new BlockPlaceContext(ctx));
		} else {
			InteractionResult result = this.use(ctx.getLevel(), player, ctx.getHand()).getResult();
			return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		Hooks.appendEffectTooltip(this, worldIn, tooltip, flagIn);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}
