package snownee.fruits.gadget.vac;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class AirVortexParticle extends ArmBasedParticle {
	public AirVortexParticle(
			ClientLevel level,
			double x,
			double y,
			double z,
			TextureAtlasSprite sprite,
			LivingEntity entity,
			HumanoidArm arm) {
		super(level, x, y, z, sprite);
		this.entity = entity;
		this.arm = arm;
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
			Entity entity = level.getEntity(options.playerId());
			if (!(entity instanceof LivingEntity living)) {
				return null;
			}
			HumanoidArm arm = options.mainArm() ? living.getMainArm() : living.getMainArm().getOpposite();
			return new AirVortexParticle(level, x, y, z, sprites.get(random), living, arm);
		}
	}
}
