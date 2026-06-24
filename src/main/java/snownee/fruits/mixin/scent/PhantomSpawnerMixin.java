package snownee.fruits.mixin.scent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import snownee.fruits.Hooks;
import snownee.fruits.gadget.GadgetModule;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"))
	private boolean tick(ServerPlayer player, Operation<Boolean> original) {
		if (Hooks.gadget && player.hasEffect(GadgetModule.PHANTOM_SCENT.holderOrThrow())) {
			return true;
		}
		return original.call(player);
	}
}
