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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import snownee.fruits.util.CommonProxy;

// 每只蜜蜂平均每天可以采4次蜜，每次采蜜获得10000能量
// 期望一般情况下3只蜜蜂可以驱动4根蜡烛
// 每天1只蜜蜂采集4次，获得40000能量，驱动1.3333根蜡烛
// 所以1根蜡烛1天可获得的能量为：30000
// 所以1根蜡烛1天消耗的能量为：24000 * 1.2 = 28800
public class ScentedCandleBlockEntity extends BlockEntity {
	public static final float BASE_POWER_RATE = 1.2f;
	private BuzzyPowerStorage power = new BuzzyPowerStorage(50000f);
	private boolean creative;

	public ScentedCandleBlockEntity(BlockPos pos, BlockState state) {
		super(GadgetModule.SCENTED_CANDLE_ENTITY.getOrCreate(), pos, state);
	}

	@Override
	public void load(CompoundTag tag) {
		if (tag.contains("power")) {
			power = BuzzyPowerStorage.read(tag.getCompound("power")).result().orElse(power);
		}
		creative = tag.getBoolean("creative");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		tag.put("power", power.save());
		if (creative) {
			tag.putBoolean("creative", true);
		}
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
		if (!be.power().hasLife()) {
			CommonProxy.extinguishCandle(null, state, level, pos);
			return;
		}
		be.power().useLife(BASE_POWER_RATE * state.getValue(CandleBlock.CANDLES));
		be.updateChunks();
	}

	public void addCandle(ItemStack stack) {
		BuzzyPowerStorage.read(stack).ifPresentOrElse(
				$ -> {
					if (getBlockState().getValue(CandleBlock.CANDLES) == 1) {
						power = $;
					} else {
						power.merge($);
					}
				}, () -> {
					if (getBlockState().getValue(CandleBlock.CANDLES) != 1) {
						power.addMaxLife(50000f);
					}
				});
	}

	public void setCreative(boolean creative) {
		this.creative = creative;
		if (creative && !power.hasLife()) {
			power.addLife(100000);
		}
	}

	public boolean isCreative() {
		return creative;
	}

	public BuzzyPowerStorage power() {
		return power;
	}
}
