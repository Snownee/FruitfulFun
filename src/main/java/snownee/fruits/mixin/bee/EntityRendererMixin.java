package snownee.fruits.mixin.bee;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.genetics.Trait;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@WrapOperation(
			method = "getBlockLightLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"))
	private int getBlockLightLevel(
			Level level,
			LightLayer lightLayer,
			BlockPos pos,
			Operation<Integer> original,
			@Local(argsOnly = true) Entity entity) {
		int light = original.call(level, lightLayer, pos);
		if (entity instanceof Bee bee && BeeAttributes.of(bee).hasTrait(Trait.GHOST)) {
			return Mth.clamp(light + 4, 6, 15);
		}
		return light;
	}
}
