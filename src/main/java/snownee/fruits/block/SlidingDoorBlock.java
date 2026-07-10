package snownee.fruits.block;

import java.util.Objects;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.CoreModule;
import snownee.fruits.block.entity.SlidingDoorEntity;

@SuppressWarnings("hiding")
public class SlidingDoorBlock extends DoorBlock {
	protected static final VoxelShape[] SOUTH_AABB = {
			Block.box(0, 0, 3, 16, 16, 4), Block.box(13, 0, 3, 29, 16, 4), Block.box(-13, 0, 3, 3, 16, 4)};
	protected static final VoxelShape[] NORTH_AABB = {
			Block.box(0, 0, 12, 16, 16, 13), Block.box(-13, 0, 12, 3, 16, 13), Block.box(13, 0, 12, 29, 16, 13)};
	protected static final VoxelShape[] WEST_AABB = {
			Block.box(12, 0, 0, 13, 16, 16), Block.box(12, 0, 13, 13, 16, 29), Block.box(12, 0, -13, 13, 16, 3)};
	protected static final VoxelShape[] EAST_AABB = {
			Block.box(3, 0, 0, 4, 16, 16), Block.box(3, 0, -13, 4, 16, 3), Block.box(3, 0, 13, 4, 16, 29)};
	protected static final VoxelShape[][] AABB = {SOUTH_AABB, WEST_AABB, NORTH_AABB, EAST_AABB};

	public SlidingDoorBlock(BlockSetType blockSetType, Properties builder) {
		super(blockSetType, builder);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return getActualShape(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(OPEN) ? Shapes.empty() : getActualShape(state);
	}

	@Override
	protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return getActualShape(state);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getActualShape(state);
	}

	public static VoxelShape getActualShape(BlockState state) {
		int index = 0;
		if (state.getValue(OPEN)) {
			++index;
			if (state.getValue(HINGE) == DoorHingeSide.RIGHT) {
				++index;
			}
		}
		return AABB[state.getValue(FACING).get2DDataValue()][index];
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (type().canOpenByHand()) {
			setOpen(player, level, state, pos, !state.getValue(OPEN));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open) {
		if (state.is(this) && state.getValue(OPEN) != open) {
			level.gameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
			if (open) {
				if (!level.isClientSide()) {
					if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
						pos = pos.below();
					}
					SlidingDoorEntity door = new SlidingDoorEntity(level);
					door.setDoorPos(pos);
					level.addFreshEntity(door);
					level.scheduleTick(pos, this, 1);
				}
			} else {
				runEntity(state, level, pos, e -> e.setOpen(!e.isOpen()));
			}
			playSound(level, pos, open);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		level.setBlock(pos, state.setValue(OPEN, true), 10);
		runEntity(state, level, pos, e -> e.setOpen(true));
	}

	@Override
	protected void neighborChanged(
			BlockState state,
			Level level,
			BlockPos pos,
			Block block,
			@Nullable Orientation orientation,
			boolean movedByPiston) {
		boolean powered = isPowered(state, level, pos);
		if (block != this && powered != state.getValue(POWERED)) {
			state = state.setValue(POWERED, powered);
			level.setBlock(pos, state, 2);

			if (powered != state.getValue(OPEN)) {
				setOpen(null, level, state, pos, powered);
			}
		}
	}

	public static boolean isPowered(BlockState state, Level level, BlockPos pos) {
		return level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(
				state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
	}

	private void playSound(Level worldIn, BlockPos pos, boolean isOpening) {
		worldIn.playSound(
				null,
				pos,
				isOpening ? CoreModule.OPEN_SOUND.get() : CoreModule.CLOSE_SOUND.get(),
				SoundSource.BLOCKS,
				1.0F,
				worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		runEntity(state, level, pos, Entity::discard);
	}

	public void runEntity(BlockState blockState, Level level, BlockPos pos, Consumer<SlidingDoorEntity> consumer) {
		BlockPos bottomPos = blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
		level.getEntities(CoreModule.SLIDING_DOOR.get(), new AABB(bottomPos), e -> Objects.equals(e.doorPos(), bottomPos))
				.forEach(consumer);
	}
}
