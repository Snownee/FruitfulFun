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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.util.ClientProxy;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Shadow
	protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float inverseArmHeight);

	@Shadow
	protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm arm, float attackValue);

	@Inject(
			method = "renderArmWithItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/ItemUseAnimation;"))
	private void renderArmWithItem(
			AbstractClientPlayer player,
			float frameInterp,
			float xRot,
			InteractionHand hand,
			float attack,
			ItemStack itemStack,
			float inverseArmHeight,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			int lightCoords,
			CallbackInfo ci) {
		if (Hooks.bee && BeeModule.INSPECTOR.is(itemStack)) {
			boolean bl = hand == InteractionHand.MAIN_HAND;
			HumanoidArm humanoidArm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
			applyItemArmTransform(poseStack, humanoidArm, inverseArmHeight);
			applyItemArmAttackTransform(poseStack, humanoidArm, attack);
			int k = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
			poseStack.translate(k * -0.641864f, 0.0f, 0.0f);
			poseStack.mulPose(Axis.YP.rotationDegrees(k * 10.0f));
		}
	}

	@Inject(method = "renderItem", at = @At("TAIL"))
	private void renderItem(
			LivingEntity mob,
			ItemStack itemStack,
			ItemDisplayContext type,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			int lightCoords,
			CallbackInfo ci) {
		if (Hooks.gadget && GadgetModule.VAC_GUN.is(itemStack)) {
			ClientProxy.renderVacGunInHand(mob, itemStack, type, type.leftHand(), poseStack);
		}
		//Donk.donk(Minecraft.getInstance(), Minecraft.getInstance().getEntityRenderDispatcher(), livingEntity, multiBufferSource);
	}
}
