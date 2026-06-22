package snownee.fruits.mixin.client;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import snownee.fruits.FFClientConfig;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
	@Unique
	private static void renderServerSideHitbox(PoseStack poseStack, Entity entity, MultiBufferSource buffer) {
		Entity entity2 = getServerSideEntity(entity);
		if (entity2 == null) {
			// not supported on 1.20. Check OutlineBufferSource.java
//			DebugRenderer.renderFloatingText(
//					poseStack,
//					buffer,
//					"Missing",
//					entity.getX(),
//					entity.getBoundingBox().maxY + 1.5,
//					entity.getZ(),
//					-65536);
		} else {
			AABB aABB = entity2.getBoundingBox().move(
					-entity.getX(),
					-entity.getY(),
					-entity.getZ());
			LevelRenderer.renderLineBox(poseStack, buffer.getBuffer(RenderType.lines()), aABB, 0.0F, 1.0F, 0.0F, 1.0F);
		}
	}

	@Unique
	@Nullable
	private static Entity getServerSideEntity(Entity entity) {
		IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();
		if (integratedServer != null) {
			ServerLevel serverLevel = integratedServer.getLevel(entity.level().dimension());
			if (serverLevel != null) {
				return serverLevel.getEntity(entity.getId());
			}
		}
		return null;
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;F)V"))
	private <E extends Entity> void render(
			E entity,
			double x,
			double y,
			double z,
			float rotationYaw,
			float partialTicks,
			PoseStack poseStack,
			MultiBufferSource buffer,
			int packedLight,
			CallbackInfo ci) {
		if (FFClientConfig.renderServerSideHitbox) {
			renderServerSideHitbox(poseStack, entity, buffer);
		}
	}

}
