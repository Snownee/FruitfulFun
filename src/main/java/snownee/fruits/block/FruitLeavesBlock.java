package snownee.fruits.block;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FFCommonConfig.DropMode;
import snownee.fruits.FruitType;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.util.CommonProxy;

public class FruitLeavesBlock extends LeavesBlock implements BonemealableBlock, EntityBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

	public final Supplier<FruitType> type;

	public FruitLeavesBlock(Supplier<FruitType> type, Properties properties) {
		super(properties);
		this.type = type;
		registerDefaultState(stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(AGE, 1).setValue(WATERLOGGED, false));
	}

	@Nullable
	public static ItemEntity dropFruit(ServerLevel level, BlockPos pos, BlockState state, @Nullable FruitTreeBlockEntity core, int consumeLifespan) {
		if (state.getValue(AGE) != 3)
			return null;
		if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
			return null;
		boolean die = true;
		if (core != null) {
			core.consumeLifespan(consumeLifespan);
			die = core.isDead();
			if (die) {
				core.removeActiveLeaves(pos);
			}
		}
		state = state.setValue(AGE, die ? 0 : 1);
		if (die && state.hasBlockEntity()) {
			state = state.setValue(PERSISTENT, false);
		}
		level.setBlockAndUpdate(pos, state);
		FruitType type = ((FruitLeavesBlock) state.getBlock()).type.get();
		ItemStack stack = new ItemStack(type.fruit.get());
		float f = EntityType.ITEM.getHeight() / 2.0F;
		double d0 = pos.getX() + 0.5F + Mth.nextDouble(level.random, -0.25D, 0.25D);
		double d1 = pos.getY() + 0.5F + Mth.nextDouble(level.random, -0.25D, 0.25D) - f;
		double d2 = pos.getZ() + 0.5F + Mth.nextDouble(level.random, -0.25D, 0.25D);
		ItemEntity itementity = new ItemEntity(level, d0, d1, d2, stack);
		itementity.setDefaultPickUpDelay();
		if (!level.addFreshEntity(itementity)) {
			return null;
		}
		return itementity;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DISTANCE, PERSISTENT, AGE, WATERLOGGED);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return canGrow(state) || state.getValue(AGE) == 1;
	}

	@Override
	public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		return state.getValue(AGE) != 3;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		world.setBlockAndUpdate(pos, state.cycle(AGE));
	}

	@Nullable
	public FruitTreeBlockEntity findCore(ServerLevel level, BlockPos pos) {
		return level.getPoiManager().findClosest(type.get().poiType::equals, pos, 10, PoiManager.Occupancy.ANY)
				.flatMap(core -> level.getBlockEntity(core, CoreModule.FRUIT_TREE.get())).orElse(null);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (shouldDecay(state)) {
			dropResources(state, world, pos);
			world.removeBlock(pos, false);
		} else if (canGrow(state) && world.getMaxLocalRawBrightness(pos.above()) >= 9) {
			if (state.getValue(AGE) == 3) {
				DropMode mode = FFCommonConfig.getDropMode(world);
				if (mode == DropMode.NO_DROP) {
					return;
				}
				FruitTreeBlockEntity core = findCore(world, pos);
				if (mode == DropMode.ONE_BY_ONE && core != null && !core.canDrop()) {
					return;
				}
				ItemEntity itemEntity = dropFruit(world, pos, state, core, 1);
				if (mode == DropMode.ONE_BY_ONE && core != null && itemEntity != null) {
					core.setOnlyItem(itemEntity);
				}
			} else {
				boolean def = rand.nextInt(100) > (99 - FFCommonConfig.growingSpeed);
				CommonProxy.maybeGrowCrops(world, pos, state, def, () -> performBonemeal(world, rand, pos, state));
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		state = updateDistance(state, world, pos);
		if (state.getValue(PERSISTENT) && state.getValue(DISTANCE) != 1) {
			state = state.setValue(PERSISTENT, false);
		}
		world.setBlockAndUpdate(pos, state);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return shouldDecay(state) || canGrow(state) || state.getValue(AGE) == 0;
	}

	public boolean shouldDecay(BlockState state) {
		return state.getValue(DISTANCE) == 7 && !state.getValue(PERSISTENT);
	}

	public boolean canGrow(BlockState state) {
		return state.getValue(AGE) > 0 && (!state.getValue(PERSISTENT) || state.getValue(DISTANCE) == 1);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (canGrow(state) || state.getValue(AGE) == 0) {
			return super.updateShape(state, facing, facingState, worldIn, currentPos, facingPos);
		}
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(PERSISTENT, true).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext c && c.getEntity() != null) {
			Entity entity = c.getEntity();
			if ((!(entity instanceof ItemEntity) && !(entity instanceof FlyingAnimal))) {
				return Shapes.block();
			}
		}
		return Shapes.empty();
	}

	@Override
	public void fallOn(Level worldIn, BlockState stateIn, BlockPos pos, Entity entityIn, float fallDistance) {
		super.fallOn(worldIn, stateIn, pos, entityIn, fallDistance);
		if (fallDistance >= 1 && worldIn instanceof ServerLevel serverLevel && (entityIn instanceof LivingEntity || entityIn instanceof FallingBlockEntity)) {
			Iterable<BlockPos> posList = BlockPos.betweenClosed(pos.offset(-1, -2, -1), pos.offset(1, 0, 1));
			MutableBoolean success = new MutableBoolean(false);
			rangeDrop(serverLevel, posList, 2, null, itemEntity -> success.setTrue());
			if (success.booleanValue()) {
				//FIXME sound
			}
		}
	}

	public static void rangeDrop(ServerLevel level, Iterable<BlockPos> posList, int consumeLifespan, @Nullable FruitTreeBlockEntity core, @Nullable Consumer<ItemEntity> consumer) {
		for (BlockPos blockpos : posList) {
			BlockState state = level.getBlockState(blockpos);
			if (state.getBlock() instanceof FruitLeavesBlock leavesBlock && state.getValue(AGE) == 3) {
				ItemEntity itemEntity = dropFruit(level, blockpos, state, core == null ? leavesBlock.findCore(level, blockpos) : core, consumeLifespan);
				if (consumer != null && itemEntity != null) {
					consumer.accept(itemEntity);
				}
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult ray) {
		if (state.getValue(AGE) == 3 && worldIn.setBlockAndUpdate(pos, state.setValue(AGE, 1))) {
			if (!worldIn.isClientSide) {
				ItemStack fruit = new ItemStack(type.get().fruit.get());
				if (!CommonProxy.isFakePlayer(playerIn) && playerIn.addItem(fruit)) {
					worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ITEM_PICKUP, playerIn.getSoundSource(), 0.2F, ((playerIn.getRandom().nextFloat() - playerIn.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
				} else {
					popResourceFromFace(worldIn, pos, ray.getDirection(), fruit);
				}
			}
			return InteractionResult.sidedSuccess(worldIn.isClientSide);
		}
		return InteractionResult.PASS;
	}

	//FIXME
//	@Override
//	public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
//		if (entity instanceof FlyingAnimal) {
//			return BlockPathTypes.OPEN;
//		}
//		return null;
//	}

	public boolean hasBlockEntity(BlockState state) {
		return state.getValue(PERSISTENT) && state.getValue(DISTANCE) == 1;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState state) {
		if (hasBlockEntity(state)) {
			return new FruitTreeBlockEntity(pPos, state, type.get());
		}
		return null;
	}

//	@Override
//	public <T extends BlockEntity> GameEventListener getListener(ServerLevel level, T blockEntity) {
//		return blockEntity instanceof FruitTreeBlockEntity ? (FruitTreeBlockEntity) blockEntity : null;
//	}

	@Override
	public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
		return 0.2F;
	}
}
