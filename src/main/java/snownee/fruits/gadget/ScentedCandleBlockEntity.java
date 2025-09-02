package snownee.fruits.gadget;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
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
		Objects.requireNonNull(level);
		int range = getBlockState().getValue(CandleBlock.CANDLES) - 1;
		int i = (int) level.getGameTime() % 50;
		if ((i % 10 != 0) || (i / 10 > range)) {
			return;
		}
		long time = level.getGameTime() + 200;
		ScentType type = ((ScentedCandleBlock) getBlockState().getBlock()).type;
		getChunksAtExactChessboardDistance(level, worldPosition, i / 10).forEach(chunk -> type.setTime(chunk, time));
	}

	public static Stream<ChunkAccess> getChunksAtExactChessboardDistance(Level level, BlockPos pos, int range) {
		int chunkX = SectionPos.blockToSectionCoord(pos.getX());
		int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
		if (range == 0) {
			return Stream.of(level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false)).filter(Objects::nonNull);
		}
		return StreamSupport.stream(
				new Spliterators.AbstractSpliterator<ChunkAccess>(
						range * 8L,
						Spliterator.SIZED) {
					int x = -range;
					int z = -range;

					@Override
					public boolean tryAdvance(Consumer<? super ChunkAccess> action) {
						while (x <= range) {
							if (Math.abs(x) == range) {
								action.accept(level.getChunk(chunkX + x, chunkZ + z, ChunkStatus.FULL, false));
								z++;
								if (z > range) {
									x++;
									z = -range;
								}
								return true;
							} else if (Math.abs(z) == range) {
								action.accept(level.getChunk(chunkX + x, chunkZ + z, ChunkStatus.FULL, false));
								z++;
								if (z > range) {
									x++;
									z = -range;
								}
								return true;
							} else {
								z++;
								if (z > range) {
									x++;
									z = -range;
								}
							}
						}
						return false;
					}
				}, false).filter(Objects::nonNull);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ScentedCandleBlockEntity be) {
		if (be.life <= 0) {
			be.life = 0;
			CommonProxy.extinguishCandle(null, state, level, pos);
			return;
		}
		be.life--;
		be.updateChunks();
		//temp
		if (level.getGameTime() % 50 == 0 && GadgetModule.BUZZY_CRAFTER.is(level.getBlockState(pos.below()))) {
			be.life = 1000;
		}
	}

	public int getLife() {
		return life;
	}
}
