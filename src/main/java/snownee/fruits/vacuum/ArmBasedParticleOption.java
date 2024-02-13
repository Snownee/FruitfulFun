package snownee.fruits.vacuum;

import java.util.function.BiFunction;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public abstract class ArmBasedParticleOption implements ParticleOptions {
	protected final int playerId;
	protected final boolean mainArm;

	protected ArmBasedParticleOption(int playerId, boolean mainArm) {
		this.playerId = playerId;
		this.mainArm = mainArm;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		buf.writeVarInt(this.playerId);
		buf.writeBoolean(this.mainArm);
	}

	@Override
	public String writeToString() {
		return String.format("%s %s %s", BuiltInRegistries.PARTICLE_TYPE.getKey(getType()), this.playerId, this.mainArm);
	}

	public int playerId() {
		return playerId;
	}

	public boolean mainArm() {
		return mainArm;
	}

	public static class Deserializer<T extends ArmBasedParticleOption> implements ParticleOptions.Deserializer<T> {
		private final BiFunction<Integer, Boolean, T> factory;

		public Deserializer(BiFunction<Integer, Boolean, T> factory) {
			this.factory = factory;
		}

		@Override
		public T fromNetwork(ParticleType<T> particleType, FriendlyByteBuf buf) {
			int playerId = buf.readVarInt();
			boolean mainHand = buf.readBoolean();
			return factory.apply(playerId, mainHand);
		}

		@Override
		public T fromCommand(ParticleType<T> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			int playerId = stringReader.readInt();
			stringReader.expect(' ');
			boolean mainArm = stringReader.readBoolean();
			return factory.apply(playerId, mainArm);
		}
	}
}
