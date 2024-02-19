package snownee.fruits.vacuum.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.vacuum.AirVortexParticleOption;

public class AirVortexParticle extends ArmBasedParticle {
	public AirVortexParticle(ClientLevel clientLevel, double d, double e, double f, LivingEntity entity, HumanoidArm arm) {
		super(clientLevel, d, e, f, entity, arm);
	}

	public static class Factory implements ParticleProvider<AirVortexParticleOption> {
		private final SpriteSet sprites;

		public Factory(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public AirVortexParticle createParticle(
				AirVortexParticleOption option,
				ClientLevel clientLevel,
				double d,
				double e,
				double f,
				double g,
				double h,
				double i) {
			Entity entity = clientLevel.getEntity(option.playerId());
			if (!(entity instanceof LivingEntity living)) {
				return null;
			}
			HumanoidArm arm = option.mainArm() ? living.getMainArm() : living.getMainArm().getOpposite();
			AirVortexParticle particle = new AirVortexParticle(clientLevel, d, e, f, living, arm);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}
}
