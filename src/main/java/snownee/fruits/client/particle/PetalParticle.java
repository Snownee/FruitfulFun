package snownee.fruits.client.particle;

import java.util.Iterator;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PetalParticle extends TextureSheetParticle {

	private final float rollStepX;
	private final float rollStepZ;
	private float rollX;
	private float oRollX;
	private boolean inWater;
	private final float particleRandom;
	private int sinceNotInWater;

	private PetalParticle(ClientLevel world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
		lifetime = 300;
		particleRandom = this.random.nextFloat();
		age = random.nextInt(20);
		quadSize = 0.75f + random.nextFloat() * 0.25f;
		alpha = 0.7f + random.nextFloat() * 0.3f;
		gravity = 7.5E-4f;

		float baseMotionX = 0.5f + random.nextFloat() * 0.2f;
		float baseMotionY = -0.75f;
		float baseMotionZ = 0.5f + random.nextFloat() * 0.2f;

		if (random.nextFloat() < 0.2f) {
			float f = random.nextFloat() * Mth.TWO_PI;
			baseMotionX += Mth.sin(f) * 0.3f;
			baseMotionZ += Mth.cos(f) * 0.3f;
		}

		Vector3f motion = new Vector3f(baseMotionX, baseMotionY, baseMotionZ);
		motion.normalize().mul(0.03f + random.nextFloat() * 0.005f);

		xd = motion.x();
		yd = motion.y();
		zd = motion.z();

		if (random.nextFloat() < 0.2f) {
			yd -= random.nextFloat() * 0.02f;
		}

		rollStepX = 0.1f + random.nextFloat() * 0.1f * (random.nextBoolean() ? 1 : -1);
		rollStepZ = 0.1f + random.nextFloat() * 0.1f * (random.nextBoolean() ? 1 : -1);

		rCol = gCol = bCol = random.nextFloat() * 0.3f + 0.7f;
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
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
			double time = age * (0.1 + particleRandom * 0.03);
			double e = Math.sin(time) * 0.0025;
			//			rCol = (float) (Math.sin(time) + 1) / 2;
			yd -= e;
			if (yd > 0) {
				yd = 0;
				xd += Math.sin(particleRandom * Mth.TWO_PI) * 0.0005;
				zd += Math.cos(particleRandom * Mth.TWO_PI) * 0.0005;
			}
			time = (age + 3) * (0.1 + particleRandom * 0.03);
			double d = Math.sin(time) * 0.0025;
			xd += d;
			zd += d;
			roll += rollStepZ;
			rollX += rollStepX;
		}

		if (sinceNotInWater > 0) {
			sinceNotInWater++;
		}
		move(xd, yd, zd);

		if (onGround) {
			if (!lastOnGround) {
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
			yd *= 0.998;
			if (Math.abs(xd) > 0.3) {
				xd *= 0.85;
			}
			if (Math.abs(yd) > 0.3) {
				yd *= 0.85;
			}
			if (Math.abs(zd) > 0.3) {
				zd *= 0.85;
			}
		}
	}

	@Override
	public void move(double pX, double pY, double pZ) {
		BlockPos pos = BlockPos.containing(x + pX, y + pY, z + pZ);
		FluidState fluidState = level.getFluidState(pos);
		float waterHeight = fluidState.getHeight(level, pos) + pos.getY();
		boolean oInWater = inWater;
		inWater = (oInWater || y <= waterHeight) && fluidState.is(FluidTags.WATER);
		if (inWater) {
			sinceNotInWater = 0;
			Vec3 flow = fluidState.getFlow(level, pos);
			pX = flow.x * 0.05;
			pY = flow.y * 0.05 - 0.05;
			pZ = flow.z * 0.05;
		} else if (oInWater) {
			sinceNotInWater = 1;
		}

		double lastX = pX; // [d0]
		double lastY = pY; // [d1]
		double lastZ = pZ; // < Create variable 'lastZ' and assign it to 'z'.

		if (pX != 0.0D || pY != 0.0D || pZ != 0.0D) {
			Vec3 moveVec = collideBoundingBox(new Vec3(pX, pY, pZ), getBoundingBox(), level);
			pX = moveVec.x;
			pY = moveVec.y;
			pZ = moveVec.z;
		}

		if (pX != 0.0D || pY != 0.0D || pZ != 0.0D) {
			if (inWater) {
				pY = accurateWaterHeight(pos, waterHeight) - y;
			}

			setBoundingBox(getBoundingBox().move(pX, pY, pZ));
			setLocationFromBoundingbox();
		}

		if (Math.abs(lastY) >= 1.0E-5F && Math.abs(pY) < 1.0E-5F) {
			stoppedByCollision = true;
		}

		if (!inWater) {
			onGround = lastY != pY && lastY < 0.0D;
			if (lastX != pX) {
				xd *= -0.5D;
			}
			if (lastZ != pZ) {
				zd *= -0.5D;
			}
		}
	}

	private float accurateWaterHeight(BlockPos pos, float waterHeight) {
		if (level.getFluidState(pos).getValue(FlowingFluid.FALLING)) {
			waterHeight = pos.getY();
		}
		float highest = waterHeight;
		Direction highestSide = null;
		for (Direction side : Direction.Plane.HORIZONTAL) {
			BlockPos sidePos = pos.relative(side);
			float sideHeight = getWaterHeight(sidePos, waterHeight);
			if (sideHeight > highest) {
				highest = sideHeight;
				highestSide = side;
			}
		}
		if (highestSide != null) {
			float ratio = highestSide.getAxis() == Direction.Axis.X ? (float) (x - pos.getX()) : (float) (z - pos.getZ());
			if (highestSide.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
				ratio = 1 - ratio;
			}
			return Mth.lerp(ratio, waterHeight, highest);
		}
		return waterHeight;
	}

	private float getWaterHeight(BlockPos pos, float waterHeight) {
		FluidState fluidState = level.getFluidState(pos);
		if (fluidState.is(FluidTags.WATER)) {
			return fluidState.getHeight(level, pos) + pos.getY();
		}
		return waterHeight;
	}

	// Modified from Entity#collideBoundingBox. Optimize and ignore the collision of leaves
	public static Vec3 collideBoundingBox(Vec3 vec3, AABB aABB, Level level) {
		Iterator<VoxelShape> iterator = new BlockCollisions<>(level, null, aABB.expandTowards(vec3), true, (mutableBlockPos, voxelShape) -> voxelShape);
		return Entity.collideWithShapes(vec3, aABB, ImmutableList.copyOf(iterator));
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
		if (onGround || inWater || sinceNotInWater > 0 && sinceNotInWater < 5) {
			quaternion = Axis.XP.rotationDegrees(90);
			f1 += 0.005f + rollZ % 0.01f;
		} else {
			quaternion = new Quaternionf();
			float rollX = Mth.lerp(partialTicks, oRollX, this.rollX);
			quaternion.rotateX(rollX);
			quaternion.rotateY(rollX * 0.2F);
		}
		quaternion.rotateZ(rollZ);
		var quadNormal = new Vector3f(0.0F, 0.0F, 1.0F);
		quadNormal.rotate(quaternion);
		Vector3f[] vertex = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float[] uv = new float[]{getU0(), getV0(), getU1(), getV1()};
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

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite) {
			spriteSet = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			PetalParticle particle = new PetalParticle(worldIn, x, y, z);
			particle.pickSprite(spriteSet);
			particle.xd += xSpeed;
			particle.yd += ySpeed;
			particle.zd += zSpeed;
			if (Math.abs(xSpeed) > 0.5 || Math.abs(ySpeed) > 0.5 || Math.abs(zSpeed) > 0.5) {
				particle.lifetime = 60;
			}
			return particle;
		}
	}
}
