package snownee.fruits.cherry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FFClientConfig;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.CherryModule;

public class CherryLeavesBlock extends FruitLeavesBlock {

	protected final ParticleOptions particleType;

	public CherryLeavesBlock(Holder<FruitType> type, ParticleOptions particleType, Properties properties) {
		super(type, 0.1F, properties);
		this.particleType = particleType;
	}

//	@Override
//	protected int getLightDampening(BlockState state) {
//		return 0;
//	}
//
//	@Override
//	protected boolean propagatesSkylightDown(BlockState state) {
//		return true;
//	}

	@Override
	public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Disabled) {
			return;
		}
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Vanilla && CherryModule.CHERRY_LEAVES.is(this)) {
			Blocks.CHERRY_LEAVES.animateTick(stateIn, level, pos, rand);
			return;
		}
		int i = rand.nextInt(64 - FFClientConfig.moddedCherryParticleFrequency);
		if (i > 1) {
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
		} else if (i == 1) {
			double d0 = pos.getX() + rand.nextFloat();
			double d1 = pos.getY() + rand.nextFloat();
			double d2 = pos.getZ() + rand.nextFloat();
			level.addParticle(particleType, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState blockState) {
		super.spawnDestroyParticles(level, player, pos, blockState);
		spawnDestroyParticles(level, player, pos, particleType);
	}

	public static void spawnDestroyParticles(Level level, Player player, BlockPos pos, ParticleOptions particleType) {
		if (level.isClientSide() && FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Modded && pos.distToCenterSqr(
				player.position()) < 81) {
			int times = Mth.randomBetweenInclusive(level.getRandom(), 6, 12);
			for (int i = 0; i < times; ++i) {
				double x = level.getRandom().nextGaussian() * 0.3D;
				double y = level.getRandom().nextGaussian() * 0.3D;
				double z = level.getRandom().nextGaussian() * 0.3D;
				x += pos.getX() + .5;
				y += pos.getY() + .5;
				z += pos.getZ() + .5;
				level.addParticle(particleType, x, y, z, 0, 0, 0);
			}
		}
	}
}
