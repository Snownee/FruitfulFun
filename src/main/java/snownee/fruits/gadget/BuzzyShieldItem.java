package snownee.fruits.gadget;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

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
}
