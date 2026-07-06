package snownee.fruits.pomegranate.block;

import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.entity.FruitTreeBlockEntity;

public class HangingFruitLeavesBlock extends FruitLeavesBlock {
	public static final MapCodec<HangingFruitLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			FFRegistries.FRUIT_TYPE.holderByNameCodec().fieldOf("fruit").forGetter(e -> e.type),
			ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter(e -> e.leafParticleChance),
			ParticleTypes.CODEC.optionalFieldOf("leaf_particle").forGetter(e -> Optional.ofNullable(e.leafParticle)),
			Codec.INT.optionalFieldOf("constant_tint_color", -1).forGetter(e -> e.constantTintColor),
			propertiesCodec()
	).apply(i, HangingFruitLeavesBlock::new));

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public HangingFruitLeavesBlock(
			Holder<FruitType> type,
			float leafParticleChance,
			Optional<ParticleOptions> leafParticle,
			int constantTintColor,
			Properties properties) {
		super(type, leafParticleChance, leafParticle, constantTintColor, properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		return InteractionResult.PASS;
	}

	@Override
	public boolean hasFruit(BlockState state, Level level, BlockPos pos) {
		return state.getValue(AGE) == FruitLeavesBlock.FRUITING &&
				level.getBlockState(pos.below()).getBlock().asItem() == type.value().fruit.get();
	}

	@Override
	public @Nullable ItemEntity doDropFruit(ServerLevel level, BlockPos pos, BlockState state) {
		BlockPos below = pos.below();
		level.removeBlock(below, false);
		return createItemEntity(level, below, type.value().fruit.get().getDefaultInstance());
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}

	@Override
	public boolean canGrowWithContext(BlockState blockState, LevelReader level, BlockPos pos) {
		if (!super.canGrowWithContext(blockState, level, pos)) {
			return false;
		}
		return blockState.getValue(AGE) != BLOOMING || level.getBlockState(pos.below()).canBeReplaced();
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		int age = state.getValue(AGE);
		if (age == FruitLeavesBlock.FRUITING) {
			gotoDeadOrYoung(world, pos, state, null);
			return;
		}
		age++;
		world.setBlockAndUpdate(pos, state.setValue(AGE, age));
		if (age == FruitLeavesBlock.FRUITING) {
			FruitTreeBlockEntity core = findCore(world, pos);
			if (core != null) {
				core.consumeLifespan(1);
			}
			BlockPos below = pos.below();
			if (world.getBlockState(below).canBeReplaced()) {
				Block block = Block.byItem(type.value().fruit.get());
				world.setBlockAndUpdate(below, block.defaultBlockState());
			}
		}
	}

	@Override
	public MapCodec<? extends HangingFruitLeavesBlock> codec() {
		return CODEC;
	}
}
