package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(Level.class)
public class LevelMixin {
	@WrapOperation(
			method = "getCurrentDifficultyAt",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;getInhabitedTime()J"))
	private long getCurrentDifficultyAt(LevelChunk chunk, Operation<Long> original, @Local LocalFloatRef moonBrightness) {
		if (Hooks.gadget && GadgetModule.WEAK.get().isActiveAt(chunk)) {
			moonBrightness.set(1);
			return 3600000 * 100;
		}
		return original.call(chunk);
	}
}
