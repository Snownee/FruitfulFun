package snownee.fruits.mixin.shield;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(FlyingMoveControl.class)
public class FlyingMoveControlMixin {
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setYya(F)V"))
	private void tick(Mob mob, float yya, Operation<Void> original) {
		if (Hooks.gadget && GadgetModule.SUMMONED_BEE.is(mob.getType())) {
			yya = Mth.clamp(yya, -0.25F, 0.25F);
		}
		original.call(mob, yya);
	}
}
