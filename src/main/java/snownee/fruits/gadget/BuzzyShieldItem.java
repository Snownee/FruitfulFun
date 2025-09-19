package snownee.fruits.gadget;

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
}
