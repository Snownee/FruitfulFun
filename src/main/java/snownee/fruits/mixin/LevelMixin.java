package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import snownee.fruits.FFCommonConfig;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Shadow
	public abstract DimensionType dimensionType();

	@Shadow
	public abstract ResourceKey<DimensionType> dimensionTypeId();

	@Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
	private void isRaining(CallbackInfoReturnable<Boolean> cir) {
		if (FFCommonConfig.fixRainingStateInNetherAndEnd) {
			if (dimensionTypeId() == BuiltinDimensionTypes.END) {
				cir.setReturnValue(false);
			}
			DimensionType dimensionType = dimensionType();
			if (dimensionType.hasCeiling() || dimensionType.ultraWarm()) {
				cir.setReturnValue(false);
			}
		}
	}
}
