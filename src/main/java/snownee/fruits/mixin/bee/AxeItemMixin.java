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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.duck.FFBeehiveBlockEntity;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@WrapOperation(
			method = "useOn",
			at = @At(value = "INVOKE", target = "Ljava/util/Optional;map(Ljava/util/function/Function;)Ljava/util/Optional;"))
	private Optional<BlockState> useOn(
			Optional<Block> instance,
			Function<? super Block, ? extends BlockState> mapper,
			Operation<Optional<BlockState>> original,
			@Local(argsOnly = true) UseOnContext context,
			@Local BlockState blockState) {
		if (!blockState.is(BlockTags.BEEHIVES)) {
			return original.call(instance, mapper);
		}
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!(level.getBlockEntity(pos) instanceof FFBeehiveBlockEntity be)) {
			return original.call(instance, mapper);
		}
		if (be.fruits$findWaxedMarkers().isEmpty()) {
			return original.call(instance, mapper);
		}
		if (!be.fruits$isWaxed()) {
			return original.call(instance, mapper);
		}
		be.fruits$setWaxed(false);
		return Optional.of(blockState);
	}
}
