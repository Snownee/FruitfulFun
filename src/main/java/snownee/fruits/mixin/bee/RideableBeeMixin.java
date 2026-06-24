package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.animal.bee.Bee;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;

@Mixin(Bee.class)
public class RideableBeeMixin {
	@Override
	public boolean isSaddleable() {
		Bee bee = (Bee) (Object) this;
		return Hooks.bee && bee.isAlive() && !bee.isBaby() && BeeAttributes.of(bee).isSaddleable();
	}

	@Inject(method = "wantsToEnterHive", at = @At("HEAD"), cancellable = true)
	private void wantsToEnterHive(CallbackInfoReturnable<Boolean> cir) {
		Bee bee = (Bee) (Object) this;
		if (Hooks.bee && bee.getControllingPassenger() != null) {
			cir.setReturnValue(false);
		}
	}
}
