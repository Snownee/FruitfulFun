package snownee.fruits.mixin.cherry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.block.CherryLeavesBlock;

@Mixin(Block.class)
public class BlockMixin {
	@Inject(method = "spawnDestroyParticles", at = @At("HEAD"))
	private void spawnDestroyParticles(Level level, Player player, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (blockState.is(Blocks.CHERRY_LEAVES)) {
			CherryLeavesBlock.spawnDestroyParticles(level, player, blockPos, CherryModule.PETAL_CHERRY.get());
		}
	}
}
