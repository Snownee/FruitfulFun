package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.util.CommonProxy;

@Mixin(EnderMan.EndermanTakeBlockGoal.class)
public class EndermanLeaveBlockGoalMixin {
	@WrapOperation(
			method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean isScentedCandle(
			BlockState blockState,
			TagKey<Block> tagKey,
			Operation<Boolean> original,
			@Local Level level,
			@Local BlockPos pos) {
		if (Hooks.gadget) {
			LevelChunk chunk = CommonProxy.getLoadedChunkAt(level, pos);
			if (chunk != null && GadgetModule.ENDER.get().isActiveAt(chunk)) {
				return false;
			}
		}
		return original.call(blockState, tagKey);
	}
}
