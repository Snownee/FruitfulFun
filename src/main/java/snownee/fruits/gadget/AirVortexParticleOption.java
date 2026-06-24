package snownee.fruits.gadget;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class AirVortexParticleOption extends ArmBasedParticleOption {
	public static final MapCodec<AirVortexParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.fieldOf("player_id").forGetter(AirVortexParticleOption::playerId),
			Codec.BOOL.fieldOf("main_arm").forGetter(AirVortexParticleOption::mainArm)
	).apply(instance, AirVortexParticleOption::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AirVortexParticleOption> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			ArmBasedParticleOption::playerId,
			ByteBufCodecs.BOOL,
			ArmBasedParticleOption::mainArm,
			AirVortexParticleOption::new);

	public AirVortexParticleOption(int playerId, boolean mainArm) {
		super(playerId, mainArm);
	}

	@Override
	public ParticleType<?> getType() {
		return GadgetModule.AIR_VORTEX.get();
	}
}
