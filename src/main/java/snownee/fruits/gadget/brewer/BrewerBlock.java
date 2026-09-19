package snownee.fruits.gadget.brewer;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BrewerBlock extends BrewingStandBlock {
	public static final MapCodec<BrewingStandBlock> CODEC = simpleCodec(BrewerBlock::new);

	public BrewerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public MapCodec<BrewingStandBlock> codec() {
		return CODEC;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double x = pos.getX() + 0.4 + random.nextFloat() * 0.2;
		double y = pos.getY() + 0.7 + random.nextFloat() * 0.3;
		double z = pos.getZ() + 0.4 + random.nextFloat() * 0.2;
		level.addParticle(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 0.75F), x, y, z, 0, 0.02, 0);
	}
}
