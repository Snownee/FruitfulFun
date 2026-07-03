package snownee.fruits.mixin.bee;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBeehiveBlockEntity;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {
	//DispenseBehavior?
	@WrapOperation(
			method = "useOn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/HoneycombItem;getWaxed(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> useOn(
			BlockState oldState,
			Operation<Optional<BlockState>> original,
			@Local(argsOnly = true, name = "context") UseOnContext context) {
		if (!Hooks.bee || !oldState.is(BlockTags.BEEHIVES)) {
			return original.call(oldState);
		}
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!(level.getBlockEntity(pos) instanceof FFBeehiveBlockEntity be) || be.fruits$isWaxed()) {
			return original.call(oldState);
		}
		if (!level.isClientSide()) {
			be.fruits$setWaxed(true);
		}
		return Optional.of(oldState);
	}
}
