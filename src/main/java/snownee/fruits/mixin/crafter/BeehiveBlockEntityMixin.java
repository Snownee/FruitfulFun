package snownee.fruits.mixin.crafter;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.BuzzyCrafterBlock;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
	@WrapOperation(
			method = "releaseOccupant",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;Ljava/util/function/Predicate;)Z"))
	private static boolean skipChangingHoneyLevel(
			BlockState blockState,
			TagKey<Block> tagKey,
			Predicate<BlockState> predicate,
			Operation<Boolean> original) {
		if (Hooks.gadget && blockState.getBlock() instanceof BuzzyCrafterBlock) {
			return false;
		}
		return original.call(blockState, tagKey, predicate);
	}
}
