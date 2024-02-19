package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(FlyNodeEvaluator.class)
public class FlyNodeEvaluatorMixin extends WalkNodeEvaluator {
	@ModifyExpressionValue(
			method = "getBlockPathType(Lnet/minecraft/world/level/BlockGetter;III)Lnet/minecraft/world/level/pathfinder/BlockPathTypes;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/pathfinder/FlyNodeEvaluator;getBlockPathTypeRaw(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/pathfinder/BlockPathTypes;",
					ordinal = 0))
	private BlockPathTypes getBlockPathType(BlockPathTypes original, BlockGetter level, @Local BlockPos.MutableBlockPos pos) {
		if (original == BlockPathTypes.LEAVES && mob instanceof FlyingAnimal) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof FruitLeavesBlock) {
				return BlockPathTypes.OPEN;
			}
		}
		return original;
	}
}
