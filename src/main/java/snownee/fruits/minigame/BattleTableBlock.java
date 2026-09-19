package snownee.fruits.minigame;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.block.IKiwiBlock;

public class BattleTableBlock extends BaseEntityBlock implements IKiwiBlock {
	public static final MapCodec<BattleTableBlock> CODEC = simpleCodec(BattleTableBlock::new);

	public BattleTableBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return use(level, pos, player);
	}

	@Override
	protected InteractionResult useItemOn(
			ItemStack itemStack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hitResult) {
		return use(level, pos, player);
	}

	private InteractionResult use(Level level, BlockPos pos, Player player) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		BattleTableBlockEntity table = Objects.requireNonNull((BattleTableBlockEntity) level.getBlockEntity(pos));
		MinigameManager.openTable((ServerPlayer) player, table);
		return InteractionResult.CONSUME;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BattleTableBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level,
			BlockState state,
			BlockEntityType<T> blockEntityType) {
		return null;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}
}
