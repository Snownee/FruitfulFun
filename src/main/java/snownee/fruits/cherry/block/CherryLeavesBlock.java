package snownee.fruits.cherry.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import snownee.fruits.FruitType;
import snownee.fruits.block.FruitLeavesBlock;

public class CherryLeavesBlock extends FruitLeavesBlock {

	protected final ParticleOptions particleType;

	public CherryLeavesBlock(Supplier<FruitType> type, Properties properties, ParticleOptions particleType) {
		super(type, properties);
		this.particleType = particleType;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		int i = rand.nextInt(15);
		boolean raining = worldIn.isRainingAt(pos.above());
		if (raining && i == 1) {
			BlockPos blockpos = pos.below();
			BlockState blockstate = worldIn.getBlockState(blockpos);
			if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP)) {
				double d0 = pos.getX() + rand.nextFloat();
				double d1 = pos.getY() - 0.05D;
				double d2 = pos.getZ() + rand.nextFloat();
				worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		} else if (i == 2 || i == 3 && raining) {
			double d0 = pos.getX() + rand.nextFloat();
			double d1 = pos.getY() + rand.nextFloat();
			double d2 = pos.getZ() + rand.nextFloat();
			worldIn.addParticle(particleType, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(worldIn, pos, state, player);
		if (worldIn.isClientSide) {
			int times = 5 + worldIn.random.nextInt(6);
			for (int i = 0; i < times; ++i) {
				double x = worldIn.random.nextGaussian() * 0.3D;
				double y = worldIn.random.nextGaussian() * 0.3D;
				double z = worldIn.random.nextGaussian() * 0.3D;
				x += pos.getX() + .5;
				y += pos.getY() + .5;
				z += pos.getZ() + .5;
				worldIn.addParticle(particleType, x, y, z, 0, 0, 0);
			}
		}
	}
}
