package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;

@Mixin(BeeRenderer.class)
public abstract class BeeRendererMixin extends MobRenderer<Bee, BeeModel<Bee>> {
	public BeeRendererMixin(EntityRendererProvider.Context context, BeeModel<Bee> entityModel, float f) {
		super(context, entityModel, f);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(EntityRendererProvider.Context context, CallbackInfo ci) {
		if (!Hooks.bee) {
			return;
		}
		this.addLayer((RenderLayer<Bee, BeeModel<Bee>>) new SaddleLayer(
				(BeeRenderer) (Object) this,
				new BeeModel(context.bakeLayer(ModelLayers.BEE)),
				FruitfulFun.id("textures/entity/bee/bee_saddle.png")));
	}

	@Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
	private void getTextureLocation(Bee bee, CallbackInfoReturnable<ResourceLocation> ci) {
		if (!Hooks.bee) {
			return;
		}
		BeeAttributes attributes = BeeAttributes.of(bee);
		ResourceLocation texture = attributes.getTexture();
		if (texture != null) {
			texture = texture.withPath($ -> {
				if (bee.isAngry() && bee.hasNectar()) {
					$ += "_angry_nectar";
				} else if (bee.isAngry()) {
					$ += "_angry";
				} else if (bee.hasNectar()) {
					$ += "_nectar";
				}
				return "textures/entity/bee/" + $ + ".png";
			});
			ci.setReturnValue(texture);
		}
	}
}
