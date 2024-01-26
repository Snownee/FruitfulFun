package snownee.fruits.food;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.block.IKiwiBlock;
import snownee.kiwi.util.VoxelUtil;

public class FoodBlock extends HorizontalDirectionalBlock implements IKiwiBlock {

	public static final Supplier<BlockPattern> RITUAL = Suppliers.memoize(() -> BlockPatternBuilder.start()
			.aisle(
					" C C ",
					"C~~~C",
					" ~ ~ ",
					"C~~~C",
					" C C ")
			.aisle(
					"C~~~C",
					"~~~~~",
					"~~~~~",
					"~~~~~",
					"C~~~C")
			.where('C', BlockInWorld.hasState(CommonProxy::isLitCandle))
			.where('~', BlockInWorld.hasState(BlockStateBase::isAir))
			.build());
	public static final UUID DUMMY_UUID = UUID.fromString("46fee2cc-dbea-4a2f-9768-afb958387795");
	private final VoxelShape[] shapes = new VoxelShape[4];
	public boolean lockShapeRotation = true;

	public FoodBlock(VoxelShape northShape) {
		super(Block.Properties.copy(Blocks.CAKE));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
		shapes[Direction.NORTH.get2DDataValue()] = northShape;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockItem createItem(Item.Properties builder) {
		return new FoodBlockItem(this, builder);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {

		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
		if (!level.isClientSide && !(this instanceof FeastBlock)) {
			level.removeBlock(pos, false);
			ItemStack stack = new ItemStack(this);
			if (!player.addItem(stack)) {
				player.drop(stack, false);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return canSupportRigidBlock(level, pos.below());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		int index = lockShapeRotation ? Direction.NORTH.get2DDataValue() : state.getValue(FACING).get2DDataValue();
		if (shapes[index] == null) {
			shapes[index] = VoxelUtil.rotateHorizontal(shapes[Direction.NORTH.get2DDataValue()], state.getValue(FACING));
		}
		return shapes[index];
	}

	@Override
	public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource random) {
		if (FoodModule.HONEY_POMELO_TEA.is(this) && random.nextInt(5) == 0) {
			double x = pos.getX() + 0.5 + random.nextDouble() * (random.nextBoolean() ? 0.2 : -0.2);
			double y = pos.getY() + 0.5;
			double z = pos.getZ() + 0.5 + random.nextDouble() * (random.nextBoolean() ? 0.2 : -0.2);
			level.addParticle(FoodModule.SMOKE.get(), true, x, y, z, 0.0, 0.003, 0.0);
		}
	}

	@Override
	public void onPlace(BlockState blockState, Level level, BlockPos pos, BlockState blockState2, boolean bl) {
		if (level.isClientSide || !FFCommonConfig.chorusFruitPieRitual
				|| !FoodModule.CHORUS_FRUIT_PIE.is(blockState)
				|| blockState2.is(blockState.getBlock())) {
			return;
		}
		List<BlockPos> headPosList = tryStartRitual(level, pos);
		if (headPosList.isEmpty()) {
			return;
		}
		//TODO ritual
		level.removeBlock(pos, false);
		AreaEffectCloud flame = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		flame.setRadius(0.5f * (headPosList.size() + 1));
		flame.setDuration(300);
		flame.setParticle(ParticleTypes.DRAGON_BREATH);
		flame.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
		flame.ownerUUID = DUMMY_UUID;
		level.addFreshEntity(flame);
		level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
	}

	public static List<BlockPos> tryStartRitual(Level level, BlockPos pos) {
		BlockPattern pattern = RITUAL.get();
		BlockPos.MutableBlockPos mutable = pos.mutable()
				.move(Direction.NORTH, 2)
				.move(Direction.EAST, 2);
		BlockPattern.BlockPatternMatch match = pattern.matches(level, mutable, Direction.UP, Direction.NORTH);
		if (match == null) {
			return List.of();
		}
		List<BlockPos> headPosList = Lists.newArrayListWithExpectedSize(4);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			mutable.set(pos).move(Direction.UP).move(direction, 3);
			BlockState head = level.getBlockState(mutable);
			if (!head.is(Blocks.DRAGON_HEAD) && !head.is(Blocks.DRAGON_WALL_HEAD)) {
				continue;
			}
			Direction facing = getSkullFacing(head);
			if (facing != null) {
				headPosList.add(mutable.immutable());
			}
		}
		return headPosList;
	}

	@Nullable
	public static Direction getSkullFacing(BlockState state) {
		if (state.getBlock() instanceof WallSkullBlock) {
			return state.getValue(WallSkullBlock.FACING);
		}
		if (state.getBlock() instanceof SkullBlock) {
			return switch (state.getValue(SkullBlock.ROTATION)) {
				case 0 -> Direction.NORTH;
				case 4 -> Direction.EAST;
				case 8 -> Direction.SOUTH;
				case 12 -> Direction.WEST;
				default -> null;
			};
		}
		return null;
	}
}
