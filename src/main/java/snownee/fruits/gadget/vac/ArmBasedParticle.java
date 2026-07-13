package snownee.fruits.gadget.vac;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public abstract class ArmBasedParticle extends SingleQuadParticle {
	public LivingEntity entity;
	public HumanoidArm arm;

	protected ArmBasedParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
		super(level, x, y, z, sprite);
	}

	protected ArmBasedParticle(
			ClientLevel level,
			double x,
			double y,
			double z,
			double xa,
			double ya,
			double za,
			TextureAtlasSprite sprite) {
		super(level, x, y, z, xa, ya, za, sprite);
	}

	@Override
	public void tick() {
		if (entity.isRemoved() || this.age++ >= this.lifetime) {
			this.remove();
		}
	}
}
