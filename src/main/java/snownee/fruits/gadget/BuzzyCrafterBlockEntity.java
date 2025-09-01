package snownee.fruits.gadget;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BuzzyCrafterBlockEntity extends BeehiveBlockEntity {
	public BuzzyCrafterBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
		type = GadgetModule.BUZZY_CRAFTER_ENTITY.get();
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
	}
}
