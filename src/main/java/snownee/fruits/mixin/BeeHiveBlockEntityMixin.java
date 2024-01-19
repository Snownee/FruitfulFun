package snownee.fruits.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BeehiveBlockEntity.class)
public class BeeHiveBlockEntityMixin {
	@ModifyExpressionValue(method = "releaseOccupant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z"))
	private static boolean releaseOccupantSuppressDefault(boolean original) {
		return false;
	}

	@Inject(method = "releaseOccupant", at = @At("HEAD"), cancellable = true)
	private static void releaseOccupant(Level level, BlockPos blockPos, BlockState blockState, BeehiveBlockEntity.BeeData beeData, @Nullable List<Entity> list, BeehiveBlockEntity.BeeReleaseStatus beeReleaseStatus, @Nullable BlockPos blockPos2, CallbackInfoReturnable<Boolean> ci) {
		if (beeReleaseStatus != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY && level.isRaining() && !beeData.entityData.getBoolean("RainCapable")) {
			ci.setReturnValue(false);
		}
	}
}
