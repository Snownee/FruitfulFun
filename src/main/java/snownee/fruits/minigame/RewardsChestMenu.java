package snownee.fruits.minigame;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class RewardsChestMenu extends ChestMenu {
	public RewardsChestMenu(int id, Inventory inventory, Container container) {
		super(MenuType.GENERIC_9x6, id, inventory, container, 6);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		Container container = getContainer();
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if (!stack.isEmpty()) {
				container.setItem(i, ItemStack.EMPTY);
				if (!player.getInventory().add(stack)) {
					player.drop(stack, false);
				}
			}
		}
	}
}
