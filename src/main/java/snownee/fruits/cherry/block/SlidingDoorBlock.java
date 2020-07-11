package snownee.fruits.cherry.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import snownee.fruits.cherry.CherryModule;

@SuppressWarnings("hiding")
public class SlidingDoorBlock extends DoorBlock {
    protected static final VoxelShape[] SOUTH_AABB = { Block.makeCuboidShape(0, 0, 3, 16, 16, 4), Block.makeCuboidShape(13, 0, 3, 29, 16, 4), Block.makeCuboidShape(-13, 0, 3, 3, 16, 4) };
    protected static final VoxelShape[] NORTH_AABB = { Block.makeCuboidShape(0, 0, 12, 16, 16, 13), Block.makeCuboidShape(-13, 0, 12, 3, 16, 13), Block.makeCuboidShape(13, 0, 12, 29, 16, 13) };
    protected static final VoxelShape[] WEST_AABB = { Block.makeCuboidShape(12, 0, 0, 13, 16, 16), Block.makeCuboidShape(12, 0, 13, 13, 16, 29), Block.makeCuboidShape(12, 0, -13, 13, 16, 3) };
    protected static final VoxelShape[] EAST_AABB = { Block.makeCuboidShape(3, 0, 0, 4, 16, 16), Block.makeCuboidShape(3, 0, -13, 4, 16, 3), Block.makeCuboidShape(3, 0, 13, 4, 16, 29) };
    protected static final VoxelShape[][] AABB = { SOUTH_AABB, WEST_AABB, NORTH_AABB, EAST_AABB };

    public SlidingDoorBlock(Block.Properties builder) {
        super(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        int index = 0;
        if (state.get(OPEN) == Boolean.TRUE) {
            ++index;
            if (state.get(HINGE) == DoorHingeSide.RIGHT) {
                ++index;
            }
        }
        return AABB[state.get(FACING).getHorizontalIndex()][index];
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            return facingState.getBlock() == this && facingState.get(HALF) != doubleblockhalf ? stateIn.with(FACING, facingState.get(FACING)).with(OPEN, facingState.get(OPEN)).with(HINGE, facingState.get(HINGE)).with(POWERED, facingState.get(POWERED)) : Blocks.AIR.getDefaultState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.get(HALF) != doubleblockhalf) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
            ItemStack itemstack = player.getHeldItemMainhand();
            if (!worldIn.isRemote && !player.isCreative() && player./*canHarvestBlock*/func_234569_d_(blockstate)) {
                Block.spawnDrops(state, worldIn, pos, (TileEntity) null, player, itemstack);
                Block.spawnDrops(blockstate, worldIn, blockpos, (TileEntity) null, player, itemstack);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)) {
            World world = context.getWorld();
            boolean flag = world.isBlockPowered(blockpos) || world.isBlockPowered(blockpos.up());
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HINGE, this.getHingeSide(context)).with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)).with(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    private DoorHingeSide getHingeSide(BlockItemUseContext p_208073_1_) {
        IBlockReader iblockreader = p_208073_1_.getWorld();
        BlockPos blockpos = p_208073_1_.getPos();
        Direction direction = p_208073_1_.getPlacementHorizontalFacing();
        BlockPos blockpos1 = blockpos.up();
        Direction direction1 = direction.rotateYCCW();
        BlockPos blockpos2 = blockpos.offset(direction1);
        BlockState blockstate = iblockreader.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.offset(direction1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
        Direction direction2 = direction.rotateY();
        BlockPos blockpos4 = blockpos.offset(direction2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.offset(direction2);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
        int i = (blockstate./*isCollisionShapeOpaque*/func_235785_r_(iblockreader, blockpos2) ? -1 : 0) + (blockstate1./*isCollisionShapeOpaque*/func_235785_r_(iblockreader, blockpos3) ? -1 : 0) + (blockstate2./*isCollisionShapeOpaque*/func_235785_r_(iblockreader, blockpos4) ? 1 : 0) + (blockstate3./*isCollisionShapeOpaque*/func_235785_r_(iblockreader, blockpos5) ? 1 : 0);
        boolean flag = blockstate.getBlock() == this && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
        boolean flag1 = blockstate2.getBlock() == this && blockstate2.get(HALF) == DoubleBlockHalf.LOWER;
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getXOffset();
                int k = direction.getZOffset();
                Vector3d vec3d = p_208073_1_.getHitVec();
                double d0 = vec3d.x - blockpos.getX();
                double d1 = vec3d.z - blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (this.material == Material.IRON) {
            return ActionResultType.PASS;
        } else {
            state = state.func_235896_a_/*cycle*/(OPEN);
            worldIn.setBlockState(pos, state, 10);
            playSound(worldIn, pos, state.get(OPEN) == Boolean.TRUE);
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.getBlock() == this && blockstate.get(OPEN) != open) {
            worldIn.setBlockState(pos, blockstate.with(OPEN, Boolean.valueOf(open)), 10);
            this.playSound(worldIn, pos, open);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (blockIn != this && flag != state.get(POWERED)) {
            if (flag != state.get(OPEN)) {
                this.playSound(worldIn, pos, flag);
            }

            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)), 2);
        }

    }

    private void playSound(World worldIn, BlockPos pos, boolean isOpening) {
        worldIn.playSound(null, pos, isOpening ? CherryModule.OPEN_SOUND : CherryModule.CLOSE_SOUND, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
    }

}
