package snownee.fruits.market;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class MarketCatalog {
	private static final List<StatType<Item>> STATS = List.of(
			Stats.ITEM_CRAFTED,
			Stats.ITEM_PICKED_UP,
			Stats.ITEM_USED,
			Stats.ITEM_DROPPED,
			Stats.ITEM_BROKEN);

	private MarketCatalog() {
	}

	public static List<ItemStack> compute(ServerPlayer player) {
		Set<Item> owned = ownedItems(player);
		List<ItemStack> result = new ArrayList<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (item == Items.AIR) {
				continue;
			}
			ItemStack stack = new ItemStack(item);
			if (MarketCurrency.unitPrice(stack, player.registryAccess()) <= 0 || !isUnlocked(player, item, owned)) {
				continue;
			}
			result.add(stack);
		}
		return result;
	}

	public static boolean isUnlocked(ServerPlayer player, ItemStack stack) {
		return !stack.isEmpty()
				&& MarketCurrency.unitPrice(stack, player.registryAccess()) > 0
				&& isUnlocked(player, stack.getItem(), ownedItems(player));
	}

	private static Set<Item> ownedItems(ServerPlayer player) {
		Inventory inventory = player.getInventory();
		Set<Item> items = new HashSet<>();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty()) {
				items.add(stack.getItem());
			}
		}
		return items;
	}

	private static boolean isUnlocked(ServerPlayer player, Item item, Set<Item> owned) {
		if (owned.contains(item)) {
			return true;
		}
		for (StatType<Item> stat : STATS) {
			if (player.getStats().getValue(stat, item) > 0) {
				return true;
			}
		}
		return false;
	}
}
