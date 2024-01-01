package snownee.fruits.block.entity;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.CoreFruitTypes;
import snownee.fruits.CoreModule;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.block.entity.ModBlockEntity;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

public class FruitTreeBlockEntity extends ModBlockEntity {

	public FruitType type = CoreFruitTypes.CITRON.get();
	private int lifespan = 20;
	private ItemEntity onlyItem;
	private final Set<BlockPos> activeLeaves = Sets.newLinkedHashSet();

	public FruitTreeBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.FRUIT_TREE.get(), pos, state);
	}

	public FruitTreeBlockEntity(BlockPos pos, BlockState state, FruitType type) {
		this(pos, state);
		this.type = type;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return null;
	}

	@Override
	protected void readPacketData(CompoundTag data) {
	}

	@Override
	protected CompoundTag writePacketData(CompoundTag data) {
		return data;
	}

	@Override
	public void load(CompoundTag compound) {
		NBTHelper helper = NBTHelper.of(compound);
		String id = helper.getString("Type");
		if (id != null) {
			type = FFRegistries.FRUIT_TYPE.get(Util.RL(id, FruitfulFun.ID));
		}
		lifespan = helper.getInt("Lifespan");
		ListTag list = helper.getTagList("ActiveLeaves", Tag.TAG_COMPOUND);
		for (Tag tag : list) {
			activeLeaves.add(NbtUtils.readBlockPos((CompoundTag) tag));
		}
		super.load(compound);
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		compound.putString("Type", Util.trimRL(FFRegistries.FRUIT_TYPE.getKey(type), FruitfulFun.ID));
		compound.putInt("Lifespan", lifespan);
		if (!activeLeaves.isEmpty()) {
			ListTag list = new ListTag();
			for (BlockPos pos : activeLeaves) {
				list.add(NbtUtils.writeBlockPos(pos));
			}
			compound.put("ActiveLeaves", list);
		}
		super.saveAdditional(compound);
	}

	public boolean canDrop() {
		return onlyItem == null || !onlyItem.isAlive();
	}

	public void setOnlyItem(ItemEntity itementity) {
		onlyItem = itementity;
	}

	public void addActiveLeaves(Collection<BlockPos> leaves) {
		for (BlockPos pos : leaves) {
			pos = pos.subtract(worldPosition);
			activeLeaves.add(pos);
		}
	}

	public void removeActiveLeaves(BlockPos pos) {
		pos = pos.subtract(worldPosition);
		activeLeaves.remove(pos);
	}

	public Set<BlockPos> getActiveLeaves() {
		return activeLeaves;
	}

	public void consumeLifespan(int i) {
		lifespan -= i;
	}

	public int getLifespan() {
		return lifespan;
	}

	public boolean isDead() {
		return lifespan <= 0;
	}

	public void setLifespan(int lifespan) {
		this.lifespan = lifespan;
	}
}
