package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.duck.FFPlayer;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(method = "changeDimension", at = @At("TAIL"))
	private void changeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
		BeeModule.changeDimension(destination, (Entity) (Object) this, cir.getReturnValue());
	}

	@WrapOperation(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;noPhysics:Z"))
	private boolean move(Entity instance, Operation<Boolean> original) {
		if (Hooks.bee && instance instanceof FFPlayer player && player.fruits$isHaunting()) {
			return true;
		}
		return original.call(instance);
	}

	@Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
	private void isInvisible(CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && this instanceof FFPlayer player && player.fruits$isHaunting()) {
			cir.setReturnValue(true);
		}
	}
}
