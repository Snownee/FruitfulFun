package snownee.fruits.gadget;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ScentedCandleBlock extends CandleBlock implements EntityBlock {
	public final ScentType type;

	public ScentedCandleBlock(Properties properties, ScentType type) {
		super(properties);
		this.type = type;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ScentedCandleBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level,
			BlockState state,
			BlockEntityType<T> blockEntityType) {
		if (!state.getValue(LIT)) {
			return null;
		}
		return level.isClientSide ? null : createTickerHelper(
				blockEntityType,
				GadgetModule.SCENTED_CANDLE_ENTITY.get(),
				ScentedCandleBlockEntity::serverTick);
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> serverType,
			BlockEntityType<E> clientType,
			BlockEntityTicker<? super E> ticker) {
		//noinspection unchecked
		return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide() && !state.getValue(LIT) && level.getBlockEntity(pos) instanceof ScentedCandleBlockEntity be &&
				be.getLife() == 0) {
			player.displayClientMessage(Component.translatable("tip.fruitfulfun.candleNoLife"), true);
		}
		return super.use(state, level, pos, player, hand, hit);
	}
}
