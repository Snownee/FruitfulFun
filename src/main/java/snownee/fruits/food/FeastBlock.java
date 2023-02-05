package snownee.fruits.food;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FeastBlock extends FoodBlock {

	public final Supplier<Item> servingItem;
	private final VoxelShape leftoverShape = Block.box(2, 0, 2, 14, 1, 14);

	public FeastBlock(VoxelShape northShape, Supplier<Item> servingItem) {
		super(northShape);
		this.servingItem = servingItem;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(getServingsProperty(), getMaxServings()));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_60508_) {
		int serves = getServings(state);
		ItemStack stack = new ItemStack(servingItem.get());
		ItemStack remainder = stack.getCraftingRemainingItem();
		if (serves == 0) {
			if (!level.isClientSide) {
				level.destroyBlock(pos, true, player);
			}
		} else if (player.getItemInHand(hand).sameItem(remainder)) {
			if (!level.isClientSide) {
				level.setBlockAndUpdate(pos, state.setValue(getServingsProperty(), serves - 1));
				if (!player.getAbilities().instabuild) {
					player.getItemInHand(hand).shrink(1);
				}
				if (!player.addItem(stack)) {
					player.drop(stack, false);
				}
			}
		} else if (serves == 4 && player.getItemInHand(hand).isEmpty()) {
			if (!level.isClientSide) {
				level.removeBlock(pos, false);
				ItemStack blockItem = new ItemStack(this);
				if (!player.addItem(blockItem)) {
					player.drop(blockItem, false);
				}
			}
		} else {
			if (level.isClientSide) {
				player.displayClientMessage(Component.translatable("tip.fruittrees.useContainer", remainder.getHoverName()), true);
			}
			return InteractionResult.PASS;
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, getServingsProperty());
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
		return getServings(blockState);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	public IntegerProperty getServingsProperty() {
		return BlockStateProperties.AGE_4;
	}

	public int getMaxServings() {
		return 4;
	}

	public int getServings(BlockState state) {
		return state.getValue(getServingsProperty());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (getServings(state) == 0) {
			return leftoverShape;
		}
		return super.getShape(state, level, pos, context);
	}

}
