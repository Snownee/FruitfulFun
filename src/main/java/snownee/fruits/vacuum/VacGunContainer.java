package snownee.fruits.vacuum;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class VacGunContainer extends SimpleContainer {
	private int itemCount;
	private final int maxItemCount;
	private boolean dirty;

	public VacGunContainer(int maxItemCount) {
		super(maxItemCount);
		this.maxItemCount = maxItemCount;
	}

	@Override
	public ItemStack addItem(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int i = maxItemCount - getItemCount();
		if (i <= 0) {
			return itemStack.copy();
		}
		int m = i = Math.min(itemStack.getCount(), i);
		for (int j = 0; j < getContainerSize(); j++) {
			ItemStack thisSlot = getItem(j);
			if (thisSlot.isEmpty()) {
				setItem(j, itemStack.copyWithCount(i));
				i = 0;
				break;
			}
			if (!ItemStack.isSameItemSameTags(thisSlot, itemStack)) {
				continue;
			}
			if (j < getContainerSize() - 1) {
				ItemStack nextSlot = getItem(j + 1);
				if (!nextSlot.isEmpty()) {
					continue;
				}
			}
			int k = Math.min(this.getMaxStackSize(), itemStack.getMaxStackSize());
			int l = Math.min(i, k - i);
			if (l <= 0) {
				continue;
			}
			thisSlot.grow(l);
			i -= l;
			setChanged();
			if (i == 0) {
				break;
			}
		}
		return itemStack.copyWithCount(itemStack.getCount() - m + i);
	}

	@Override
	public boolean canAddItem(ItemStack itemStack) {
		if (getItemCount() >= maxItemCount) {
			return false;
		}
		return super.canAddItem(itemStack);
	}

	public int getItemCount() {
		if (dirty) {
			itemCount = items.stream().mapToInt(ItemStack::getCount).sum();
			dirty = false;
		}
		return itemCount;
	}

	@Override
	public void fromTag(ListTag listTag) {
		super.fromTag(listTag);
		itemCount = items.stream().mapToInt(ItemStack::getCount).sum();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		dirty = true;
	}

	public ItemStack getLastItem() {
		for (int i = getContainerSize() - 1; i >= 0; i--) {
			ItemStack itemStack = getItem(i);
			if (!itemStack.isEmpty()) {
				return itemStack;
			}
		}
		return ItemStack.EMPTY;
	}
}
