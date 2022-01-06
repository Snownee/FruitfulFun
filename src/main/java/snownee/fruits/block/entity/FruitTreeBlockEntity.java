package snownee.fruits.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.kiwi.block.entity.BaseBlockEntity;
import snownee.kiwi.util.NBTHelper;

public class FruitTreeBlockEntity extends BaseBlockEntity implements GameEventListener {

	public FruitType type = FruitType.CITRON;
	private int deathRate = 0;
	private ItemEntity onlyItem;
	private PositionSource source;

	public FruitTreeBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.FRUIT_TREE, pos, state);
		source = new BlockPositionSource(pos);
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
		String id = helper.getString("type");
		if (id != null) {
			type = FruitType.parse(id);
			if (type == null) {
				type = FruitType.CITRON;
			}
		} else {
			FruitType[] types = FruitType.values();
			type = types[Mth.clamp(helper.getInt("type"), 0, types.length)];
		}
		deathRate = helper.getInt("death");
		super.load(compound);
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		compound.putString("type", type.name());
		compound.putInt("death", deathRate);
		super.saveAdditional(compound);
	}

	public boolean canDrop() {
		return onlyItem == null || !onlyItem.isAlive();
	}

	public void setOnlyItem(ItemEntity itementity) {
		onlyItem = itementity;
	}

	@Override
	public PositionSource getListenerSource() {
		return source;
	}

	@Override
	public int getListenerRadius() {
		return 6;
	}

	public float getDeathRate() {
		if (deathRate <= 10) {
			return 0;
		}
		if (deathRate >= 50) {
			return 1;
		}
		return deathRate / 50F;
	}

	@Override
	public boolean handleGameEvent(Level level, GameEvent gameEvent, Entity entity, BlockPos pos) {
		if (CoreModule.FRUIT_DROP.matches(gameEvent)) {
			if (canDrop()) {
				BlockState state = level.getBlockState(pos);
				deathRate += 1;
				// Do not remove block entity inside the loop of game event
				CoreModule.FRUIT_DROP.runnable = FruitLeavesBlock.dropFruit(level, pos, state, getDeathRate());
			}
			CoreModule.FRUIT_DROP.swallow(this);
			return true;
		} else if (CoreModule.LEAVES_TRAMPLE.matches(gameEvent)) {
			deathRate += 3;
			CoreModule.LEAVES_TRAMPLE.swallow(this);
			return true;
		}
		return false;
	}

}
