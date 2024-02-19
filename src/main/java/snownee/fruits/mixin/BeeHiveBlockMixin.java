package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.BeehiveBlock;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;

@Mixin(BeehiveBlock.class)
public class BeeHiveBlockMixin {
	@ModifyExpressionValue(
			method = "angerNearbyBees",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/animal/Bee;getTarget()Lnet/minecraft/world/entity/LivingEntity;"))
	private static LivingEntity angerNearbyBees(LivingEntity original, @Local Bee bee) {
		if (original == null && BeeAttributes.of(bee).hasTrait(Trait.MILD)) {
			return bee; // return anything nonnull to continue the loop
		}
		return original;
	}
}
