package snownee.fruits.vacuum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleType;

public class AirVortexParticleOption extends ArmBasedParticleOption {
	public static final Codec<AirVortexParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("player_id").forGetter(AirVortexParticleOption::playerId),
			Codec.BOOL.fieldOf("main_arm").forGetter(AirVortexParticleOption::mainArm)
	).apply(instance, AirVortexParticleOption::new));
	public static final ArmBasedParticleOption.Deserializer<AirVortexParticleOption> DESERIALIZER = new ArmBasedParticleOption.Deserializer<>(AirVortexParticleOption::new);

	public AirVortexParticleOption(int playerId, boolean mainArm) {
		super(playerId, mainArm);
	}

	@Override
	public ParticleType<?> getType() {
		return VacModule.AIR_VORTEX.get();
	}
}
