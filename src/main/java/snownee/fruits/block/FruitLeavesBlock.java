package snownee.fruits.block;

import java.util.function.Consumer;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gamerules.GameRules;
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
import snownee.fruits.FFFruitTypes;
import snownee.fruits.FruitType;
import snownee.fruits.Hooks;
import snownee.fruits.block.entity.FruitTreeBlockEntity;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.loader.Platform;

public class FruitLeavesBlock extends LeavesBlock implements BonemealableBlock, EntityBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	public static final int DEAD = 0;
	public static final int YOUNG = 1;
	public static final int BLOOMING = 2;
	public static final int FRUITING = 3;

	public final Holder<FruitType> type;

	public FruitLeavesBlock(Holder<FruitType> type, float leafParticleChance, Properties properties) {
		super(
				leafParticleChance,
				properties.isValidSpawn(Blocks::ocelotOrParrot)
						.isSuffocating(Blocks::never)
						.isViewBlocking(Blocks::never)
						.isRedstoneConductor(Blocks::never));
		this.type = type;
		registerDefaultState(stateDefinition.any()
				.setValue(DISTANCE, 7)
				.setValue(PERSISTENT, false)
				.setValue(AGE, YOUNG)
				.setValue(WATERLOGGED, false));
	}

	@Nullable
	public ItemEntity dropFruit(ServerLevel level, BlockPos pos, BlockState state, @Nullable FruitTreeBlockEntity core) {
		if (state.getValue(AGE) != FRUITING) {
			return null;
		}
		if (!level.getGameRules().get(GameRules.BLOCK_DROPS)) {
			return null;
		}
		gotoDeadOrYoung(level, pos, state, core);
		level.setBlockAndUpdate(pos, state);
		ItemEntity itemEntity = ((FruitLeavesBlock) state.getBlock()).doDropFruit(level, pos, state);
		if (itemEntity != null && !level.addFreshEntity(itemEntity)) {
			return null;
		}
		return itemEntity;
	}

	@Nullable
	public ItemEntity doDropFruit(ServerLevel level, BlockPos pos, BlockState state) {
		FruitType fruitType = type.value();
		Item item = Items.AIR;
		if (Hooks.hauntedHarvest && FFCommonConfig.rottenAppleChance > 0 && FFFruitTypes.APPLE.is(fruitType) &&
				level.getRandom().nextFloat() < FFCommonConfig.rottenAppleChance) {
			item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("hauntedharvest", "rotten_apple"));
		}
		if (item == Items.AIR) {
			item = fruitType.fruit.get();
		}
		return createItemEntity(level, pos, item.getDefaultInstance());
	}

	public static ItemEntity createItemEntity(ServerLevel level, BlockPos pos, ItemStack stack) {
		float f = EntityType.ITEM.getHeight() / 2.0F;
		double d0 = pos.getX() + 0.5F + Mth.nextDouble(level.getRandom(), -0.25D, 0.25D);
		double d1 = pos.getY() + 0.5F + Mth.nextDouble(level.getRandom(), -0.25D, 0.25D) - f;
		double d2 = pos.getZ() + 0.5F + Mth.nextDouble(level.getRandom(), -0.25D, 0.25D);
		ItemEntity itemEntity = new ItemEntity(level, d0, d1, d2, stack);
		itemEntity.setDefaultPickUpDelay();
		itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().x, 0, itemEntity.getDeltaMovement().z);
		return itemEntity;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DISTANCE, PERSISTENT, AGE, WATERLOGGED);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		if (state.getValue(AGE) == YOUNG) {
			return true;
		}
		if (FFCommonConfig.allogamousTrees && type.value().allogamous && state.getValue(AGE) == BLOOMING) {
			return false;
		}
		return canGrow(state) && state.getValue(AGE) < FRUITING;
	}

	@Override
	public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		world.setBlockAndUpdate(pos, state.cycle(AGE));
		if (state.getValue(AGE) == FruitLeavesBlock.BLOOMING) {
			FruitTreeBlockEntity core = findCore(world, pos);
			if (core != null) {
				core.consumeLifespan(1);
				core.increaseFruitProduced();
			}
		}
	}

	@Nullable
	public FruitTreeBlockEntity findCore(ServerLevel level, BlockPos pos) {
		return level.getPoiManager()
				.findClosest(type.value().poiType::equals, pos, 10, PoiManager.Occupancy.ANY)
				.flatMap(core -> level.getBlockEntity(core, CoreModule.FRUIT_TREE.get()))
				.orElse(null);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (shouldDecay(state)) {
			dropResources(state, world, pos);
			world.removeBlock(pos, false);
		} else if (canGrowWithContext(state, world, pos)) {
			if (hasFruit(state, world, pos)) {
				DropMode mode = FFCommonConfig.getDropMode(world);
				if (mode == DropMode.NoDrop) {
					return;
				}
				FruitTreeBlockEntity core = findCore(world, pos);
				if (mode == DropMode.OneByOne && core != null && !core.canDrop()) {
					return;
				}
				ItemEntity itemEntity = dropFruit(world, pos, state, core);
				if (mode == DropMode.OneByOne && core != null && itemEntity != null) {
					core.setOnlyItem(itemEntity);
				}
				gotoDeadOrYoung(world, pos, state, core);
			} else if (FFCommonConfig.allogamousTrees && type.value().allogamous && state.getValue(AGE) == BLOOMING) {
				boolean def = rand.nextInt(100) > (99 - FFCommonConfig.treeGrowingSpeed);
				if (def) {
					FruitTreeBlockEntity core = findCore(world, pos);
					if (core != null) {
						core.consumeLifespan(1);
					}
					gotoDeadOrYoung(world, pos, state, core);
				}
			} else {
				boolean def = rand.nextInt(100) > (99 - FFCommonConfig.treeGrowingSpeed);
				CommonProxy.maybeGrowCrops(world, pos, state, def, () -> performBonemeal(world, rand, pos, state));
			}
		}
	}

	public void gotoDeadOrYoung(ServerLevel level, BlockPos pos, BlockState blockState, @Nullable FruitTreeBlockEntity core) {
		if (core == null) {
			core = findCore(level, pos);
		}
		boolean die = true;
		if (core != null) {
			die = core.isDead();
			if (die) {
				core.removeLeaves(pos);
			}
		}
		blockState = blockState.setValue(AGE, die ? DEAD : YOUNG);
		if (die && blockState.hasBlockEntity()) {
			blockState = blockState.setValue(PERSISTENT, false);
		}
		level.setBlockAndUpdate(pos, blockState);
	}

	public boolean hasFruit(BlockState state, Level level, BlockPos pos) {
		return state.getValue(AGE) == FRUITING;
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
	protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
//TODO
	}

	@Override
	public MapCodec<? extends LeavesBlock> codec() {
		return null; //TODO
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		if (hasBlockEntity(state)) {
			return true;
		}
		return notPlacedByPlayer(state);
	}

	public boolean notPlacedByPlayer(BlockState state) {
		return shouldDecay(state) || canGrow(state) || state.getValue(AGE) == DEAD;
	}

	public boolean shouldDecay(BlockState state) {
		return state.getValue(DISTANCE) == 7 && !state.getValue(PERSISTENT);
	}

	public boolean canGrowWithContext(BlockState blockState, LevelReader level, BlockPos pos) {
		return canGrow(blockState) && level.getMaxLocalRawBrightness(pos.above()) >= 9;
	}

	public boolean canGrow(BlockState blockState) {
		return blockState.getValue(AGE) != DEAD && (!blockState.getValue(PERSISTENT) || blockState.getValue(DISTANCE) == 1);
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
		if (canGrow(state) || state.getValue(AGE) == DEAD) {
			return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
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
			if (entity instanceof ItemEntity || entity instanceof FlyingAnimal) {
				return Shapes.empty();
			}
		}
		return Shapes.block();
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
		super.fallOn(level, state, pos, entity, fallDistance);
		if (fallDistance >= 1 && level instanceof ServerLevel serverLevel &&
				(entity instanceof LivingEntity || entity instanceof FallingBlockEntity)) {
			Iterable<BlockPos> posList = BlockPos.betweenClosed(pos.offset(-1, -2, -1), pos.offset(1, 0, 1));
			MutableBoolean success = new MutableBoolean(false);
			rangeDrop(serverLevel, posList, null, _ -> success.setTrue());
			if (success.booleanValue()) {
				//FIXME sound
			}
		}
	}

	public static void rangeDrop(
			ServerLevel level,
			Iterable<BlockPos> posList,
			@Nullable FruitTreeBlockEntity core,
			@Nullable Consumer<ItemEntity> consumer) {
		for (BlockPos blockpos : posList) {
			BlockState state = level.getBlockState(blockpos);
			if (state.getBlock() instanceof FruitLeavesBlock leavesBlock && state.getValue(AGE) == FRUITING) {
				ItemEntity itemEntity = leavesBlock.dropFruit(level, blockpos, state, core);
				if (consumer != null && itemEntity != null) {
					consumer.accept(itemEntity);
				}
			}
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (hasFruit(state, level, pos)) {
			giveItemTo(player, hitResult, type.value().fruit.get().getDefaultInstance());
			if (!level.isClientSide()) {
				gotoDeadOrYoung((ServerLevel) level, pos, state, null);
			}
			return InteractionResult.SUCCESS_SERVER;
		}
		return InteractionResult.PASS;
	}

	public static void giveItemTo(Player player, BlockHitResult hit, ItemStack stack) {
		Level level = player.level();
		if (level.isClientSide()) {
			return;
		}
		if (!Platform.isFakePlayer(player) && player.addItem(stack)) {
			level.playSound(
					null,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.ITEM_PICKUP,
					player.getSoundSource(),
					0.2F,
					((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
		} else {
			popResourceFromFace(level, hit.getBlockPos(), hit.getDirection(), stack);
		}
	}

	public boolean hasBlockEntity(BlockState state) {
		return state.getValue(PERSISTENT) && state.getValue(DISTANCE) == 1;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState state) {
		if (hasBlockEntity(state)) {
			return new FruitTreeBlockEntity(pPos, state, type);
		}
		return null;
	}
}
