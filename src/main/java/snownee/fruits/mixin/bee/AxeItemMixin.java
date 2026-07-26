package snownee.fruits.mixin.bee;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.fruits.Hooks;
import snownee.fruits.duck.FFBeehiveBlockEntity;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@Shadow
	private static void spawnSoundAndParticle(
			Level level,
			BlockPos pos,
			@Nullable Player player,
			BlockState oldState,
			SoundEvent soundEvent,
			int particle) {throw new UnsupportedOperationException("Implemented via mixin");}

	@WrapOperation(
			method = "evaluateNewBlockState",
			at = @At(value = "INVOKE", target = "Ljava/util/Optional;empty()Ljava/util/Optional;"))
	private Optional<BlockState> useOn(
			Operation<Optional<BlockState>> original,
			@Local(argsOnly = true, name = "level") Level level,
			@Local(argsOnly = true, name = "pos") BlockPos pos,
			@Local(argsOnly = true, name = "player") @Nullable Player player,
			@Local(argsOnly = true, name = "oldState") BlockState oldState) {
		if (Hooks.bee || !oldState.is(BlockTags.BEEHIVES) || !(level.getBlockEntity(pos) instanceof FFBeehiveBlockEntity be) ||
				be.fruits$findWaxedMarkers().isEmpty() || !be.fruits$isWaxed()) {
			return original.call();
		}
		spawnSoundAndParticle(level, pos, player, oldState, SoundEvents.AXE_WAX_OFF, 3004);
		be.fruits$setWaxed(false);
		return Optional.of(oldState);
	}
}
