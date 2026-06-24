package snownee.fruits.gadget.client;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import snownee.fruits.gadget.AirVortexParticleOption;

public class AirVortexParticle extends ArmBasedParticle {
	public AirVortexParticle(ClientLevel clientLevel, double d, double e, double f, LivingEntity entity, HumanoidArm arm) {
		super(clientLevel, d, e, f, entity, arm);
	}

	@Override
	protected Layer getLayer() {
		return null;
	}

	public static class Factory implements ParticleProvider<AirVortexParticleOption> {
		private final SpriteSet sprites;

		public Factory(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public @Nullable Particle createParticle(
				AirVortexParticleOption options,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xAux,
				double yAux,
				double zAux,
				RandomSource random) {
			Entity entity = clientLevel.getEntity(options.playerId());
			if (!(entity instanceof LivingEntity living)) {
				return null;
			}
			HumanoidArm arm = options.mainArm() ? living.getMainArm() : living.getMainArm().getOpposite();
			AirVortexParticle particle = new AirVortexParticle(clientLevel, d, e, f, living, arm);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}
}
