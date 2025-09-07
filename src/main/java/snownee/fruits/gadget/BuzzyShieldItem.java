package snownee.fruits.gadget;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class BuzzyShieldItem extends ShieldItem {
	public BuzzyShieldItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return false;
	}

	public static ItemStack getItemInHand(LivingEntity entity) {
		if (entity.getMainHandItem().getItem() instanceof BuzzyShieldItem) {
			return entity.getMainHandItem();
		}
		if (entity.getOffhandItem().getItem() instanceof BuzzyShieldItem) {
			return entity.getOffhandItem();
		}
		return ItemStack.EMPTY;
	}

	public static BuzzyPowerStorage getPowerStorage(ItemStack itemStack) {
		return BuzzyPowerStorage.read(itemStack).orElseGet(() -> new BuzzyPowerStorage(120000f));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return hasNoPower(itemStack) ? UseAnim.NONE : super.getUseAnimation(itemStack);
	}

	public boolean hasNoPower(ItemStack itemStack) {
		return BuzzyPowerStorage.read(itemStack).map(BuzzyPowerStorage::isEmpty).orElse(true);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack itemStack = player.getItemInHand(usedHand);
		if (hasNoPower(itemStack)) {
			if (!level.isClientSide()) {
				player.displayClientMessage(Component.translatable("tip.fruitfulfun.notEnoughPower"), true);
			}
			return InteractionResultHolder.fail(itemStack);
		}
		return super.use(level, player, usedHand);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return BuzzyPowerStorage.isBarVisible(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BuzzyPowerStorage.getBarColor(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return BuzzyPowerStorage.getBarWidth(stack);
	}
}
