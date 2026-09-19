package snownee.fruits.market;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MarketMenu extends AbstractContainerMenu {
	public static final int MARKET_SLOTS = MarketBlockEntity.SIZE;
	public static final int CURRENCY_SLOT = MARKET_SLOTS;
	public static final int MARKET_X = 8;
	public static final int MARKET_Y = 18;
	public static final int CURRENCY_X = 178;
	public static final int CURRENCY_Y = 18;
	public static final int IMAGE_WIDTH = 196;
	public static final int IMAGE_HEIGHT = 168;

	private final Container market;
	private final Container currency;
	private final @Nullable MarketBlockEntity blockEntity;

	public MarketMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(MARKET_SLOTS), new SimpleContainer(1), null);
	}

	public MarketMenu(int id, Inventory inventory, MarketBlockEntity blockEntity) {
		this(id, inventory, blockEntity, blockEntity.currency(), blockEntity);
	}

	private MarketMenu(
			int id,
			Inventory inventory,
			Container market,
			Container currency,
			@Nullable MarketBlockEntity blockEntity) {
		super(MarketModule.MARKET_MENU.get(), id);
		this.market = market;
		this.currency = currency;
		this.blockEntity = blockEntity;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlot(new Slot(market, col + row * 9, MARKET_X + col * 18, MARKET_Y + row * 18));
			}
		}
		addSlot(new CurrencySlot(currency, 0, CURRENCY_X, CURRENCY_Y));
		addStandardInventorySlots(inventory, 8, 84);
		if (blockEntity != null && inventory.player instanceof ServerPlayer serverPlayer) {
			blockEntity.addViewer(serverPlayer);
		}
	}

	public @Nullable MarketBlockEntity blockEntity() {
		return blockEntity;
	}

	public BlockPos pos() {
		return blockEntity == null ? BlockPos.ZERO : blockEntity.getBlockPos();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot.hasItem()) {
			ItemStack stack = slot.getItem();
			copy = stack.copy();
			if (index < MARKET_SLOTS) {
				if (!moveItemStackTo(stack, MARKET_SLOTS + 1, slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (index == CURRENCY_SLOT) {
				if (!moveItemStackTo(stack, MARKET_SLOTS + 1, slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (MarketCurrency.isCurrency(stack) && !slots.get(CURRENCY_SLOT).hasItem()) {
				if (!moveItemStackTo(stack, CURRENCY_SLOT, CURRENCY_SLOT + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(stack, 0, MARKET_SLOTS, false)) {
				return ItemStack.EMPTY;
			}
			if (stack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return copy;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (blockEntity != null && player instanceof ServerPlayer serverPlayer) {
			blockEntity.removeViewer(serverPlayer);
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return blockEntity == null || blockEntity.stillValid(player);
	}

	public static class CurrencySlot extends Slot {
		public CurrencySlot(Container container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return MarketCurrency.isCurrency(stack);
		}
	}
}
