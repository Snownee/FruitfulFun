package snownee.fruits.cherry.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.CoreModule;

@SuppressWarnings("hiding")
public class SlidingDoorBlock extends DoorBlock {
	protected static final VoxelShape[] SOUTH_AABB = { Block.box(0, 0, 3, 16, 16, 4), Block.box(13, 0, 3, 29, 16, 4), Block.box(-13, 0, 3, 3, 16, 4) };
	protected static final VoxelShape[] NORTH_AABB = { Block.box(0, 0, 12, 16, 16, 13), Block.box(-13, 0, 12, 3, 16, 13), Block.box(13, 0, 12, 29, 16, 13) };
	protected static final VoxelShape[] WEST_AABB = { Block.box(12, 0, 0, 13, 16, 16), Block.box(12, 0, 13, 13, 16, 29), Block.box(12, 0, -13, 13, 16, 3) };
	protected static final VoxelShape[] EAST_AABB = { Block.box(3, 0, 0, 4, 16, 16), Block.box(3, 0, -13, 4, 16, 3), Block.box(3, 0, 13, 4, 16, 29) };
	protected static final VoxelShape[][] AABB = { SOUTH_AABB, WEST_AABB, NORTH_AABB, EAST_AABB };

	public SlidingDoorBlock(Block.Properties builder) {
		super(builder);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		int index = 0;
		if (state.getValue(OPEN) == Boolean.TRUE) {
			++index;
			if (state.getValue(HINGE) == DoorHingeSide.RIGHT) {
				++index;
			}
		}
		return AABB[state.getValue(FACING).get2DDataValue()][index];
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
			return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, facingState.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		}
	}

	//    @Override
	//    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
	//        return false;
	//    }

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		if (blockpos.getY() < 255 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)) {
			Level world = context.getLevel();
			boolean flag = world.hasNeighborSignal(blockpos) || world.hasNeighborSignal(blockpos.above());
			return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, getHingeSide(context)).setValue(POWERED, flag).setValue(OPEN, flag).setValue(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	private DoorHingeSide getHingeSide(BlockPlaceContext p_208073_1_) {
		BlockGetter iblockreader = p_208073_1_.getLevel();
		BlockPos blockpos = p_208073_1_.getClickedPos();
		Direction direction = p_208073_1_.getHorizontalDirection();
		BlockPos blockpos1 = blockpos.above();
		Direction direction1 = direction.getCounterClockWise();
		BlockPos blockpos2 = blockpos.relative(direction1);
		BlockState blockstate = iblockreader.getBlockState(blockpos2);
		BlockPos blockpos3 = blockpos1.relative(direction1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
		Direction direction2 = direction.getClockWise();
		BlockPos blockpos4 = blockpos.relative(direction2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
		BlockPos blockpos5 = blockpos1.relative(direction2);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
		int i = (blockstate.isCollisionShapeFullBlock(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeFullBlock(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.isCollisionShapeFullBlock(iblockreader, blockpos5) ? 1 : 0);
		boolean flag = blockstate.getBlock() == this && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
		boolean flag1 = blockstate2.getBlock() == this && blockstate2.getValue(HALF) == DoubleBlockHalf.LOWER;
		if ((!flag || flag1) && i <= 0) {
			if ((!flag1 || flag) && i >= 0) {
				int j = direction.getStepX();
				int k = direction.getStepZ();
				Vec3 vec3d = p_208073_1_.getClickLocation();
				double d0 = vec3d.x - blockpos.getX();
				double d1 = vec3d.z - blockpos.getZ();
				return (j >= 0 || (d1 >= 0.5D)) && (j <= 0 || (d1 <= 0.5D)) && (k >= 0 || (d0 <= 0.5D)) && (k <= 0 || (d0 >= 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			} else {
				return DoorHingeSide.LEFT;
			}
		} else {
			return DoorHingeSide.RIGHT;
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (material != Material.METAL) {
			setOpen(player, worldIn, state, pos, !state.getValue(OPEN));
			return InteractionResult.sidedSuccess(worldIn.isClientSide);
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setOpen(@Nullable Entity p_153166_, Level p_153167_, BlockState p_153168_, BlockPos p_153169_, boolean p_153170_) {
		if (p_153168_.is(this) && p_153168_.getValue(OPEN) != p_153170_) {
			p_153167_.setBlock(p_153169_, p_153168_.setValue(OPEN, p_153170_), 10);
			playSound(p_153167_, p_153169_, p_153170_);
			p_153167_.gameEvent(p_153166_, p_153170_ ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_153169_);
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		boolean flag = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (blockIn != this && flag != state.getValue(POWERED)) {
			if (flag != state.getValue(OPEN)) {
				playSound(worldIn, pos, flag);
			}

			worldIn.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
		}

	}

	private void playSound(Level worldIn, BlockPos pos, boolean isOpening) {
		worldIn.playSound(null, pos, isOpening ? CoreModule.OPEN_SOUND : CoreModule.CLOSE_SOUND, SoundSource.BLOCKS, 1.0F, worldIn.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (worldIn.isClientSide)
			return;
		if (!state.getValue(OPEN) || state.getValue(HALF) != DoubleBlockHalf.LOWER)
			return;
		if (oldState.getBlock() == this && oldState.getValue(OPEN))
			return;
		SlidingDoorEntity door = new SlidingDoorEntity(worldIn, pos);
		worldIn.addFreshEntity(door);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, worldIn, pos, newState, isMoving);
		if (worldIn.isClientSide)
			return;
		if (!state.getValue(OPEN) || state.getValue(HALF) != DoubleBlockHalf.LOWER)
			return;
		if (newState.getBlock() == this && newState.getValue(OPEN))
			return;
		worldIn.getEntities(CoreModule.SLIDING_DOOR, new AABB(pos), e -> e.doorPos.equals(pos)).forEach(e -> e.remove(RemovalReason.KILLED));
	}
}
