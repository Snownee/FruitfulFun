package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@WrapOperation(
			method = "getCurrentDifficultyAt",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkAccess;getInhabitedTime()J"))
	private long getCurrentDifficultyAt(
			ChunkAccess chunk,
			Operation<Long> original,
			@Local(name = "moonBrightness") LocalFloatRef moonBrightness) {
		if (Hooks.gadget && GadgetModule.WEAK.get().isActiveAt((LevelAccessor) this, chunk)) {
			moonBrightness.set(1);
			return 3600000 * 100;
		}
		return original.call(chunk);
	}
}
