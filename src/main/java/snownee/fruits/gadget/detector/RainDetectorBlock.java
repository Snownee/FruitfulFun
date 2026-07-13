package snownee.fruits.gadget.detector;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.gadget.GadgetModule;

public class RainDetectorBlock extends BaseEntityBlock {
	public static final MapCodec<RainDetectorBlock> CODEC = simpleCodec(RainDetectorBlock::new);
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 6.0);

	public RainDetectorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean useShapeForLightOcclusion(final BlockState state) {
		return true;
	}

	@Override
	protected int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
		return state.getValue(POWER);
	}

	@Override
	protected boolean isSignalSource(final BlockState state) {
		return true;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return new RainDetectorBlockEntity(worldPosition, blockState);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
			final Level level,
			final BlockState blockState,
			final BlockEntityType<T> type) {
		return !level.isClientSide() && level.dimensionType().hasSkyLight()
				? createTickerHelper(type, GadgetModule.RAIN_DETECTOR_ENTITY.get(), RainDetectorBlock::tickEntity)
				: null;
	}

	private static void tickEntity(
			final Level level,
			final BlockPos blockPos,
			final BlockState blockState,
			final RainDetectorBlockEntity blockEntity) {
		if (level.getGameTime() % 20L != 0L || !level.canHaveWeather()) {
			return;
		}
		int target = (int) (level.getRainLevel(1) * 15);
		target = Mth.clamp(target, 0, 15);
		if (blockState.getValue(POWER) != target) {
			level.setBlock(blockPos, blockState.setValue(POWER, target), 3);
		}
	}

	@Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}
}
