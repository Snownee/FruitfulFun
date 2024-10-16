package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;

@Mixin(targets = "net.minecraft.network.protocol.game.ServerboundInteractPacket$Action")
public interface ServerboundInteractPacketActionAccess {
	@Invoker
	ServerboundInteractPacket.ActionType callGetType();
}
