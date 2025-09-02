package snownee.fruits.gadget;

import java.util.List;
import java.util.Objects;

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
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.util.CommonProxy;

public class BuzzyCrafterBlockEntity extends BeehiveBlockEntity implements BuzzyCrafterContainer {
	private static final String ITEM_STACK_KEY = "item";
	protected ItemStack item = ItemStack.EMPTY;
	protected TriState blocked = TriState.DEFAULT;

	public BuzzyCrafterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
		type = GadgetModule.BUZZY_CRAFTER_ENTITY.get();
	}

	@Override
	public void addOccupantWithPresetTicks(Entity occupant, boolean hasNectar, int ticksInHive) {
		if (isFull() || !(occupant instanceof Bee)) {
			return;
		}
		BeeAttributes attributes = BeeAttributes.of(occupant);
		List<String> pollens = attributes.getPollens();
		pollens.clear();
		super.addOccupantWithPresetTicks(occupant, hasNectar, ticksInHive);
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
		}
	}

	protected CompoundTag writeData(CompoundTag pTag, boolean network) {
		if (network || !item.isEmpty()) {
			pTag.put(ITEM_STACK_KEY, item.save(new CompoundTag()));
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
		return item;
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
}
