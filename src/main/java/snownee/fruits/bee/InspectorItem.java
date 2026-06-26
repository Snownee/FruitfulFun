package snownee.fruits.bee;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.util.ClientProxy;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.item.ModItem;

public class InspectorItem extends ModItem {
	public InspectorItem(Item.Properties builder) {
		super(builder.stacksTo(1));
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
		return ItemUseAnimation.SPYGLASS;
	}

	@Override
	public int getUseDuration(ItemStack itemStack, LivingEntity user) {
		return 1200;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.level().isClientSide()) {
			ItemStack offhandItem = player.getOffhandItem();
			if (!InspectorClientHandler.canUse()
					&& hand == InteractionHand.MAIN_HAND
					&& offhandItem.is(Items.WRITABLE_BOOK)
					&& !offhandItem.getOrDefault(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY).pages().isEmpty()) {
				Items.WRITABLE_BOOK.use(level, player, InteractionHand.OFF_HAND);
				return InteractionResult.CONSUME;
			}
		}
		return ItemUtils.startUsingInstantly(level, player, hand);
	}

	@Override
	public InteractionResult useOn(UseOnContext useOnContext) {
		BlockState blockState = useOnContext.getLevel().getBlockState(useOnContext.getClickedPos());
		if (CommonProxy.isBookshelf(blockState)) {
			if (useOnContext.getLevel().isClientSide()) {
				Player player = useOnContext.getPlayer();
				if (player == null) {
					return InteractionResult.FAIL;
				}
				if (FFPlayer.of(player).fruits$getGeneNames().isEmpty()) {
					player.sendOverlayMessage(Component.translatable("tip.fruitfulfun.noGeneNames"));
					return InteractionResult.FAIL;
				}
				ClientProxy.openEditGeneNameScreen();
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(
			ItemStack itemStack,
			TooltipContext context,
			TooltipDisplay display,
			Consumer<Component> builder,
			TooltipFlag tooltipFlag) {
		builder.accept(Component.empty());
		builder.accept(Component.translatable("tip.fruitfulfun.whenUseOnBookshelf").withStyle(ChatFormatting.GRAY));
		builder.accept(Component.translatable("tip.fruitfulfun.renameGenes").withStyle(ChatFormatting.BLUE));
		if (Hooks.gadget) {
			builder.accept(Component.translatable("tip.fruitfulfun.whenUseOnBlock").withStyle(ChatFormatting.GRAY));
			builder.accept(Component.translatable("tip.fruitfulfun.viewScents").withStyle(ChatFormatting.BLUE));
		}
	}
}
