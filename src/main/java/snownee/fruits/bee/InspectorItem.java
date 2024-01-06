package snownee.fruits.bee;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import snownee.kiwi.item.ModItem;

public class InspectorItem extends ModItem {
	public InspectorItem(Properties builder) {
		super(builder.stacksTo(1));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.SPYGLASS;
	}

	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 1200;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (player.level().isClientSide) {
			ItemStack offhandItem = player.getOffhandItem();
			if (!InspectorClientHandler.startUsing()
					&& hand == InteractionHand.MAIN_HAND
					&& offhandItem.is(Items.WRITABLE_BOOK)
					&& offhandItem.getTag() != null
					&& offhandItem.getTag().contains("pages")) {
				Items.WRITABLE_BOOK.use(level, player, InteractionHand.OFF_HAND);
				return InteractionResultHolder.consume(player.getItemInHand(hand));
			}
		}
		return ItemUtils.startUsingInstantly(level, player, hand);
	}
}
