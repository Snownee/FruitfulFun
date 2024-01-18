package snownee.fruits.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.util.ClientProxy;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@Shadow
	@Final
	private ItemModelShaper itemModelShaper;

	@Inject(method = "render", at = @At("HEAD"))
	private void render(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci, @Local LocalRef<BakedModel> modelSetter) {
		if (itemDisplayContext == ItemDisplayContext.HEAD && itemStack.getItem() instanceof FlowerCrownItem) {
			ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
			modelSetter.set(ClientProxy.getModel(itemModelShaper.getModelManager(), id.withPrefix("block/")));
		}
	}
}
