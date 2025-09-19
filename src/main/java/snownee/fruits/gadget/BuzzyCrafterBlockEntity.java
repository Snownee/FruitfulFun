package snownee.fruits.gadget;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.util.CommonProxy;

public class BuzzyCrafterBlockEntity extends BeehiveBlockEntity implements BuzzyCrafterContainer {
	public static final Map<Class<?>, Function<BuzzyCrafterBlockEntity, BuzzyPowerReceiver>> BLOCK_RECEIVER_FACTORIES = Map.of(
			ScentedCandleBlock.class, ScentedCandleBlock::getPowerReceiver
	);
	public static final Map<Class<?>, Function<ItemStack, BuzzyPowerStorage>> ITEM_STORAGE_FACTORIES = Map.of(
			BuzzyShieldItem.class, BuzzyShieldItem::getPowerStorage
	);
	private static final String ITEM_STACK_KEY = "item";
	protected ItemStack item = ItemStack.EMPTY;
	protected TriState blocked = TriState.DEFAULT;
	private boolean blockPowerReceiverUpdated;
	private boolean itemPowerReceiverUpdated;
	private @Nullable BuzzyPowerReceiver blockPowerReceiver;
	private @Nullable BuzzyPowerStorage itemPowerReceiver;

	public BuzzyCrafterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
		type = GadgetModule.BUZZY_CRAFTER_ENTITY.get();
	}

	@Override
	public void addOccupantWithPresetTicks(Entity occupant, boolean hasNectar, int ticksInHive) {
		if (isFull() || !(occupant instanceof Bee)) {
			return;
		}
		List<String> pollens;
		if (Hooks.bee) {
			pollens = BeeAttributes.of(occupant).getPollens();
		} else {
			pollens = List.of("*");
		}
		if (!blockPowerReceiverUpdated) {
			updateBlockPowerReceiver();
		}
		List<BuzzyPowerReceiver> receivers = Stream.of(blockPowerReceiver, itemPowerReceiver).filter(Objects::nonNull).toList();
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
		}
		super.addOccupantWithPresetTicks(occupant, hasNectar, ticksInHive);
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
	}

	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		readData(pTag);
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		writeData(pTag, false);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return writeData(new CompoundTag(), true);
	}

	protected void readData(CompoundTag pTag) {
		item = ItemStack.EMPTY;
		if (pTag.contains(ITEM_STACK_KEY)) {
			item = ItemStack.of(pTag.getCompound(ITEM_STACK_KEY));
			if (level != null && !level.isClientSide()) {
				updateItemPowerReceiver();
			}
		}
	}

	protected CompoundTag writeData(CompoundTag pTag, boolean network) {
		if (network || !item.isEmpty()) {
			pTag.put(ITEM_STACK_KEY, getFirstItem().save(new CompoundTag()));
		}
		return pTag;
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
				item);
		clearContent();
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

	@Override
	public void setItem(int pSlot, ItemStack pStack) {
		boolean empty = item.isEmpty();
		item = pStack;
		maybePopItem();
		if (pStack.getCount() > getMaxStackSize()) {
			pStack.setCount(getMaxStackSize());
		}
		updateItemPowerReceiver();
		refresh();
		if (level != null && !level.isClientSide) {
			if (empty && !item.isEmpty()) {
				level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, 1);
			} else if (!empty && item.isEmpty()) {
				level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, 1);
			}
		}
	}

	@Override
	public ItemStack getItem(int slot) {
		updateItemStats();
		return item;
	}

	private void updateItemStats() {
		if (!itemPowerReceiverUpdated || item.isEmpty() || itemPowerReceiver == null) {
			return;
		}
		if (!itemPowerReceiver.isEmpty()) {
			BuzzyPowerStorage.write(item, itemPowerReceiver);
		}
		itemPowerReceiverUpdated = false;
	}

	@Override
	public ItemStack removeItemNoUpdate(int pSlot) {
		ItemStack itemstack = item;
		item = ItemStack.EMPTY;
		return itemstack;
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
		if (!item.isEmpty()) {
			Function<ItemStack, BuzzyPowerStorage> function = ITEM_STORAGE_FACTORIES.get(item.getItem().getClass());
			if (function != null) {
				itemPowerReceiver = function.apply(item);
				return;
			}
		}
		itemPowerReceiver = null;
	}
}
