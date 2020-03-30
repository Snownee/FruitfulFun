package snownee.fruits.cherry.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PetalParticle extends SpriteTexturedParticle {

    private float angleStepX;
    private float angleStepZ;
    private Vector3f vForce;
    private float particleAngleX;
    private float prevParticleAngleX;

    private PetalParticle(World world, double posX, double posY, double posZ) {
        super(world, posX, posY, posZ);
        this.maxAge = 60;
        age = rand.nextInt(20);
        particleScale = 0.12f + rand.nextFloat() * 0.04f;
        particleAlpha = 0.7f + rand.nextFloat() * 0.3f;

        float baseMotionX = 0.2f + rand.nextFloat() * 0.06f;
        float baseMotionY = -0.5f;
        float baseMotionZ = 0.3f + rand.nextFloat() * 0.06f;

        Vector3f motion = new Vector3f(baseMotionX, baseMotionY, baseMotionZ);
        motion.normalize();
        vForce = new Vector3f(baseMotionZ, 0, -baseMotionX);
        Quaternion rot = vForce.rotationDegrees(-90);
        vForce = motion.copy();
        vForce.transform(rot);
        motion.mul(0.1f + rand.nextFloat() * 0.05f);

        this.motionX = motion.getX();
        this.motionY = motion.getY();
        this.motionZ = motion.getZ();

        angleStepX = rand.nextFloat() * 0.1f;
        angleStepZ = rand.nextFloat() * 0.1f;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getScale(float pTicks) {
        return this.particleScale * MathHelper.clamp((this.age + pTicks) / this.maxAge * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevParticleAngle = this.particleAngle;
        this.prevParticleAngleX = this.particleAngleX;

        if (this.age++ >= this.maxAge) {
            this.setExpired();
            return;
        }

        float mul = age % 10 < 5 ? 0.03f : -0.03f;
        this.motionX += vForce.getX() * mul;
        this.motionY += vForce.getY() * mul;
        this.motionZ += vForce.getZ() * mul;
        particleAngle += angleStepZ;
        particleAngleX += angleStepX;

        this.move(this.motionX, this.motionY, this.motionZ);

        if (onGround) {
            this.setExpired();
            return;
        }

        this.motionX *= 1.001F;
        this.motionY *= 0.998F;
        this.motionZ *= 1.001F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        Vec3d vec3d = renderInfo.getProjectedView();
        float f = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - vec3d.getX());
        float f1 = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - vec3d.getY());
        float f2 = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - vec3d.getZ());
        Quaternion quaternion = new Quaternion(renderInfo.getRotation());
        float rx = MathHelper.lerp(partialTicks, this.prevParticleAngleX, this.particleAngleX);
        float rz = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
        quaternion.multiply(Vector3f.XP.rotation(rx));
        quaternion.multiply(Vector3f.ZP.rotation(rz));

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F) };
        float f4 = this.getScale(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getMinU();
        float f8 = this.getMaxU();
        float f5 = this.getMinV();
        float f6 = this.getMaxV();
        int j = this.getBrightnessForRender(partialTicks);
        buffer.pos(avector3f[0].getX(), avector3f[0].getY(), avector3f[0].getZ()).tex(f8, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer.pos(avector3f[1].getX(), avector3f[1].getY(), avector3f[1].getZ()).tex(f8, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer.pos(avector3f[2].getX(), avector3f[2].getY(), avector3f[2].getZ()).tex(f7, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer.pos(avector3f[3].getX(), avector3f[3].getY(), avector3f[3].getZ()).tex(f7, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PetalParticle noteparticle = new PetalParticle(worldIn, x, y, z);
            noteparticle.selectSpriteRandomly(this.spriteSet);
            return noteparticle;
        }
    }
}
