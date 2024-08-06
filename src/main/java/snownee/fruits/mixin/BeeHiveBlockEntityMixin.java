package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;

@Mixin(BeehiveBlockEntity.class)
public class BeeHiveBlockEntityMixin {
	@WrapOperation(method = "releaseOccupant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private static boolean releaseOccupantSuppressDefault(
			Level instance,
			Operation<Boolean> original,
			@Local(argsOnly = true) BeehiveBlockEntity.BeeData beeData) {
		return original.call(instance) && !beeData.entityData.getBoolean("RainCapable");
	}
}
