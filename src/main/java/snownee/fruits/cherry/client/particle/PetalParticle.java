package snownee.fruits.cherry.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PetalParticle extends SpriteTexturedParticle {
    private PetalParticle(World p_i51018_1_, double p_i51018_2_, double p_i51018_4_, double p_i51018_6_, double p_i51018_8_) {
        super(p_i51018_1_, p_i51018_2_, p_i51018_4_, p_i51018_6_, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.01F;
        this.motionY *= -0.01F;
        this.motionZ *= 0.01F;
        this.motionY += 0.2D;
        this.particleRed = 1; //Math.max(0.0F, MathHelper.sin(((float) p_i51018_8_ + 0.0F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.particleGreen = 1; //Math.max(0.0F, MathHelper.sin(((float) p_i51018_8_ + 0.33333334F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.particleBlue = 1; //Math.max(0.0F, MathHelper.sin(((float) p_i51018_8_ + 0.6666667F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F);
        this.particleScale *= 1.5F;
        this.maxAge = 60;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getScale(float p_217561_1_) {
        return this.particleScale * MathHelper.clamp((this.age + p_217561_1_) / this.maxAge * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.move(this.motionX, this.motionY, this.motionZ);
            if (this.posY == this.prevPosY) {
                this.motionX *= 1.1D;
                this.motionZ *= 1.1D;
            }

            this.motionX *= 0.66F;
            this.motionY *= 0.66F;
            this.motionZ *= 0.66F;
            if (this.onGround) {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50044_1_) {
            this.spriteSet = p_i50044_1_;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PetalParticle noteparticle = new PetalParticle(worldIn, x, y, z, xSpeed);
            noteparticle.selectSpriteRandomly(this.spriteSet);
            return noteparticle;
        }
    }
}
