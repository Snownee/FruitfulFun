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
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;
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

	@Inject(
			method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/BeeRenderState;)Lnet/minecraft/resources/Identifier;",
			at = @At("HEAD"),
			cancellable = true)
	private void getTextureLocation(BeeRenderState state, CallbackInfoReturnable<Identifier> cir) {
		if (!Hooks.bee) {
			return;
		}
		Identifier texture = state.getData(ClientProxy.TEXTURE);
		if (texture != null) {
			texture = texture.withPath($ -> {
				if (state.isAngry && state.hasNectar) {
					$ += "_angry_nectar";
				} else if (state.isAngry) {
					$ += "_angry";
				} else if (state.hasNectar) {
					$ += "_nectar";
				}
				if (state.isBaby) {
					$ += "_baby";
				}
				return "textures/entity/bee/" + $ + ".png";
			});
			cir.setReturnValue(texture);
		}
	}

	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/animal/bee/Bee;Lnet/minecraft/client/renderer/entity/state/BeeRenderState;F)V",
			at = @At("HEAD"))
	private void extractRenderState(Bee entity, BeeRenderState state, float partialTicks, CallbackInfo ci) {
		if (!Hooks.bee) {
			return;
		}
		BeeAttributes attributes = BeeAttributes.of(entity);
		state.setData(ClientProxy.SADDLE, entity.getItemBySlot(EquipmentSlot.SADDLE));
		state.setData(ClientProxy.TEXTURE, attributes.getTexture());
		if (attributes.hasTrait(Trait.GHOST)) {
			state.setData(ClientProxy.TRANSLUCENT, Unit.INSTANCE);
		}
	}
}
