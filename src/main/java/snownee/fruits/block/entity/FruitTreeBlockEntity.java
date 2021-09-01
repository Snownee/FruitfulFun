package snownee.fruits.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.kiwi.block.entity.BaseBlockEntity;
import snownee.kiwi.util.NBTHelper;

public class FruitTreeBlockEntity extends BaseBlockEntity {

	public FruitType type = FruitType.CITRON;
	private int deathRate = 0;
	private ItemEntity onlyItem;

	public FruitTreeBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.FRUIT_TREE, pos, state);
	}

	public FruitTreeBlockEntity(BlockPos pos, BlockState state, FruitType type) {
		this(pos, state);
		this.type = type;
	}

	public int updateDeathRate() {
		return ++deathRate;
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
	public CompoundTag save(CompoundTag compound) {
		super.save(compound);
		compound.putString("type", type.name());
		compound.putInt("death", deathRate);
		return compound;
	}

	public boolean canDrop() {
		return onlyItem == null || !onlyItem.isAlive();
	}

	public void setOnlyItem(ItemEntity itementity) {
		onlyItem = itementity;
	}
}
