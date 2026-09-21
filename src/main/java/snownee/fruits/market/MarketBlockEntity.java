package snownee.fruits.market;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import snownee.fruits.market.network.SMarketSyncPacket;

public class MarketBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
	public static final int SIZE = 27;
	private static final Codec<List<ItemStack>> ORDERS_CODEC = ItemStack.OPTIONAL_CODEC.listOf();

	private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
	private final NonNullList<ItemStack> orders = NonNullList.withSize(SIZE, ItemStack.EMPTY);
	private final SimpleContainer currency = new SimpleContainer(1);
	private final Set<ServerPlayer> viewers = new HashSet<>();
	private long money;
	private long lastRestockDay = Long.MIN_VALUE;

	public MarketBlockEntity(BlockPos pos, BlockState state) {
		super(MarketModule.MARKET_ENTITY.get(), pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MarketBlockEntity blockEntity) {
		blockEntity.serverTick(level);
	}

	private void serverTick(Level level) {
		ItemStack stack = currency.getItem(0);
		if (!stack.isEmpty()) {
			int value = MarketCurrency.valueOf(stack);
			if (value > 0) {
				currency.setItem(0, ItemStack.EMPTY);
				addMoney((long) value * stack.getCount());
				level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 1.4f);
			}
		}
		if (level.getGameTime() % 20L == 0L) {
			tickDay(level);
		}
	}

	private void tickDay(Level level) {
		long day = Math.floorDiv(level.getOverworldClockTime(), 24000L);
		if (lastRestockDay == Long.MIN_VALUE) {
			lastRestockDay = day;
			setChanged();
			return;
		}
		if (day == lastRestockDay) {
			return;
		}
		if (day < lastRestockDay) {
			lastRestockDay = day;
			setChanged();
			if (level instanceof ServerLevel serverLevel) {
				MarketPricing.reanchor(serverLevel, day);
			}
			return;
		}
		if (level.hasNeighborSignal(worldPosition)) {
			return;
		}
		lastRestockDay = day;
		setChanged();
		if (level instanceof ServerLevel serverLevel) {
			MarketPricing.advanceDay(serverLevel, day);
			restock(serverLevel);
		}
	}

	public void restock(ServerLevel level) {
		Map<Holder<Item>, Long> filled = new LinkedHashMap<>();
		for (int i = 0; i < SIZE; i++) {
			ItemStack order = orders.get(i);
			if (order.isEmpty()) {
				continue;
			}
			ItemStack current = items.get(i);
			if (!current.isEmpty() && !current.is(order.getItem())) {
				continue;
			}
			int target = order.getCount();
			int have = current.isEmpty() ? 0 : current.getCount();
			if (have >= target) {
				continue;
			}
			long unit = MarketPricing.unitPrice(order.getItem(), level);
			if (unit <= 0) {
				continue;
			}
			long affordable = money / unit;
			if (affordable <= 0) {
				break;
			}
			int fill = (int) Math.min(target - have, affordable);
			money -= fill * unit;
			items.set(i, current.isEmpty() ? order.copyWithCount(fill) : current.copyWithCount(have + fill));
			filled.merge(order.typeHolder(), (long) fill, Long::sum);
		}
		if (filled.isEmpty()) {
			return;
		}
		setChanged();
		level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.3f, 1.4f);
		MarketSales.record(level, filled);
		sync();
	}

	public Container currency() {
		return currency;
	}

	public long getMoney() {
		return money;
	}

	public void addMoney(long amount) {
		long total = money + amount;
		money = total < 0 ? Long.MAX_VALUE : total;
		changed();
	}

	public List<ItemStack> orders() {
		return List.copyOf(orders);
	}

	public ItemStack getOrder(int index) {
		return orders.get(index);
	}

	public void setOrder(int index, ItemStack stack) {
		orders.set(index, stack);
		changed();
	}

	public void addViewer(ServerPlayer player) {
		viewers.add(player);
	}

	public void removeViewer(ServerPlayer player) {
		viewers.remove(player);
	}

	public void changed() {
		setChanged();
		sync();
	}

	public void sync() {
		if (viewers.isEmpty() || !(level instanceof ServerLevel)) {
			return;
		}
		for (ServerPlayer viewer : List.copyOf(viewers)) {
			if (viewer.isRemoved()) {
				viewers.remove(viewer);
				continue;
			}
			SMarketSyncPacket.send(this, viewer);
		}
	}

	@Override
	public int getContainerSize() {
		return SIZE + 1;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot == SIZE ? currency.getItem(0) : super.getItem(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot == SIZE) {
			currency.setItem(0, stack);
			setChanged();
		} else {
			super.setItem(slot, stack);
		}
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		if (slot != SIZE) {
			return super.removeItem(slot, count);
		}
		ItemStack result = currency.removeItem(0, count);
		if (!result.isEmpty()) {
			setChanged();
		}
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return slot == SIZE ? currency.removeItemNoUpdate(0) : super.removeItemNoUpdate(slot);
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && currency.isEmpty();
	}

	@Override
	public void clearContent() {
		super.clearContent();
		currency.clearContent();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return (slot == SIZE) == MarketCurrency.isCurrency(stack);
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		int[] slots = new int[SIZE + 1];
		for (int i = 0; i <= SIZE; i++) {
			slots[i] = i;
		}
		return slots;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return slot != SIZE;
	}

	public int getStockSignal() {
		float totalPercent = 0.0F;
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				totalPercent += (float) stack.getCount() / getMaxStackSize(stack);
			}
		}
		return Mth.lerpDiscrete(totalPercent / SIZE, 0, 15);
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.fruitfulfun.market");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new MarketMenu(id, inventory, this);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, items);
		List<ItemStack> savedOrders = input.read("orders", ORDERS_CODEC).orElse(List.of());
		for (int i = 0; i < SIZE; i++) {
			orders.set(i, i < savedOrders.size() ? savedOrders.get(i) : ItemStack.EMPTY);
		}
		money = input.getLongOr("money", 0L);
		lastRestockDay = input.getLongOr("restockDay", Long.MIN_VALUE);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items);
		output.store("orders", ORDERS_CODEC, List.copyOf(orders));
		output.putLong("money", money);
		output.putLong("restockDay", lastRestockDay);
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		money = components.getOrDefault(MarketModule.MARKET_MONEY.get(), 0L);
		List<ItemStack> savedOrders = components.getOrDefault(MarketModule.MARKET_ORDERS.get(), List.of());
		for (int i = 0; i < SIZE; i++) {
			orders.set(i, i < savedOrders.size() ? savedOrders.get(i) : ItemStack.EMPTY);
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		components.set(MarketModule.MARKET_MONEY.get(), money);
		boolean hasOrder = false;
		for (ItemStack order : orders) {
			if (!order.isEmpty()) {
				hasOrder = true;
				break;
			}
		}
		if (hasOrder) {
			components.set(MarketModule.MARKET_ORDERS.get(), List.copyOf(orders));
		}
	}

	@Override
	public void removeComponentsFromTag(ValueOutput output) {
		super.removeComponentsFromTag(output);
		output.discard("orders");
		output.discard("money");
	}
}
