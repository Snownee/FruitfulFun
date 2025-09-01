package snownee.fruits.block.entity;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.CoreModule;
import snownee.fruits.FFFruitTypes;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.block.entity.ModBlockEntity;
import snownee.kiwi.util.Util;

public class FruitTreeBlockEntity extends ModBlockEntity {

	public FruitType type = FFFruitTypes.CITRON.get();
	private int lifespan = 20;
	private int maxLifespan = 30;
	private int fruitProduced = 0;
	private ItemEntity onlyItem;
	private final LongLinkedOpenHashSet leaves = new LongLinkedOpenHashSet();
	private final List<BlockPos> leavesCache = Lists.newArrayList();
	private @Nullable BlockPos lastWorldPosition;

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
	public void load(CompoundTag data) {
		String id = data.getString("Type");
		if (!id.isEmpty()) {
			type = FFRegistries.FRUIT_TYPE.get(Util.RL(id, FruitfulFun.ID));
		}
		lifespan = data.getInt("Lifespan");
		maxLifespan = data.getInt("MaxLifespan");
		fruitProduced = data.getInt("FruitProduced");
		leaves.clear();
		if (data.contains("Leaves", Tag.TAG_LONG_ARRAY)) {
			for (long pos : data.getLongArray("Leaves")) {
				leaves.add(pos);
			}
		} else if (data.contains("ActiveLeaves", Tag.TAG_COMPOUND)) {
			for (Tag tag : data.getList("ActiveLeaves", Tag.TAG_COMPOUND)) {
				leaves.add(NbtUtils.readBlockPos((CompoundTag) tag).asLong());
			}
		}
		super.load(data);
	}

	@Override
	protected void saveAdditional(CompoundTag data) {
		data.putString("Type", Util.trimRL(FFRegistries.FRUIT_TYPE.getKey(type), FruitfulFun.ID));
		data.putInt("Lifespan", lifespan);
		data.putInt("MaxLifespan", maxLifespan);
		data.putInt("FruitProduced", fruitProduced);
		if (!leaves.isEmpty()) {
			data.putLongArray("Leaves", leaves.toLongArray());
		}
		super.saveAdditional(data);
	}

	public boolean canDrop() {
		return onlyItem == null || !onlyItem.isAlive();
	}

	public void setOnlyItem(ItemEntity itementity) {
		onlyItem = itementity;
	}

	public long toRelativePos(BlockPos pos) {
		return BlockPos.offset(pos.asLong(), -worldPosition.getX(), -worldPosition.getY(), -worldPosition.getZ());
	}

	public void addLeaves(Iterable<BlockPos> leaves) {
		for (BlockPos pos : leaves) {
			this.leaves.add(toRelativePos(pos));
		}
		lastWorldPosition = null;
	}

	public void removeLeaves(BlockPos pos) {
		leaves.remove(toRelativePos(pos));
		lastWorldPosition = null;
	}

	public List<BlockPos> getLeaves() {
		if (lastWorldPosition != null && lastWorldPosition.equals(worldPosition)) {
			return leavesCache;
		}
		lastWorldPosition = worldPosition;
		leavesCache.clear();
		leaves.longStream()
				.mapToObj(l -> BlockPos.of(BlockPos.offset(l, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())))
				.forEach(leavesCache::add);
		return leavesCache;
	}

	public void consumeLifespan(int i) {
		lifespan = Mth.clamp(lifespan - i, 0, maxLifespan);
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

	public void increaseFruitProduced() {
		fruitProduced++;
	}

	public int getFruitProduced() {
		return fruitProduced;
	}

	public void setMaxLifespan(int lifespan) {
		maxLifespan = lifespan;
	}

	public int getMaxLifespan() {
		return maxLifespan;
	}
}
