package snownee.fruits.gadget.detector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.gadget.GadgetModule;

public class RainDetectorBlockEntity extends BlockEntity {
	public RainDetectorBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(GadgetModule.RAIN_DETECTOR_ENTITY.get(), worldPosition, blockState);
	}
}
