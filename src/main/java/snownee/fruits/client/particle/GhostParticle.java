package snownee.fruits.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class GhostParticle extends RisingParticle {
	private final SpriteSet sprites;
	protected boolean isGlowing;

	GhostParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet) {
		super(clientLevel, d, e, f, g, h, i);
		this.sprites = spriteSet;
		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public int getLightColor(float partialTick) {
		int light = super.getLightColor(partialTick);
		if (this.isGlowing) {
			int blockLight = (light >> 4) & 15;
			light = (light & 0xFFFF00) | Mth.clamp(blockLight + 4, 6, 15) << 4;
		}
		return light;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
		public Particle createParticle(
				SimpleParticleType type,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xSpeed,
				double ySpeed,
				double zSpeed) {
			GhostParticle particle = new GhostParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
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
		public Particle createParticle(
				SimpleParticleType type,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xSpeed,
				double ySpeed,
				double zSpeed) {
			GhostParticle particle = new GhostParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
			particle.setAlpha(0.5f);
			return particle;
		}
	}
}
