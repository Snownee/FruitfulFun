package snownee.fruits.food;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
		if (!ctx.getItemInHand().isEdible() || player == null || ctx.isSecondaryUseActive() || !player.canEat(ctx.getItemInHand().getFoodProperties(player).canAlwaysEat())) {
			return place(new BlockPlaceContext(ctx));
		} else {
			InteractionResult interactionresult1 = this.use(ctx.getLevel(), player, ctx.getHand()).getResult();
			return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
		}
	}

}
