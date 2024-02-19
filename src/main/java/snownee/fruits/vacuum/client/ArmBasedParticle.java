package snownee.fruits.vacuum.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public abstract class ArmBasedParticle extends TextureSheetParticle {
	public final LivingEntity entity;
	public final HumanoidArm arm;

	protected ArmBasedParticle(ClientLevel clientLevel, double d, double e, double f, LivingEntity entity, HumanoidArm arm) {
		super(clientLevel, d, e, f);
		this.entity = entity;
		this.arm = arm;
	}

	@Override
	public void tick() {
		if (entity.isRemoved() || this.age++ >= this.lifetime) {
			this.remove();
		}
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
		//TODO update position
		//Vec3 handPos =
		super.render(vertexConsumer, camera, f);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
}
