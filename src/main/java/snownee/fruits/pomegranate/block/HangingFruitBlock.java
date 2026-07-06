package snownee.fruits.pomegranate.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.block.FruitLeavesBlock;

public class HangingFruitBlock extends HangingRootsBlock {
	public static final MapCodec<HangingRootsBlock> CODEC = simpleCodec(HangingFruitBlock::new);
	protected static final VoxelShape SHAPE = Block.box(3, 5, 3, 13, 15, 13);

	public HangingFruitBlock(Properties builder) {
		super(builder);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		FruitLeavesBlock.giveItemTo(player, hitResult, asItem().getDefaultInstance());
		level.removeBlock(pos, false);
		if (!level.isClientSide()) {
			BlockPos up = pos.above();
			BlockState upState = level.getBlockState(up);
			if (upState.getBlock() instanceof FruitLeavesBlock leavesBlock && leavesBlock.type.value().fruit.get() == asItem() &&
					upState.getValue(FruitLeavesBlock.AGE) == FruitLeavesBlock.FRUITING) {
				leavesBlock.gotoDeadOrYoung((ServerLevel) level, up, upState, null);
			}
		}
		return InteractionResult.SUCCESS_SERVER;
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
		if (level.getBlockState(pos.above()).is(BlockTags.LEAVES)) {
			return true;
		}
		return super.canSurvive(blockState, level, pos);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
		Vec3 vec3 = blockState.getOffset(blockPos);
		return SHAPE.move(vec3.x, vec3.y, vec3.z);
	}

	@Override
	public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
		BlockPos blockPos = blockHitResult.getBlockPos();
		if (level instanceof ServerLevel serverLevel && projectile.mayInteract(serverLevel, blockPos) && projectile.mayBreak(serverLevel)) {
			level.destroyBlock(blockPos, true, projectile);
		}
	}

	@Override
	public MapCodec<HangingRootsBlock> codec() {
		return CODEC;
	}
}
