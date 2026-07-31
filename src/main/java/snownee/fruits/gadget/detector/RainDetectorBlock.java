package snownee.fruits.gadget.detector;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 6, 16);

	public RainDetectorBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(POWER);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return new RainDetectorBlockEntity(worldPosition, blockState);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level, BlockState blockState, BlockEntityType<T> type) {
		return !level.isClientSide && level.dimensionType().hasSkyLight()
				? createTickerHelper(type, GadgetModule.RAIN_DETECTOR_ENTITY.get(), RainDetectorBlock::tickEntity)
				: null;
	}

	private static void tickEntity(
			Level level, BlockPos blockPos, BlockState blockState, RainDetectorBlockEntity blockEntity) {
		if (level.getGameTime() % 20L != 0L) {
			return;
		}
		int target = Mth.clamp((int) (level.getRainLevel(1) * 15), 0, 15);
		if (blockState.getValue(POWER) != target) {
			level.setBlock(blockPos, blockState.setValue(POWER, target), 3);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
