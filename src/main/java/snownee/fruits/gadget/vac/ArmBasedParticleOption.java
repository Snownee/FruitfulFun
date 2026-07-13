package snownee.fruits.gadget.vac;

import net.minecraft.core.particles.ParticleOptions;

public abstract class ArmBasedParticleOption implements ParticleOptions {
	protected final int playerId;
	protected final boolean mainArm;

	protected ArmBasedParticleOption(int playerId, boolean mainArm) {
		this.playerId = playerId;
		this.mainArm = mainArm;
	}

	public int playerId() {
		return playerId;
	}

	public boolean mainArm() {
		return mainArm;
	}
}
