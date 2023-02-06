package snownee.fruits.food;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.Hooks;
import snownee.kiwi.block.IKiwiBlock;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.VoxelUtil;

public class FoodBlock extends HorizontalDirectionalBlock implements IKiwiBlock {

	private final VoxelShape[] shapes = new VoxelShape[4];
	public boolean lockShapeRotation = true;

	public FoodBlock(VoxelShape northShape) {
		super(Block.Properties.copy(Blocks.CAKE));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
		shapes[Direction.NORTH.get2DDataValue()] = northShape;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockItem createItem(Item.Properties builder) {
		return new FoodBlockItem(this, builder);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (FoodModule.HONEY_POMELO_TEA.is(stack) && (!Hooks.farmersdelight || !Platform.isProduction())) {
			tooltip.add(Component.translatable("tip.fruittrees.clearHarmfulEffects").withStyle(ChatFormatting.BLUE));
		}
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
		if (!level.isClientSide) {
			level.removeBlock(pos, false);
			ItemStack stack = new ItemStack(this);
			if (!player.addItem(stack)) {
				player.drop(stack, false);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return canSupportRigidBlock(level, pos.below());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		int index = lockShapeRotation ? Direction.NORTH.get2DDataValue() : state.getValue(FACING).get2DDataValue();
		if (shapes[index] == null) {
			shapes[index] = VoxelUtil.rotateHorizontal(shapes[Direction.NORTH.get2DDataValue()], state.getValue(FACING));
		}
		return shapes[index];
	}

}
