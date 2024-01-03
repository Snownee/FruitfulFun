package snownee.fruits.cherry.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FFClientConfig;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;

public class CherryLeavesBlock extends FruitLeavesBlock {

	protected final ParticleOptions particleType;

	public CherryLeavesBlock(Supplier<FruitType> type, Properties properties, ParticleOptions particleType) {
		super(type, properties);
		this.particleType = particleType;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return 0;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Disabled) {
			return;
		}
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Vanilla && CherryModule.CHERRY_LEAVES.is(this)) {
			Blocks.CHERRY_LEAVES.animateTick(stateIn, level, pos, rand);
			return;
		}
		int i = rand.nextInt(15);
		if (i > 2) {
			return;
		}
		BlockPos blockpos = pos.below();
		BlockState blockstate = level.getBlockState(blockpos);
		if (blockstate.canOcclude() && blockstate.isFaceSturdy(level, blockpos, Direction.UP)) {
			return;
		}
		boolean raining = level.isRainingAt(pos.above());
		if (raining && i == 0) {
			double d0 = pos.getX() + rand.nextFloat();
			double d1 = pos.getY() - 0.05D;
			double d2 = pos.getZ() + rand.nextFloat();
			level.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		} else if (i == 1 || i == 2 && raining) {
			double d0 = pos.getX() + rand.nextFloat();
			double d1 = pos.getY() + rand.nextFloat();
			double d2 = pos.getZ() + rand.nextFloat();
			level.addParticle(particleType, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState blockState) {
		super.spawnDestroyParticles(level, player, pos, blockState);
		spawnDestroyParticles(level, pos, particleType);
	}

	public static void spawnDestroyParticles(Level level, BlockPos pos, ParticleOptions particleType) {
		if (level.isClientSide && FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Modded) {
			int times = Mth.randomBetweenInclusive(level.random, 6, 12);
			for (int i = 0; i < times; ++i) {
				double x = level.random.nextGaussian() * 0.3D;
				double y = level.random.nextGaussian() * 0.3D;
				double z = level.random.nextGaussian() * 0.3D;
				x += pos.getX() + .5;
				y += pos.getY() + .5;
				z += pos.getZ() + .5;
				level.addParticle(particleType, x, y, z, 0, 0, 0);
			}
		}
	}
}
