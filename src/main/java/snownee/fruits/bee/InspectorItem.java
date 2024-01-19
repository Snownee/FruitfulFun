package snownee.fruits.bee;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.util.ClientProxy;
import snownee.fruits.util.CommonProxy;
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

	@Override
	public InteractionResult useOn(UseOnContext useOnContext) {
		BlockState blockState = useOnContext.getLevel().getBlockState(useOnContext.getClickedPos());
		if (CommonProxy.isBookshelf(blockState)) {
			if (useOnContext.getLevel().isClientSide) {
				Player player = useOnContext.getPlayer();
				if (player == null) {
					return InteractionResult.FAIL;
				}
				if (FFPlayer.of(player).fruits$getGeneNames().isEmpty()) {
					player.displayClientMessage(Component.translatable("tip.fruitfulfun.noGeneNames"), true);
					return InteractionResult.FAIL;
				}
				ClientProxy.openEditGeneNameScreen();
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("tip.fruitfulfun.whenUseOnBookshelf").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("tip.fruitfulfun.renameGenes").withStyle(ChatFormatting.BLUE));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}
