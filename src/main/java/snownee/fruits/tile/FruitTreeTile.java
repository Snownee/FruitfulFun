package snownee.fruits.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.kiwi.tile.BaseTile;
import snownee.kiwi.util.NBTHelper;

public class FruitTreeTile extends BaseTile {

	public FruitType type = FruitType.CITRON;
	private int deathRate = 0;
	private ItemEntity onlyItem;

	public FruitTreeTile() {
		super(CoreModule.FRUIT_TREE);
	}

	public FruitTreeTile(FruitType type) {
		this();
		this.type = type;
	}

	public int updateDeathRate() {
		return ++deathRate;
	}

	@Override
	protected void readPacketData(CompoundNBT data) {
	}

	@Override
	protected CompoundNBT writePacketData(CompoundNBT data) {
		return data;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		NBTHelper helper = NBTHelper.of(compound);
		String id = helper.getString("type");
		if (id != null) {
			type = FruitType.parse(id);
			if (type == null) {
				type = FruitType.CITRON;
			}
		} else {
			FruitType[] types = FruitType.values();
			type = types[MathHelper.clamp(helper.getInt("type"), 0, types.length)];
		}
		deathRate = helper.getInt("death");
		super.read(state, compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
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
