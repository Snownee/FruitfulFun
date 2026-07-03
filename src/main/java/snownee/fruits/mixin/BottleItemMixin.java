package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.BottleItem;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.ritual.RitualModule;

@Mixin(BottleItem.class)
public class BottleItemMixin {

	@WrapOperation(method = "lambda$use$0", constant = @Constant(classValue = EnderDragon.class))
	private static boolean allowDummyDragonBreath(
			Object object,
			Operation<Boolean> original,
			@Local(argsOnly = true, name = "input") AreaEffectCloud input) {
		if (input.owner != null && RitualModule.DUMMY_UUID.equals(input.owner.getUUID())) {
			return true;
		}
		return original.call(object);
	}

	@Inject(
			method = "lambda$use$0",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;isAlive()Z"),
			cancellable = true)
	private static void fixDragonBreathExploit(AreaEffectCloud input, CallbackInfoReturnable<Boolean> ci) {
		if (FFCommonConfig.fixDragonBreathExploit && input.getRadius() <= 0) {
			ci.setReturnValue(false);
		}
	}
}
