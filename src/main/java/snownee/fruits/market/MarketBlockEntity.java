package snownee.fruits.market;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import snownee.fruits.market.network.SMarketSyncPacket;

public class MarketBlockEntity extends BaseContainerBlockEntity {
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
		blockEntity.serverTick();
	}

	private void serverTick() {
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
			tickDay();
		}
	}

	private void tickDay() {
		long day = Math.floorDiv(level.getOverworldClockTime(), 24000L);
		if (lastRestockDay == Long.MIN_VALUE) {
			lastRestockDay = day;
			setChanged();
			return;
		}
		if (day > lastRestockDay) {
			lastRestockDay = day;
			setChanged();
			restock();
		}
	}

	public void restock() {
		Map<Item, Long> filled = new LinkedHashMap<>();
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
			long unit = MarketCurrency.unitPrice(order);
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
			filled.merge(order.getItem(), (long) fill, Long::sum);
		}
		if (filled.isEmpty()) {
			return;
		}
		setChanged();
		level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.3f, 1.4f);
		if (level instanceof ServerLevel serverLevel) {
			MarketSales.record(serverLevel, filled);
		}
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
		return SIZE;
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
