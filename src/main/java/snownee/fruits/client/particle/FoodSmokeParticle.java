package snownee.fruits.client.particle;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class FoodSmokeParticle extends SingleQuadParticle {
	FoodSmokeParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, TextureAtlasSprite sprite) {
		super(level, x, y, z, sprite);
		this.setSize(0.25f, 0.25f);
		this.lifetime = this.random.nextInt(50) + 50;
		this.gravity = 3.0E-6f;
		this.xd = xa;
		this.yd = ya + (double) (this.random.nextFloat() / 1000.0f);
		this.zd = za;
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
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private SpriteSet sprites;

		public Factory(SpriteSet spriteSet) {
			this.sprites = spriteSet;
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
			FoodSmokeParticle particle = new FoodSmokeParticle(level, x, y, z, xAux, yAux, zAux, sprites.get(random));
			particle.setAlpha(0.7f);
			return particle;
		}
	}
}
