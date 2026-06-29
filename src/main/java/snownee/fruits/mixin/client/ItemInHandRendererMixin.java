package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.util.ClientProxy;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
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
