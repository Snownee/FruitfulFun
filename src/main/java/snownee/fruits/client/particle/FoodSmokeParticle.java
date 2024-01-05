package snownee.fruits.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class FoodSmokeParticle extends TextureSheetParticle {
	FoodSmokeParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
		super(clientLevel, d, e, f);
		this.setSize(0.25f, 0.25f);
		this.lifetime = this.random.nextInt(50) + 50;
		this.gravity = 3.0E-6f;
		this.xd = g;
		this.yd = h + (double) (this.random.nextFloat() / 1000.0f);
		this.zd = i;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime || this.alpha <= 0.0f) {
			this.remove();
			return;
		}
		this.xd += this.random.nextFloat() / 5000.0f * (float) (this.random.nextBoolean() ? 1 : -1);
		this.zd += this.random.nextFloat() / 5000.0f * (float) (this.random.nextBoolean() ? 1 : -1);
		this.yd -= this.gravity;
		this.move(this.xd, this.yd, this.zd);
		if (this.age >= this.lifetime - 50 && this.alpha > 0.01f) {
			this.alpha -= 0.01f;
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Factory(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
			FoodSmokeParticle particle = new FoodSmokeParticle(clientLevel, d, e, f, g, h, i);
			particle.setAlpha(0.7f);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}
}
