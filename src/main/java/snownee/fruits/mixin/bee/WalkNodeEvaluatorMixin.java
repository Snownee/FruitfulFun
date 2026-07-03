package snownee.fruits.mixin.bee;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(WalkNodeEvaluator.class)
public class WalkNodeEvaluatorMixin {
	@Inject(
			method = "getPathTypeFromState",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/level/pathfinder/PathType;LEAVES:Lnet/minecraft/world/level/pathfinder/PathType;",
					opcode = Opcodes.GETSTATIC),
			cancellable = true)
	private static void getPathTypeFromState(
			BlockGetter level,
			BlockPos pos,
			CallbackInfoReturnable<PathType> cir,
			@Local(name = "block") Block block) {
		if (block instanceof FruitLeavesBlock) {
			cir.setReturnValue(PathType.FRUITFULFUN_LEAVES);
		}
	}
}
