package snownee.fruits.gadget;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.ContainerSingleItem;
import snownee.kiwi.block.IKiwiBlock;
import snownee.kiwi.item.ModBlockItem;

public class BuzzyCrafterBlock extends BeehiveBlock implements IKiwiBlock {
	public static final MapCodec<BeehiveBlock> CODEC = simpleCodec(BuzzyCrafterBlock::new);

	public BuzzyCrafterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		// No particles
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BuzzyCrafterBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level,
			BlockState state,
			BlockEntityType<T> blockEntityType) {
		return level.isClientSide() ? null : BeehiveBlock.createTickerHelper(
				blockEntityType,
				GadgetModule.BUZZY_CRAFTER_ENTITY.get(),
				BeehiveBlockEntity::serverTick);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (doesHitTop(hitResult)) {
			return useTop(state, level, pos, player, InteractionHand.MAIN_HAND, hitResult);
		} else {
			return useSide(state, level, pos, player, InteractionHand.MAIN_HAND, hitResult);
		}
	}

	protected InteractionResult useSide(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (pPlayer.isCreative() && pLevel.getBlockEntity(pPos) instanceof BuzzyCrafterBlockEntity be) {
			ItemStack held = pPlayer.getItemInHand(pHand);
			if (held.is(Items.RED_DYE)) {
				be.debugAddPower(BuzzyPowerType.RED, 1);
				return InteractionResult.SUCCESS;
			} else if (held.is(Items.BLUE_DYE)) {
				be.debugAddPower(BuzzyPowerType.BLUE, 1);
				return InteractionResult.SUCCESS;
			} else if (held.is(Items.GREEN_DYE)) {
				be.debugAddPower(BuzzyPowerType.GREEN, 1);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	protected InteractionResult useTop(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (!(pLevel.getBlockEntity(pPos) instanceof Container container)) {
			return InteractionResult.FAIL;
		}
		if (pLevel.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		ItemStack held = pPlayer.getItemInHand(pHand);
		if (held.isEmpty()) {
			grab(pState, pLevel, pPos, pPlayer);
			return InteractionResult.CONSUME;
		}
		if (held.getItem() instanceof BlockItem blockItem && pLevel.getBlockState(pPos.above()).canBeReplaced()) {
			BlockState blockState = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(pPlayer, pHand, held, pHit));
			if (blockState != null && !blocksContainer(blockState)) {
				return InteractionResult.PASS;
			}
		}
		insertItem(container, pPlayer.getAbilities().instabuild ? held.copy() : held);
		return InteractionResult.CONSUME;
	}

	public void click(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {
		if (doesHitTop(hit)) {
			clickTop(blockState, level, pos, player, hit);
		} else {
			clickSide(blockState, level, pos, player, hit);
		}
	}

	protected void clickSide(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {}

	protected void clickTop(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {
		grab(blockState, level, pos, player);
	}

	public boolean insertItem(Container container, ItemStack itemStack) {
		if (!container.canPlaceItem(0, itemStack)) {
			return false;
		}
		ItemStack displayed = container.getItem(0);
		if (displayed.isEmpty() || ItemStack.isSameItemSameComponents(displayed, itemStack)) {
			int maxSize = Math.min(itemStack.getMaxStackSize(), container.getMaxStackSize());
			int transferAmount = Math.min(itemStack.getCount(), maxSize - displayed.getCount());
			if (transferAmount > 0) {
				ItemStack split = itemStack.split(transferAmount);
				split.grow(displayed.getCount());
				container.setItem(0, split);
				return true;
			}
		}
		return false;
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		super.stepOn(pLevel, pPos, pState, pEntity);
		if (!pLevel.isClientSide() && pEntity instanceof ItemEntity itemEntity && itemEntity.hasPickUpDelay() &&
				pLevel.getBlockEntity(pPos) instanceof Container container) {
			if (insertItem(container, itemEntity.getItem())) {
				itemEntity.setItem(itemEntity.getItem()); // send update packet
			}
		}
	}

	public void grab(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		if (pLevel.isClientSide() || !(pLevel.getBlockEntity(pPos) instanceof ContainerSingleItem be)) {
			return;
		}
		ItemStack item = be.removeTheItem();
		if (item.isEmpty()) {
			return;
		}
		double d3 = pPos.getX() + 0.5;
		double d4 = pPos.getY() + 1;
		double d5 = pPos.getZ() + 0.5;
		ItemEntity itementity = new ItemEntity(pLevel, d3, d4, d5, item);
		itementity.setDeltaMovement(0, 0.2, 0);
		pLevel.addFreshEntity(itementity);
		itementity.playerTouch(pPlayer);
	}

	public boolean doesHitTop(BlockHitResult pHit) {
		return pHit.getDirection() == Direction.UP && pHit.getLocation().y - pHit.getBlockPos().getY() > 0.75;
	}

	public boolean canBeDestroyed(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return !(level.getBlockEntity(pos) instanceof ContainerSingleItem container) || container.getTheItem().isEmpty();
	}

	@Override
	protected BlockState updateShape(
			BlockState state,
			LevelReader level,
			ScheduledTickAccess ticks,
			BlockPos pos,
			Direction directionToNeighbour,
			BlockPos neighbourPos,
			BlockState neighbourState,
			RandomSource random) {
		if (directionToNeighbour == Direction.UP && level.getBlockEntity(pos) instanceof BuzzyCrafterBlockEntity be) {
			be.setBlocked(blocksContainer(neighbourState));
			be.setHasBlockAbove(!neighbourState.canBeReplaced());
			be.updateBlockPowerReceiver();
			be.refresh();
		}
		return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
	}

	public static boolean blocksContainer(BlockState blockState) {
		if (!blockState.getFluidState().isEmpty()) {
			return true;
		}
		return !blockState.isAir() && !blockState.is(GadgetModule.SUSTAIN_CRAFTER_ITEM);
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		if (!(level.getBlockEntity(pos) instanceof BuzzyCrafterBlockEntity be)) {
			return 0;
		}
		return be.getAnalogOutput();
	}

	@Override
	public BlockItem createItem(Item.Properties builder) {
		return new ModBlockItem(this, builder.component(GadgetModule.HIDE_HONEY_LEVEL.getOrCreate(), Unit.INSTANCE));
	}

	@Override
	public MapCodec<BeehiveBlock> codec() {
		return CODEC;
	}
}
