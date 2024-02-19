package snownee.fruits.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;

public class Donk {

	public static void donk(Minecraft mc, EntityRenderDispatcher entityRenderDispatcher, Entity p, MultiBufferSource buffers) {
		var particle = mc.particleEngine.createParticle(
				ParticleTypes.TOTEM_OF_UNDYING, p.getX() - 0.5, p.getY() + 1, p.getZ() - 0.5, 0, 0, 0);

		if (particle == null) {
			System.out.println("Error: particle failed to be created");
			return;
		}
		RenderType renderType = RenderType.create("particle_translucent", DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 256, false,
				true,
				RenderType.CompositeState.builder()
						.setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
						.setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
						.setTransparencyState(new RenderStateShard.TransparencyStateShard("particle", () -> {
							RenderSystem.enableBlend();
							RenderSystem.defaultBlendFunc();
						}, () -> {
							RenderSystem.disableBlend();
							RenderSystem.defaultBlendFunc();
						}))
						.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getParticleShader))
						.createCompositeState(false));
		particle.render(buffers.getBuffer(renderType), entityRenderDispatcher.camera, mc.getFrameTime());
	}
}
