package snownee.fruits.mixin.bee;

import java.util.Optional;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBeehiveBlockEntity;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@WrapOperation(
			method = "evaluateNewBlockState",
			at = @At(value = "INVOKE", target = "Ljava/util/Optional;map(Ljava/util/function/Function;)Ljava/util/Optional;"))
	private Optional<BlockState> useOn(
			Optional<Block> instance,
			Function<? super Block, ? extends BlockState> mapper,
			Operation<Optional<BlockState>> original,
			@Local(argsOnly = true, name = "level") Level level,
			@Local(argsOnly = true, name = "pos") BlockPos pos,
			@Local(argsOnly = true, name = "oldState") BlockState oldState) {
		if (Hooks.bee || !oldState.is(BlockTags.BEEHIVES) || !(level.getBlockEntity(pos) instanceof FFBeehiveBlockEntity be) ||
				be.fruits$findWaxedMarkers().isEmpty() || !be.fruits$isWaxed()) {
			return original.call(instance, mapper);
		}
		be.fruits$setWaxed(false);
		return Optional.of(oldState);
	}
}
