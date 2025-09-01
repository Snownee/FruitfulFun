package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(WanderingTraderSpawner.class)
public class WanderingTraderSpawnerMixin {
	@Inject(
			method = "spawn",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;blockPosition()Lnet/minecraft/core/BlockPos;"),
			cancellable = true)
	private void spawn(ServerLevel serverLevel, CallbackInfoReturnable<Boolean> cir, @Local Player player) {
		if (Hooks.gadget && player.hasEffect(GadgetModule.WANDERING_TRADER_SCENT.get())) {
			cir.setReturnValue(false);
		}
	}
}
