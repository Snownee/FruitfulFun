package snownee.fruits.cherry.client.particle;

import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class PetalParticle extends TextureSheetParticle {

	private float angleStepX;
	private float angleStepZ;
	private Vector3f vForce;
	private float rollX;
	private float oRollX;
	private boolean inWater;

	private PetalParticle(ClientLevel world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
		lifetime = 100;
		age = random.nextInt(20);
		quadSize = 0.75f + random.nextFloat() * 0.25f;
		alpha = 0.7f + random.nextFloat() * 0.3f;

		float baseMotionX = 0.05f + random.nextFloat() * 0.02f;
		float baseMotionY = -0.1f;
		float baseMotionZ = 0.075f + random.nextFloat() * 0.02f;

		Vector3f motion = new Vector3f(baseMotionX, baseMotionY, baseMotionZ);
		motion.normalize();

		//		motion.mul(0.02f);
		//		lifetime = 500;

		vForce = new Vector3f(baseMotionZ, 0, -baseMotionX);
		//		Quaternionf rot = vForce.rotationDegrees(-90);
		//		vForce = motion.copy();
		//		vForce.transform(rot);
		motion.mul(0.075f + random.nextFloat() * 0.05f);

		xd = motion.x();
		yd = motion.y();
		zd = motion.z();

		angleStepX = 0.1f + random.nextFloat() * 0.1f;
		angleStepZ = 0.1f + random.nextFloat() * 0.1f;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public float getQuadSize(float pTicks) {
		if (lifetime - age < 10) {
			float f = Mth.sin((float) ((lifetime - age - pTicks) / 20 * Math.PI));
			return Mth.clamp(f, 0, 1);
		}
		return 1;
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		oRoll = roll;
		oRollX = rollX;

		if (age++ >= lifetime) {
			remove();
			return;
		}

		boolean lastOnGround = onGround;
		if (!onGround && !inWater) {
			float mul = age % 10 < 5 ? 0.03f : -0.03f;
			xd += vForce.x() * mul;
			yd += vForce.y() * mul;
			zd += vForce.z() * mul;
			roll += angleStepZ;
			rollX += angleStepX;
		}

		move(xd, yd, zd);

		if (onGround) {
			if (onGround && !lastOnGround) {
				age = lifetime - 20;
			}
			xd *= 0.5;
			zd *= 0.5;
		} else if (inWater) {
			xd *= 0.66;
			zd *= 0.66;
		} else {
			if (lastOnGround) {
				age = lifetime - 60;
			}
			xd *= 1.001;
			yd *= 0.998;
			zd *= 1.001;
		}
	}

	@Override
	public void move(double pX, double pY, double pZ) {
		//        if (!this.collidedY) {
		int ix = (int) x;
		int iy = (int) y;
		int iz = (int) z;
		BlockPos pos = new BlockPos(ix, iy, iz);
		LevelChunk chunk = level.getChunk(ix >> 4, iz >> 4);
		FluidState fluidState = chunk.getFluidState(ix, iy, iz);
		float height = fluidState.getHeight(level, pos);
		if (fluidState.is(FluidTags.WATER)) {
			if (!inWater && y <= height + iy) {
				inWater = true;
				age -= 60;
				yd = 0;
				yo = y = height + iy;
			}
			if (inWater) {
				Vec3 flow = fluidState.getFlow(level, pos);
				xd += flow.x * 0.02;
				zd += flow.z * 0.02;
				pX = xd;
				pZ = zd;
			}
		} else if (inWater) {
			//            inWater = false;
			//            yd = -0.1f;
		}

		double lastX = pX; // [d0]
		double lastY = pY; // [d1]
		double lastZ = pZ; // < Create variable 'lastZ' and assign it to 'z'.

		if (pX != 0.0D || pY != 0.0D || pZ != 0.0D) {
			Vec3 moveVec = Entity.collideBoundingBox((Entity) null, new Vec3(pX, pY, pZ), getBoundingBox(), level, List.of());
			pX = moveVec.x;
			pY = moveVec.y;
			pZ = moveVec.z;
		}

		if (pX != 0.0D || pY != 0.0D || pZ != 0.0D) {
			if (inWater) {
				double targetY = height + iy;
				pY = Mth.clamp(targetY - y, -0.005, 0.02);
			}

			setBoundingBox(getBoundingBox().move(pX, pY, pZ));
			setLocationFromBoundingbox();
		}

		if (Math.abs(lastY) >= 1.0E-5F && Math.abs(pY) < 1.0E-5F) {
			stoppedByCollision = true;
		}

		if (!inWater) {
			onGround = lastY != pY && lastY < 0.0D;
		}
		if (lastX != pX) {
			xd = 0.0D;
		}

		if (lastZ != pZ) {
			zd = 0.0D;
		}

	}

	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
		Vec3 vec3d = camera.getPosition();
		float f = (float) (Mth.lerp(partialTicks, xo, x) - vec3d.x());
		float f1 = (float) (Mth.lerp(partialTicks, yo, y) - vec3d.y());
		float f2 = (float) (Mth.lerp(partialTicks, zo, z) - vec3d.z());
		Vector3f sub = new Vector3f(f, f1, f2);
		Quaternionf quaternion;

		float rollZ = Mth.lerp(partialTicks, oRoll, roll);
		if (onGround || inWater) {
			quaternion = Axis.XP.rotationDegrees(90);
			if (inWater) {
				f1 += 0.16f + rollZ % 0.01f;
			} else {
				f1 += 0.005f + rollZ % 0.01f;
			}
		} else {
			quaternion = new Quaternionf();
			float rollX = Mth.lerp(partialTicks, oRollX, this.rollX);
			quaternion.rotateX(rollX);
			quaternion.rotateY(rollX * 0.2F);
		}
		quaternion.rotateZ(rollZ);
		var quadNormal = new Vector3f(0.0F, 0.0F, 1.0F);
		quadNormal.rotate(quaternion);
		Vector3f[] vertex = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F) };
		float uv[] = new float[] { getU0(), getV0(), getU1(), getV1() };
		if (sub.dot(quadNormal) < 0) {
			for (int i = 0; i < 4; ++i) {
				vertex[i].mul(-1, 1, 1);
			}
			uv[0] = getU1();
			uv[2] = getU0();
		}

		float f4 = getQuadSize(partialTicks);
		float alpha = f4 * this.alpha;
		f4 *= quadSize * 0.15f;

		for (int i = 0; i < 4; ++i) {
			Vector3f vector3f = vertex[i];
			vector3f.rotate(quaternion);
			vector3f.mul(f4);
			vector3f.add(f, f1, f2);
		}

		int j = getLightColor(partialTicks);
		buffer.vertex(vertex[0].x(), vertex[0].y(), vertex[0].z()).uv(uv[2], uv[3]).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
		buffer.vertex(vertex[1].x(), vertex[1].y(), vertex[1].z()).uv(uv[2], uv[1]).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
		buffer.vertex(vertex[2].x(), vertex[2].y(), vertex[2].z()).uv(uv[0], uv[1]).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
		buffer.vertex(vertex[3].x(), vertex[3].y(), vertex[3].z()).uv(uv[0], uv[3]).color(rCol, gCol, bCol, alpha).uv2(j).endVertex();
	}

	//	public enum RenderType implements ParticleRenderType {
	//		INSTANCE;
	//
	//		@SuppressWarnings("deprecation")
	//		@Override
	//		public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
	//			RenderSystem.disableCull();
	//			RenderSystem.depthMask(true);
	//			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
	//			RenderSystem.enableBlend();
	//			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	//			pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
	//		}
	//
	//		@Override
	//		public void end(Tesselator pTesselator) {
	//			pTesselator.end();
	//			RenderSystem.enableCull();
	//		}
	//
	//		@Override
	//		public String toString() {
	//			return "PARTICLE_SHEET_TRANSLUCENT_NO_CULL";
	//		}
	//	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite) {
			spriteSet = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			PetalParticle noteparticle = new PetalParticle(worldIn, x, y, z);
			noteparticle.pickSprite(spriteSet);
			return noteparticle;
		}

	}
}
