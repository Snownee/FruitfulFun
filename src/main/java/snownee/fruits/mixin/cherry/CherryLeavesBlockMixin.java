package snownee.fruits.mixin.cherry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CherryLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.FFClientConfig;
import snownee.fruits.cherry.CherryModule;

@Mixin(CherryLeavesBlock.class)
public class CherryLeavesBlockMixin {
	@Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
	private void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Disabled) {
			ci.cancel();
		}
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Modded) {
			CherryModule.CHERRY_LEAVES.get().animateTick(blockState, level, blockPos, randomSource);
			ci.cancel();
		}
	}
}
