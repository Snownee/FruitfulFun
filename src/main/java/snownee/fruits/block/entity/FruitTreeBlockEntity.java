package snownee.fruits.block.entity;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import snownee.fruits.CoreModule;
import snownee.fruits.FFFruitTypes;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.block.entity.ModBlockEntity;
import snownee.kiwi.util.KUtil;

public class FruitTreeBlockEntity extends ModBlockEntity {

	public Holder<FruitType> type = FFFruitTypes.CITRON.holder().orElseThrow();
	private int lifespan = 20;
	private int maxLifespan = 30;
	private int fruitProduced = 0;
	private @Nullable ItemEntity onlyItem;
	private final LongLinkedOpenHashSet leaves = new LongLinkedOpenHashSet();
	private final List<BlockPos> leavesCache = Lists.newArrayList();
	private @Nullable BlockPos lastWorldPosition;

	public FruitTreeBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.FRUIT_TREE.get(), pos, state);
	}

	public FruitTreeBlockEntity(BlockPos pos, BlockState state, Holder<FruitType> type) {
		this(pos, state);
		this.type = type;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return null;
	}

	@Override
	protected void readPacketData(ValueInput valueInput) {
		// NO-OP
	}

	@Override
	protected void writePacketData(ValueOutput valueOutput) {
		// NO-OP
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		Identifier id = KUtil.RL(input.getStringOr("Type", ""), FruitfulFun.ID);
		if (id != null) {
			type = FFRegistries.FRUIT_TYPE.getOrThrow(ResourceKey.create(FFRegistries.FRUIT_TYPE.key(), id));
		}
		lifespan = input.getIntOr("Lifespan", 20);
		maxLifespan = input.getIntOr("MaxLifespan", 30);
		fruitProduced = input.getIntOr("FruitProduced", 0);
		leaves.clear();
		if (input.contains("Leaves")) {
			for (long pos : input.getOptionalLongArray("Leaves").orElseThrow()) {
				leaves.add(pos);
			}
		}
		super.loadAdditional(input);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		output.putString("Type", KUtil.trimRL(type.getRegisteredName(), FruitfulFun.ID));
		output.putInt("Lifespan", lifespan);
		output.putInt("MaxLifespan", maxLifespan);
		output.putInt("FruitProduced", fruitProduced);
		if (!leaves.isEmpty()) {
			output.putLongArray("Leaves", leaves.toLongArray());
		}
		super.saveAdditional(output);
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
