package snownee.fruits.cherry.client.particle;

import java.util.stream.Stream;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PetalParticle extends SpriteTexturedParticle {

	private float angleStepX;
	private float angleStepZ;
	private Vector3f vForce;
	private float particleAngleX;
	private float prevParticleAngleX;
	private boolean inWater;

	private PetalParticle(ClientWorld world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
		maxAge = 100;
		age = rand.nextInt(20);
		particleScale = 0.75f + rand.nextFloat() * 0.25f;
		particleAlpha = 0.7f + rand.nextFloat() * 0.3f;

		float baseMotionX = 0.05f + rand.nextFloat() * 0.02f;
		float baseMotionY = -0.1f;
		float baseMotionZ = 0.075f + rand.nextFloat() * 0.02f;

		Vector3f motion = new Vector3f(baseMotionX, baseMotionY, baseMotionZ);
		motion.normalize();
		vForce = new Vector3f(baseMotionZ, 0, -baseMotionX);
		Quaternion rot = vForce.rotationDegrees(-90);
		vForce = motion.copy();
		vForce.transform(rot);
		motion.mul(0.1f + rand.nextFloat() * 0.05f);

		motionX = motion.getX();
		motionY = motion.getY();
		motionZ = motion.getZ();

		angleStepX = 0.1f + rand.nextFloat() * 0.1f;
		angleStepZ = 0.1f + rand.nextFloat() * 0.1f;
	}

	public enum RenderType implements IParticleRenderType {
		INSTANCE;

		@Override
		public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
			RenderSystem.disableCull();
			RenderSystem.depthMask(true);
			p_217600_2_.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.alphaFunc(516, 0.003921569F);
			p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		}

		@Override
		public void finishRender(Tessellator p_217599_1_) {
			p_217599_1_.draw();
			RenderSystem.enableCull();
		}

		@Override
		public String toString() {
			return "PARTICLE_SHEET_TRANSLUCENT_NO_CULL";
		}
	}

	@Override
	public IParticleRenderType getRenderType() {
		return RenderType.INSTANCE;
	}

	@Override
	public float getScale(float pTicks) {
		if (maxAge - age < 10) {
			float f = MathHelper.sin((float) ((maxAge - age - pTicks) / 20 * Math.PI));
			return MathHelper.clamp(f, 0, 1);
		}
		return 1;
	}

	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevParticleAngle = particleAngle;
		prevParticleAngleX = particleAngleX;

		if (age++ >= maxAge) {
			setExpired();
			return;
		}

		boolean lastOnGround = onGround;
		if (!onGround && !inWater) {
			float mul = age % 10 < 5 ? 0.03f : -0.03f;
			motionX += vForce.getX() * mul;
			motionY += vForce.getY() * mul;
			motionZ += vForce.getZ() * mul;
			particleAngle += angleStepZ;
			particleAngleX += angleStepX;
		}

		move(motionX, motionY, motionZ);

		if (onGround) {
			if (onGround && !lastOnGround) {
				age = maxAge - 20;
			}
			motionX *= 0.5;
			motionZ *= 0.5;
		} else if (inWater) {
			motionX *= 0.66;
			motionZ *= 0.66;
		} else {
			if (lastOnGround) {
				age = maxAge - 60;
			}
			motionX *= 1.001;
			motionY *= 0.998;
			motionZ *= 1.001;
		}
	}

	@Override
	public void move(double x, double y, double z) {
		//        if (!this.collidedY) {
		int ix = (int) posX;
		int iy = (int) posY;
		int iz = (int) posZ;
		Chunk chunk = world.getChunk(ix >> 4, iz >> 4);
		FluidState fluidState = chunk.getFluidState(ix, iy, iz);
		float height = fluidState.getHeight();
		if (fluidState.isTagged(FluidTags.WATER)) {
			if (!inWater && posY <= height + iy) {
				inWater = true;
				age -= 60;
				motionY = 0;
				prevPosY = posY = height + iy;
			}
			if (inWater) {
				Vector3d flow = fluidState.getFlow(world, new BlockPos(ix, iy, iz));
				motionX += flow.x * 0.02;
				motionZ += flow.z * 0.02;
				x = motionX;
				z = motionZ;
			}
		} else if (inWater) {
			//            inWater = false;
			//            motionY = -0.1f;
		}

		double lastX = x; // [d0]
		double lastY = y; // [d1]
		double lastZ = z; // < Create variable 'lastZ' and assign it to 'z'.

		if (x != 0.0D || y != 0.0D || z != 0.0D) {
			Vector3d moveVec = Entity.collideBoundingBoxHeuristically((Entity) null, new Vector3d(x, y, z), getBoundingBox(), world, ISelectionContext.dummy(), new ReuseableStream<>(Stream.empty())); // [vec3d]
			x = moveVec.x;
			y = moveVec.y;
			z = moveVec.z;
		}

		if (x != 0.0D || y != 0.0D || z != 0.0D) {
			if (inWater) {
				double targetY = height + iy;
				y = MathHelper.clamp(targetY - posY, -0.005, 0.02);
			}

			setBoundingBox(getBoundingBox().offset(x, y, z));
			resetPositionToBB();
		}

		//           vvvvv Use 'lastY' instead of 'y'.
		if (Math.abs(lastY) >= 1.0E-5F && Math.abs(y) < 1.0E-5F) {
			collidedY = true;
		}

		//              vvvvv Use 'lastY' instead of 'y'.
		if (!inWater) {
			onGround = lastY != y && lastY < 0.0D;
		}
		if (lastX != x) {
			motionX = 0.0D;
		}

		//  vvvvv Use 'lastZ' instead of 'z'
		if (lastZ != z) {
			motionZ = 0.0D;
		}
		//        }
	}

	@Override
	public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
		Vector3d vec3d = renderInfo.getProjectedView();
		float f = (float) (MathHelper.lerp(partialTicks, prevPosX, posX) - vec3d.getX());
		float f1 = (float) -vec3d.getY();
		if (inWater) {
			f1 += posY;
		} else {
			f1 += MathHelper.lerp(partialTicks, prevPosY, posY);
		}
		float f2 = (float) (MathHelper.lerp(partialTicks, prevPosZ, posZ) - vec3d.getZ());
		Quaternion quaternion = new Quaternion(renderInfo.getRotation());

		float rx = MathHelper.lerp(partialTicks, prevParticleAngleX, particleAngleX);
		float rz = MathHelper.lerp(partialTicks, prevParticleAngle, particleAngle);
		if (onGround || inWater) {
			quaternion = Vector3f.XP.rotationDegrees(90);
			if (inWater) {
				f1 += 0.16f + rz % 0.01;
			} else {
				f1 += 0.005f + rz % 0.01;
			}
		} else {
			quaternion = new Quaternion(renderInfo.getRotation());
			quaternion.multiply(Vector3f.XP.rotation(rx));
		}
		quaternion.multiply(Vector3f.ZP.rotation(rz));

		Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f1.transform(quaternion);
		Vector3f[] avector3f = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F) };
		float f4 = getScale(partialTicks);
		float alpha = f4 * particleAlpha;
		f4 *= particleScale * 0.15f;

		for (int i = 0; i < 4; ++i) {
			Vector3f vector3f = avector3f[i];
			vector3f.transform(quaternion);
			vector3f.mul(f4);
			vector3f.add(f, f1, f2);
		}

		float f7 = getMinU();
		float f8 = getMaxU();
		float f5 = getMinV();
		float f6 = getMaxV();
		int j = getBrightnessForRender(partialTicks);
		buffer.pos(avector3f[0].getX(), avector3f[0].getY(), avector3f[0].getZ()).tex(f8, f6).color(particleRed, particleGreen, particleBlue, alpha).lightmap(j).endVertex();
		buffer.pos(avector3f[1].getX(), avector3f[1].getY(), avector3f[1].getZ()).tex(f8, f5).color(particleRed, particleGreen, particleBlue, alpha).lightmap(j).endVertex();
		buffer.pos(avector3f[2].getX(), avector3f[2].getY(), avector3f[2].getZ()).tex(f7, f5).color(particleRed, particleGreen, particleBlue, alpha).lightmap(j).endVertex();
		buffer.pos(avector3f[3].getX(), avector3f[3].getY(), avector3f[3].getZ()).tex(f7, f6).color(particleRed, particleGreen, particleBlue, alpha).lightmap(j).endVertex();
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprite) {
			spriteSet = sprite;
		}

		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			PetalParticle noteparticle = new PetalParticle(worldIn, x, y, z);
			noteparticle.selectSpriteRandomly(spriteSet);
			return noteparticle;
		}

	}
}
