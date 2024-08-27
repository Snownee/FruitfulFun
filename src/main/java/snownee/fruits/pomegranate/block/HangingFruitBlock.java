package snownee.fruits.pomegranate.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
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
	protected static final VoxelShape SHAPE = Block.box(3, 5, 3, 13, 15, 13);

	public HangingFruitBlock(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResult use(
			BlockState blockState,
			Level level,
			BlockPos blockPos,
			Player player,
			InteractionHand interactionHand,
			BlockHitResult blockHitResult) {
		FruitLeavesBlock.giveItemTo(player, blockHitResult, asItem().getDefaultInstance());
		level.removeBlock(blockPos, false);
		if (!level.isClientSide) {
			BlockPos up = blockPos.above();
			BlockState upState = level.getBlockState(up);
			if (upState.getBlock() instanceof FruitLeavesBlock leavesBlock && leavesBlock.type.get().fruit.get() == asItem() &&
					upState.getValue(FruitLeavesBlock.AGE) == 3) {
				level.setBlockAndUpdate(up, upState.setValue(FruitLeavesBlock.AGE, 1));
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
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
		Vec3 vec3 = blockState.getOffset(blockGetter, blockPos);
		return SHAPE.move(vec3.x, vec3.y, vec3.z);
	}

	@Override
	public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
		//TODO(1.21): gamerule projectilesCanBreakBlocks
		BlockPos blockPos = blockHitResult.getBlockPos();
		if (!level.isClientSide && projectile.mayInteract(level, blockPos) && projectile.getType().is(EntityTypeTags.IMPACT_PROJECTILES)) {
			level.destroyBlock(blockPos, true, projectile);
		}
	}
}
