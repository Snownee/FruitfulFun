package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import snownee.fruits.vacuum.ContainerOpenerFakePlayer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = {"addPairing", "removePairing"}, at = @At("HEAD"), cancellable = true)
	private void addPairing(ServerPlayer serverPlayer, CallbackInfo ci) {
		if (serverPlayer.getClass() == ContainerOpenerFakePlayer.class || entity.getClass() == ContainerOpenerFakePlayer.class) {
			ci.cancel();
		}
	}
}
