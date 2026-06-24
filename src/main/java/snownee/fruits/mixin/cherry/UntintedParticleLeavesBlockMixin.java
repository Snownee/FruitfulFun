package snownee.fruits.mixin.cherry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import snownee.fruits.FFClientConfig;
import snownee.fruits.cherry.CherryModule;

@Mixin(UntintedParticleLeavesBlock.class)
public class UntintedParticleLeavesBlockMixin {
	@Inject(method = "spawnFallingLeavesParticle", at = @At("HEAD"), cancellable = true)
	private void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if ((Object) this != Blocks.CHERRY_LEAVES) {
			return;
		}
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Disabled) {
			ci.cancel();
		}
		if (FFClientConfig.cherryParticle == FFClientConfig.CherryParticleOption.Modded) {
			CherryModule.CHERRY_LEAVES.get().spawnFallingLeavesParticle(level, pos, random);
			ci.cancel();
		}
	}
}
