package snownee.fruits.cherry.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SlidingDoorBlockEntity extends BlockEntity {

	private boolean opening;
	private int ticks;

	public SlidingDoorBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, null, null);
		// TODO Auto-generated constructor stub
	}

}
