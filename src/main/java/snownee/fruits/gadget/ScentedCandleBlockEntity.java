package snownee.fruits.gadget;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.util.CommonProxy;

public class ScentedCandleBlockEntity extends BlockEntity {
	private int life;

	public ScentedCandleBlockEntity(BlockPos pos, BlockState state) {
		super(GadgetModule.SCENTED_CANDLE_ENTITY.getOrCreate(), pos, state);
		life = 100;
	}

	@Override
	public void load(CompoundTag tag) {
		life = tag.getInt("Life");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		tag.putInt("Life", life);
	}

	public void updateChunks() {
		LevelChunk chunk = Objects.requireNonNull(level).getChunkAt(worldPosition);
		long time = level.getGameTime() + 200;
		ScentType type = ((ScentedCandleBlock) getBlockState().getBlock()).type;
		type.setTime(chunk, time);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ScentedCandleBlockEntity be) {
		be.life--;
		if (be.life <= 0) {
			be.life = 0;
			CommonProxy.extinguishCandle(null, state, level, pos);
			return;
		}
		if (level.getGameTime() % 50 != 0) {
			be.updateChunks();
		}
	}

	public int getLife() {
		return life;
	}
}
