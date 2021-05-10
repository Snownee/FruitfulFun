package snownee.fruits.cherry.block;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class SlidingDoorTileEntity extends TileEntity implements ITickableTileEntity {

	private boolean opening;
	private int ticks;

	public SlidingDoorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

}
