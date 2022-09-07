package snownee.fruits.block.entity;

import java.util.Locale;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent.Message;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import snownee.fruits.CoreFruitTypes;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.fruits.FruitsMod;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.kiwi.block.entity.BaseBlockEntity;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

public class FruitTreeBlockEntity extends BaseBlockEntity implements GameEventListener {

	public FruitType type = CoreFruitTypes.CITRON.get();
	private int deathRate = 0;
	private ItemEntity onlyItem;
	private PositionSource source;

	public FruitTreeBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.FRUIT_TREE.get(), pos, state);
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
			type = FruitType.REGISTRY.getValue(Util.RL(id.toLowerCase(Locale.ENGLISH), FruitsMod.ID));
			if (type == null) {
				type = CoreFruitTypes.CITRON.get();
			}
		}
		deathRate = helper.getInt("death");
		super.load(compound);
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		compound.putString("type", Util.trimRL(FruitType.REGISTRY.getKey(type), FruitsMod.ID));
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
	public boolean handleGameEvent(ServerLevel level, Message message) {
		if (CoreModule.FRUIT_DROP.get().matches(message.gameEvent())) {
			if (canDrop()) {
				BlockState state = message.context().affectedState();
				deathRate += 1;
				BlockPos pos = new BlockPos(message.source());
				// Do not remove block entity inside the loop of game event
				CoreModule.FRUIT_DROP.get().runnable = FruitLeavesBlock.dropFruit(level, pos, state, getDeathRate());
			}
			CoreModule.FRUIT_DROP.get().swallow(this);
			return true;
		} else if (CoreModule.LEAVES_TRAMPLE.get().matches(message.gameEvent())) {
			deathRate += 3;
			CoreModule.LEAVES_TRAMPLE.get().swallow(this);
			return true;
		}
		return false;
	}

}
