package snownee.fruits.gadget;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.util.CommonProxy;

public class BuzzyCrafterBlockEntity extends BeehiveBlockEntity implements BuzzyCrafterContainer {
	public static final Map<Class<?>, Function<BuzzyCrafterBlockEntity, BuzzyPowerReceiver>> BLOCK_RECEIVER_FACTORIES = Map.of(
			ScentedCandleBlock.class, ScentedCandleBlock::getPowerReceiver
	);
	public static final Map<Class<?>, Function<ItemStack, BuzzyPowerStorage>> ITEM_STORAGE_FACTORIES = Map.of(
			BuzzyShieldItem.class, BuzzyShieldItem::getPowerStorage,
			ScentedCandleItem.class, ScentedCandleItem::getPowerStorage
	);
	private static final String ITEM_STACK_KEY = "item";
	protected ItemStack item = ItemStack.EMPTY;
	protected TriState blocked = TriState.DEFAULT;
	protected boolean hasBlockAbove;
	private boolean blockPowerReceiverUpdated;
	private boolean itemPowerReceiverUpdated;
	private @Nullable BuzzyPowerReceiver blockPowerReceiver;
	private @Nullable BuzzyPowerStorage itemPowerReceiver;

	public BuzzyCrafterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean isValidBlockState(BlockState blockState) {
		type = GadgetModule.BUZZY_CRAFTER_ENTITY.get();
		return super.isValidBlockState(blockState);
	}

	@Override
	public void addOccupant(Bee bee) {
		if (isFull()) {
			return;
		}
		List<String> pollens;
		if (Hooks.bee) {
			pollens = BeeAttributes.of(bee).pollens();
		} else {
			pollens = List.of("*");
		}
		if (!blockPowerReceiverUpdated) {
			updateBlockPowerReceiver();
		}
		List<BuzzyPowerReceiver> receivers = powerReceivers().toList();
		if (!pollens.isEmpty() && !receivers.isEmpty()) {
			for (Iterator<String> iterator = pollens.iterator(); iterator.hasNext(); ) {
				String pollen = iterator.next();
				float amount = 1f;
				boolean changed = false;
				do {
					float oneTime = amount / receivers.size();
					for (BuzzyPowerReceiver receiver : receivers) {
						float newOneTime = receiver.addPower(BuzzyPowerType.RED, oneTime);
						float used = oneTime - newOneTime;
						amount -= used;
						if (used > 0f) {
							changed = true;
						}
					}
				} while (amount > 0f && changed);
				if (amount < 1f) {
					itemPowerReceiverUpdated = true;
					Objects.requireNonNull(level).levelEvent(LevelEvent.PARTICLES_WAX_OFF, worldPosition.above(), 0);
					iterator.remove();
				}
			}
			setChanged();
		}
		super.addOccupant(bee);
	}

	public void debugAddPower(BuzzyPowerType type, float amount) {
		if (Objects.requireNonNull(level).isClientSide()) {
			return;
		}
		if (!blockPowerReceiverUpdated) {
			updateBlockPowerReceiver();
		}
		if (blockPowerReceiver != null) {
			blockPowerReceiver.addPower(type, amount);
		}
		if (itemPowerReceiver != null) {
			itemPowerReceiver.addPower(type, amount);
			itemPowerReceiverUpdated = true;
		}
		setChanged();
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		readData(input);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		writeData(output, false);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(problemPath(), FruitfulFun.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
			writeData(output, true);
			return output.buildResult();
		}
	}

	protected void readData(ValueInput input) {
		item = ItemStack.EMPTY;
		if (input.contains(ITEM_STACK_KEY)) {
			item = input.read(ITEM_STACK_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
			hasBlockAbove = input.getBooleanOr("hasBlockAbove", false);
			if (level != null && !level.isClientSide()) {
				updateItemPowerReceiver();
			}
		}
	}

	protected void writeData(ValueOutput output, boolean network) {
		if (network || !item.isEmpty()) {
			output.store(ITEM_STACK_KEY, ItemStack.OPTIONAL_CODEC, getTheItem());
			output.putBoolean("hasBlockAbove", hasBlockAbove);
		}
	}

	public void refresh() {
		if (this.hasLevel() && !Objects.requireNonNull(this.level).isClientSide()) {
			BlockState state = this.getBlockState();
			this.level.sendBlockUpdated(this.worldPosition, state, state, 11);
			this.setChanged();
		}
	}

	public void setBlocked(boolean b) {
		blocked = TriState.of(b);
		maybePopItem();
	}

	private void maybePopItem() {
		if (!isBlocked() || item.isEmpty()) {
			return;
		}
		BlockPos below = worldPosition.below();
		BlockState belowState = Objects.requireNonNull(level).getBlockState(below);
		if (!belowState.isAir()) {
			CommonProxy.insertItem(level, below, belowState, level.getBlockEntity(below), Direction.UP, item);
			if (item.isEmpty()) {
				clearContent();
				return;
			}
		}
		ItemEntity itemEntity = new ItemEntity(
				Objects.requireNonNull(level),
				worldPosition.getX() + 0.5,
				worldPosition.getY() - EntityType.ITEM.getHeight(),
				worldPosition.getZ() + 0.5,
				removeTheItem());
		itemEntity.setDefaultPickUpDelay();
		itemEntity.setDeltaMovement(0, -0.1, 0);
		level.addFreshEntity(itemEntity);
	}

	public boolean isBlocked() {
		if (blocked == TriState.DEFAULT) {
			Objects.requireNonNull(level);
			blocked = TriState.of(BuzzyCrafterBlock.blocksContainer(level.getBlockState(worldPosition.above())));
		}
		return blocked == TriState.TRUE;
	}

	public void setHasBlockAbove(boolean hasBlockAbove) {
		this.hasBlockAbove = hasBlockAbove;
	}

	public boolean hasBlockAbove() {
		return hasBlockAbove;
	}

	private void updateItemStats() {
		if (!itemPowerReceiverUpdated || item.isEmpty() || itemPowerReceiver == null) {
			return;
		}
		if (!itemPowerReceiver.isEmpty()) {
			item.set(GadgetModule.BUZZY_POWER_STORAGE.get(), itemPowerReceiver);
		}
		itemPowerReceiverUpdated = false;
	}

	@Override
	public ItemStack getTheItem() {
		updateItemStats();
		return item;
	}

	@Override
	public void setTheItem(ItemStack itemStack) {
		boolean empty = item.isEmpty();
		item = itemStack;
		maybePopItem();
		if (itemStack.getCount() > getMaxStackSize()) {
			itemStack.setCount(getMaxStackSize());
		}
		updateItemPowerReceiver();
		refresh();
		if (level != null && !level.isClientSide()) {
			if (empty && !item.isEmpty()) {
				playAddSound();
			} else if (!empty && item.isEmpty()) {
				playRemoveSound();
			}
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int pSlot) {
		ItemStack itemstack = item;
		item = ItemStack.EMPTY;
		return itemstack;
	}

	@Override
	public ItemStack splitTheItem(int count) {
		ItemStack itemStack = BuzzyCrafterContainer.super.splitTheItem(count);
		if (!itemStack.isEmpty()) {
			playRemoveSound();
			refresh();
		}
		return itemStack;
	}

	private void playAddSound() {
		if (level != null && !level.isClientSide()) {
			level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, 1);
		}
	}

	private void playRemoveSound() {
		if (level != null && !level.isClientSide()) {
			level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, 1);
		}
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return Container.stillValidBlockEntity(this, pPlayer);
	}

	public void updateBlockPowerReceiver() {
		if (Objects.requireNonNull(level).isClientSide()) {
			return;
		}
		Block block = Objects.requireNonNull(level).getBlockState(worldPosition.above()).getBlock();
		Function<BuzzyCrafterBlockEntity, BuzzyPowerReceiver> function = BLOCK_RECEIVER_FACTORIES.get(block.getClass());
		if (function != null) {
			blockPowerReceiver = function.apply(this);
		} else {
			blockPowerReceiver = null;
		}
		blockPowerReceiverUpdated = true;
	}

	public void updateItemPowerReceiver() {
		if (Objects.requireNonNull(level).isClientSide()) {
			return;
		}
		itemPowerReceiverUpdated = false;
		if (item.isEmpty()) {
			itemPowerReceiver = null;
			return;
		}
		itemPowerReceiver = getPowerStorage(item);
	}

	@Nullable
	public static BuzzyPowerStorage getPowerStorage(ItemStack itemStack) {
		Function<ItemStack, BuzzyPowerStorage> function = ITEM_STORAGE_FACTORIES.get(itemStack.getItem().getClass());
		return function == null ? null : function.apply(itemStack);
	}

	public Stream<BuzzyPowerReceiver> powerReceivers() {
		if (!blockPowerReceiverUpdated) {
			updateBlockPowerReceiver();
		}
		BuzzyPowerStorage itemReceiver = itemPowerReceiver;
		BuzzyPowerReceiver blockReceiver = blockPowerReceiver;
		if (itemReceiver == null && blockReceiver == null) {
			return Stream.empty();
		}
		if (itemReceiver == null) {
			return Stream.of(blockReceiver);
		}
		if (blockReceiver == null) {
			return Stream.of(itemReceiver);
		}
		return Stream.of(blockReceiver, itemReceiver);
	}

	public int getAnalogOutput() {
		List<BuzzyPowerReceiver> receivers = powerReceivers().toList();
		if (receivers.isEmpty()) {
			return 0;
		}
		float life = 0;
		float maxLife = 0;
		for (BuzzyPowerReceiver receiver : receivers) {
			BuzzyPowerStorage view = receiver.view();
			if (view == null) {
				continue;
			}
			life += view.life();
			maxLife += view.maxLife();
		}
		if (maxLife == 0) {
			return 0;
		}
		return 1 + Mth.clamp((int) Math.ceil(14 * life / maxLife), 0, 14);
	}
}
