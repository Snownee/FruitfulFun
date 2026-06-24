package snownee.fruits.client.particle;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class GhostParticle extends RisingParticle {
	private final SpriteSet sprites;
	protected boolean isGlowing;

	GhostParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
		super(level, x, y, z, xd, yd, zd, sprites.first());
		this.sprites = sprites;
		this.scale(1.5F);
		this.setSpriteFromAge(sprites);
	}

	@Override
	public int getLightCoords(float a) {
		return this.isGlowing ? LightCoordsUtil.withBlock(super.getLightCoords(a), 15) : super.getLightCoords(a);
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return SingleQuadParticle.Layer.TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.sprites);
	}

	public static class EmissiveProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;

		public EmissiveProvider(SpriteSet spriteSet) {
			this.sprite = spriteSet;
		}

		@Override
		public @Nullable Particle createParticle(
				SimpleParticleType options,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xAux,
				double yAux,
				double zAux,
				RandomSource random) {
			GhostParticle particle = new GhostParticle(level, x, y, z, xAux, yAux, zAux, this.sprite);
			particle.setAlpha(0.5f);
			particle.isGlowing = true;
			return particle;
		}
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;

		public Provider(SpriteSet spriteSet) {
			this.sprite = spriteSet;
		}

		@Override
		public @Nullable Particle createParticle(
				SimpleParticleType options,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xAux,
				double yAux,
				double zAux,
				RandomSource random) {
			GhostParticle particle = new GhostParticle(level, x, y, z, xAux, yAux, zAux, this.sprite);
			particle.setAlpha(0.5f);
			return particle;
		}
	}
}
