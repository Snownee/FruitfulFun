package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import snownee.fruits.util.ClientProxy;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends HumanoidRenderState> {
	@Shadow
	@Final
	public ModelPart leftArm;

	@Shadow
	@Final
	public ModelPart rightArm;

	@Shadow
	@Final
	public ModelPart head;

	@Inject(at = @At("HEAD"), method = "poseLeftArm", cancellable = true)
	private void poseLeftArm(T state, CallbackInfo ci) {
		if (state.leftArmPose == HumanoidModel.ArmPose.SPYGLASS && ClientProxy.poseArm(state, leftArm, head, false)) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "poseRightArm", cancellable = true)
	private void poseRightArm(T state, CallbackInfo ci) {
		if (state.rightArmPose == HumanoidModel.ArmPose.SPYGLASS && ClientProxy.poseArm(state, rightArm, head, true)) {
			ci.cancel();
		}
	}
}
