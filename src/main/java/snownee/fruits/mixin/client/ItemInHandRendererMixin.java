package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.util.ClientProxy;
import snownee.fruits.vacuum.VacModule;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Shadow
	protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f);

	@Shadow
	protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f);

	@Inject(
			method = "renderArmWithItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;"))
	private void renderArmWithItem(
			AbstractClientPlayer player,
			float f,
			float g,
			InteractionHand hand,
			float h,
			ItemStack stack,
			float i,
			PoseStack poseStack,
			MultiBufferSource multiBufferSource,
			int j,
			CallbackInfo ci) {
		if (Hooks.bee && BeeModule.INSPECTOR.is(stack)) {
			boolean bl = hand == InteractionHand.MAIN_HAND;
			HumanoidArm humanoidArm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
			applyItemArmTransform(poseStack, humanoidArm, i);
			applyItemArmAttackTransform(poseStack, humanoidArm, h);
			int k = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
			poseStack.translate(k * -0.641864f, 0.0f, 0.0f);
			poseStack.mulPose(Axis.YP.rotationDegrees(k * 10.0f));
		}
	}

	@Inject(method = "renderItem", at = @At("TAIL"))
	private void renderItem(
			LivingEntity livingEntity,
			ItemStack itemStack,
			ItemDisplayContext itemDisplayContext,
			boolean leftHand,
			PoseStack poseStack,
			MultiBufferSource multiBufferSource,
			int i,
			CallbackInfo ci) {
		if (Hooks.vac && VacModule.VAC_GUN.is(itemStack)) {
			ClientProxy.renderVacGunInHand(livingEntity, itemStack, itemDisplayContext, leftHand, poseStack);
		}
		//Donk.donk(Minecraft.getInstance(), Minecraft.getInstance().getEntityRenderDispatcher(), livingEntity, multiBufferSource);
	}
}
