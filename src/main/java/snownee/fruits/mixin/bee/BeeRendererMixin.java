package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.animal.bee.AdultBeeModel;
import net.minecraft.client.model.animal.bee.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.util.ClientProxy;

@Mixin(BeeRenderer.class)
public abstract class BeeRendererMixin extends MobRenderer<Bee, BeeRenderState, BeeModel> {
	public BeeRendererMixin(EntityRendererProvider.Context context, BeeModel entityModel, float f) {
		super(context, entityModel, f);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(EntityRendererProvider.Context context, CallbackInfo ci) {
		if (!Hooks.bee) {
			return;
		}
		this.addLayer(new SimpleEquipmentLayer<>(
				(BeeRenderer) (Object) this,
				context.getEquipmentRenderer(),
				EquipmentClientInfo.LayerType.PIG_SADDLE,
				state -> state.getDataOrDefault(ClientProxy.SADDLE, ItemStack.EMPTY),
				new AdultBeeModel(context.bakeLayer(ModelLayers.BEE)),
				null));
//		FruitfulFun.id("textures/entity/bee/bee_saddle.png")
	}

	@Inject(method = "getTextureLocation*", at = @At("HEAD"), cancellable = true)
	private void getTextureLocation(Bee bee, CallbackInfoReturnable<Identifier> ci) {
		if (!Hooks.bee) {
			return;
		}
		BeeAttributes attributes = BeeAttributes.of(bee);
		Identifier texture = attributes.getTexture();
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
