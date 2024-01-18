package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.bee.network.SSyncPlayerPacket;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@Inject(method = "addPlayer", at = @At("HEAD"))
	private void addPlayer(ServerPlayer player, CallbackInfo ci) {
		SSyncPlayerPacket.send(player);
	}
}
