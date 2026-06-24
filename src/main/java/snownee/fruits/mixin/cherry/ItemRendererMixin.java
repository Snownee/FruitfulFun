package snownee.fruits.mixin.cherry;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.item.ItemModelResolver;

@Mixin(ItemModelResolver.class)
public class ItemRendererMixin {
//	@Shadow
//	@Final
//	private ItemModelShaper itemModelShaper;
//
//	@Inject(method = "render", at = @At("HEAD"))
//	private void render(
//			ItemStack itemStack,
//			ItemDisplayContext itemDisplayContext,
//			boolean bl,
//			PoseStack poseStack,
//			MultiBufferSource multiBufferSource,
//			int i,
//			int j,
//			BakedModel bakedModel,
//			CallbackInfo ci,
//			@Local(argsOnly = true) LocalRef<BakedModel> modelSetter) {
//		if (itemDisplayContext == ItemDisplayContext.HEAD && itemStack.getItem() instanceof FlowerCrownItem) {
//			Identifier id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
//			BakedModel model = ClientProxy.getModel(itemModelShaper.getModelManager(), id.withPrefix("block/"));
//			if (model != null) {
//				modelSetter.set(model);
//			}
//		}
//	}
}
