package snownee.fruits.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.fruits.FruitsConfig;
import snownee.fruits.FruitsConfig.DropMode;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.levelgen.treedecorators.CarpetTreeDecorator;

public class FruitLeavesBlock extends LeavesBlock implements BonemealableBlock, EntityBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

	public final Supplier<FruitType> type;

	public FruitLeavesBlock(Supplier<FruitType> type, Properties properties) {
		super(properties);
		this.type = type;
		registerDefaultState(stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(AGE, 1));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DISTANCE, PERSISTENT, AGE);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return canGrow(state) || state.getValue(AGE) == 1;
	}

	@Override
	public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {
		return state.getValue(AGE) != 3;
	}

	@Override
	public void performBonemeal(ServerLevel world, Random rand, BlockPos pos, BlockState state) {
		world.setBlockAndUpdate(pos, state.cycle(AGE));
	}

	public static Supplier<ItemEntity> dropFruit(Level level, BlockPos pos, BlockState state, float deathRate) {
		if (level.isClientSide || state.getValue(AGE) != 3)
			return () -> null;
		if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
			return () -> null;
		boolean die = deathRate == 1;
		if (deathRate > 0) {
			die = level.random.nextFloat() < deathRate;
		}
		state = state.setValue(AGE, die ? 0 : 1);
		if (deathRate == 1) {
			state = state.setValue(PERSISTENT, false);
		}
		BlockState newState = state;

		FruitType type = ((FruitLeavesBlock) state.getBlock()).type.get();
		ItemStack stack = new ItemStack(type.fruit);
		if (!stack.isEmpty() && !level.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
			float f = EntityType.ITEM.getHeight() / 2.0F;
			double d0 = pos.getX() + 0.5F + Mth.nextDouble(level.random, -0.25D, 0.25D);
			double d1 = pos.getY() + 0.5F + Mth.nextDouble(level.random, -0.25D, 0.25D) - f;
			double d2 = pos.getZ() + 0.5F + Mth.nextDouble(level.random, -0.25D, 0.25D);
			ItemEntity itementity = new ItemEntity(level, d0, d1, d2, stack);
			itementity.setDefaultPickUpDelay();
			if (level.addFreshEntity(itementity)) {
				return () -> {
					level.setBlockAndUpdate(pos, newState);
					return itementity;
				};
			}
		}
		return () -> null;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
		if (shouldDecay(state)) {
			dropResources(state, world, pos);
			world.removeBlock(pos, false);
		} else if (canGrow(state) && world.isAreaLoaded(pos, 1) && world.getMaxLocalRawBrightness(pos.above()) >= 9) {
			int r = rand.nextInt(100);

			if (r < 10 && type.get().carpet != null) {
				CarpetTreeDecorator.placeCarpet(world, pos, type.get().carpet.defaultBlockState(), world::setBlockAndUpdate);
			}

			boolean def = r > (99 - FruitsConfig.growingSpeed);

			if (state.getValue(AGE) == 3) {
				DropMode mode = FruitsConfig.getDropMode(world);
				if (mode == DropMode.NO_DROP) {
					return;
				}
				GameEventListener receiver = CoreModule.FRUIT_DROP.post(world, pos, null);
				if (receiver == null) {
					dropFruit(world, pos, state, 0.6F).get();
				}
			} else if (ForgeHooks.onCropsGrowPre(world, pos, state, def)) {
				performBonemeal(world, rand, pos, state);
				ForgeHooks.onCropsGrowPost(world, pos, state);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
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
		return defaultBlockState().setValue(PERSISTENT, true);
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
		if (!worldIn.isClientSide && fallDistance >= 1 && (entityIn instanceof LivingEntity || entityIn instanceof FallingBlockEntity)) {
			GameEventListener receiver = CoreModule.LEAVES_TRAMPLE.post(worldIn, pos, entityIn);
			float deathRate = 1;
			if (receiver instanceof FruitTreeBlockEntity) {
				deathRate = ((FruitTreeBlockEntity) receiver).getDeathRate();
			}
			for (BlockPos pos2 : BlockPos.betweenClosed(pos.getX() - 1, pos.getY() - 2, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1)) {
				BlockState state = worldIn.getBlockState(pos2);
				if (state.getBlock() instanceof FruitLeavesBlock) {
					dropFruit(worldIn, pos2, state, deathRate).get();
					if (type.get().carpet != null) {
						CarpetTreeDecorator.placeCarpet(worldIn, pos2, type.get().carpet.defaultBlockState(), worldIn::setBlockAndUpdate);
					}
				}
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult ray) {
		if (state.getValue(AGE) == 3 && worldIn.setBlockAndUpdate(pos, state.setValue(AGE, 1))) {
			if (!worldIn.isClientSide) {
				ItemStack fruit = new ItemStack(type.get().fruit);
				if (playerIn instanceof FakePlayer) {
					popResourceFromFace(worldIn, pos, ray.getDirection(), fruit);
				} else {
					ItemHandlerHelper.giveItemToPlayer(playerIn, fruit);
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		if (entity instanceof FlyingAnimal) {
			return BlockPathTypes.OPEN;
		}
		return null;
	}

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

	@Override
	public <T extends BlockEntity> GameEventListener getListener(Level level, T blockEntity) {
		if (level.isClientSide)
			return null;
		return blockEntity instanceof FruitTreeBlockEntity ? (FruitTreeBlockEntity) blockEntity : null;
	}
}
