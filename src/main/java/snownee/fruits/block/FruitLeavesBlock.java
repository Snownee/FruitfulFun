package snownee.fruits.block;

import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.fruits.FruitType;
import snownee.fruits.FruitsConfig;
import snownee.fruits.cherry.block.CherryLeavesBlock;
import snownee.fruits.tile.FruitTreeTile;
import snownee.fruits.world.gen.treedecorator.CarpetTreeDecorator;

public class FruitLeavesBlock extends LeavesBlock implements IGrowable {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;

	public final Supplier<FruitType> type;

	public FruitLeavesBlock(Supplier<FruitType> type, Properties properties) {
		super(properties);
		this.type = type;
		setDefaultState(stateContainer.getBaseState().with(DISTANCE, 7).with(PERSISTENT, false).with(AGE, 1));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(DISTANCE, PERSISTENT, AGE);
	}

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return canGrow(state) || state.get(AGE) == 1;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return state.get(AGE) != 3;
	}

	@Override
	public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		if (state.get(AGE) == 3) {
			if (!world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS))
				return;
			switch (FruitsConfig.getDropMode(world)) {
			case INDEPENDENT:
				world.setBlockState(pos, onPassiveGathered(world, pos, state));
				spawnAsEntity(world, pos, new ItemStack(type.get().fruit));
				break;
			case ONE_BY_ONE:
				FruitTreeTile tile = findTile(world, pos, state);
				if (tile != null && tile.canDrop()) {
					ItemStack stack = new ItemStack(type.get().fruit);
					if (!stack.isEmpty() && !world.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
						double d0 = world.rand.nextFloat() * 0.5F + 0.25D;
						double d1 = world.rand.nextFloat() * 0.5F + 0.25D;
						double d2 = world.rand.nextFloat() * 0.5F + 0.25D;
						ItemEntity itementity = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
						itementity.setDefaultPickupDelay();
						if (world.addEntity(itementity))
							tile.setOnlyItem(itementity);
					}
				}
				break;
			default:
				break;
			}
		} else {
			world.setBlockState(pos, state.cycleValue(AGE));
		}
	}

	@Nullable
	public FruitTreeTile findTile(ServerWorld world, BlockPos pos, BlockState state) {
		if (state.hasTileEntity()) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof FruitTreeTile) {
				return (FruitTreeTile) tile;
			}
		} else {
			for (BlockPos pos2 : BlockPos.getAllInBoxMutable(pos.getX() - 2, pos.getY(), pos.getZ() - 2, pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2)) {
				TileEntity tile = world.getTileEntity(pos2);
				if (tile instanceof FruitTreeTile) {
					return (FruitTreeTile) tile;
				}
			}
		}
		return null;
	}

	public BlockState onPassiveGathered(ServerWorld world, BlockPos pos, BlockState state) {
		int death = 30;
		FruitTreeTile tile = findTile(world, pos, state);
		if (tile != null)
			death = tile.updateDeathRate();
		if (death >= 50 || !state.hasTileEntity() && world.rand.nextInt(50) < death) {
			return state.with(AGE, 0);
		} else {
			return state.with(AGE, 1);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(PERSISTENT) && state.get(DISTANCE) == 1;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new FruitTreeTile(type.get());
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (shouldDecay(state)) {
			spawnDrops(state, world, pos);
			world.removeBlock(pos, false);
		} else if (canGrow(state) && world.isAreaLoaded(pos, 1) && world.getLight(pos.up()) >= 9) {

			boolean def = rand.nextInt(100) > (99 - FruitsConfig.growingSpeed);

			if (ForgeHooks.onCropsGrowPre(world, pos, state, def)) {
				grow(world, rand, pos, state);
				ForgeHooks.onCropsGrowPost(world, pos, state);
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		state = updateDistance(state, world, pos);
		if (state.get(PERSISTENT) && state.get(DISTANCE) != 1) {
			state = state.with(PERSISTENT, false);
		}
		world.setBlockState(pos, state, 3);
	}

	@Override
	public boolean ticksRandomly(BlockState state) {
		return shouldDecay(state) || canGrow(state) || state.get(AGE) == 0;
	}

	public boolean shouldDecay(BlockState state) {
		return state.get(DISTANCE) == 7 && !state.get(PERSISTENT);
	}

	public boolean canGrow(BlockState state) {
		return state.get(AGE) > 0 && !state.get(PERSISTENT) || state.get(DISTANCE) == 1;
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (canGrow(state) || state.get(AGE) == 0) {
			return super.updatePostPlacement(state, facing, facingState, worldIn, currentPos, facingPos);
		}
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(PERSISTENT, true);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Entity entity = context.getEntity();
		if (entity instanceof ItemEntity || entity instanceof IFlyingAnimal || entity == null && state.getBlock().getClass() == CherryLeavesBlock.class) {
			return VoxelShapes.empty();
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
		if (!worldIn.isRemote && fallDistance >= 1 && (entityIn instanceof LivingEntity || entityIn instanceof FallingBlockEntity)) {
			for (BlockPos pos2 : BlockPos.getAllInBoxMutable(pos.getX() - 1, Math.max(0, pos.getY() - 2), pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1)) {
				BlockState state = worldIn.getBlockState(pos2);
				if (state.getBlock() instanceof FruitLeavesBlock) {
					if (state.get(AGE) == 3) {
						((FruitLeavesBlock) state.getBlock()).grow((ServerWorld) worldIn, worldIn.rand, pos2, state);
					}
					if (state.getBlock() instanceof CherryLeavesBlock) {
						CarpetTreeDecorator.placeCarpet(worldIn, pos2, ((CherryLeavesBlock) state.getBlock()).getCarpet().getDefaultState(), 3);
					}
				}
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult ray) {
		if (state.get(AGE) == 3 && worldIn.setBlockState(pos, state.with(AGE, 1))) {
			if (!worldIn.isRemote) {
				ItemStack fruit = new ItemStack(type.get().fruit);
				if (playerIn instanceof FakePlayer) {
					double d0 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
					double d1 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
					double d2 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
					ItemEntity itementity = new ItemEntity(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, fruit);
					itementity.setDefaultPickupDelay();
					worldIn.addEntity(itementity);
				} else {
					ItemHandlerHelper.giveItemToPlayer(playerIn, fruit);
				}
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, MobEntity entity) {
		if (entity instanceof IFlyingAnimal) {
			return PathNodeType.OPEN;
		}
		return null;
	}
}
