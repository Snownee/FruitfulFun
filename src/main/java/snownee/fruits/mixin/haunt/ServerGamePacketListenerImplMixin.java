package snownee.fruits.mixin.haunt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.GhostFakePlayer;
import snownee.fruits.duck.FFPlayer;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
	private void handlePlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
		if (Hooks.bee && ((FFPlayer) player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleSetCarriedItem", at = @At("HEAD"), cancellable = true)
	private void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
		if (Hooks.bee && ((FFPlayer) player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleUseItem", at = @At("HEAD"), cancellable = true)
	private void handleUseItem(ServerboundUseItemPacket packet, CallbackInfo ci) {
		if (Hooks.bee && ((FFPlayer) player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
	private void handleContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
		if (Hooks.bee && ((FFPlayer) player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handlePlayerCommand", at = @At("HEAD"), cancellable = true)
	private void handlePlayerCommand(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
		if (Hooks.bee && packet.getAction() == ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY &&
				((FFPlayer) player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "handleInteract",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V"),
			cancellable = true)
	private void handleInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
		if (!Hooks.bee || !((FFPlayer) player).fruits$isHaunting()) {
			return;
		}
		ServerboundInteractPacket.ActionType actionType = ((ServerboundInteractPacketActionAccess) packet.action).callGetType();
		if (actionType == ServerboundInteractPacket.ActionType.INTERACT_AT && !BeeModule.isHauntingNormalEntity(player, null)) {
			return;
		} else if (actionType != ServerboundInteractPacket.ActionType.ATTACK) {
			GhostFakePlayer fakePlayer = null;
			try {
				fakePlayer = GhostFakePlayer.getOrCreate(player);
				fakePlayer.connection.handleInteract(packet);
			} catch (Exception e) {
				FruitfulFun.LOGGER.trace("Failed to handle Interact packet", e);
			} finally {
				if (fakePlayer != null) {
					fakePlayer.getInventory().dropAll();
					fakePlayer.discard();
				}
			}
		}
		ci.cancel();
	}

	@Inject(
			method = "handleUseItemOn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;ackBlockChangesUpTo(I)V"), cancellable = true)
	private void handleUseItemOn(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
		if (!Hooks.bee || !((FFPlayer) player).fruits$isHaunting()) {
			return;
		}
		GhostFakePlayer fakePlayer = null;
		try {
			fakePlayer = GhostFakePlayer.getOrCreate(player);
			fakePlayer.connection.handleUseItemOn(packet);
		} catch (Exception e) {
			FruitfulFun.LOGGER.trace("Failed to handle UseItemOn packet", e);
		} finally {
			if (fakePlayer != null) {
				fakePlayer.getInventory().dropAll();
				fakePlayer.discard();
			}
		}
		ci.cancel();
	}

	@WrapOperation(
			method = {"handleMoveVehicle", "tick"}, at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
	private Entity getRootVehicle(ServerPlayer player, Operation<Entity> original) {
		if (Hooks.bee && ((FFPlayer) player).fruits$isHaunting()) {
			return ((FFPlayer) player).fruits$hauntingTarget();
		}
		return original.call(player);
	}

	@WrapOperation(
			method = "handleMoveVehicle", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;absMoveTo(DDDFF)V"))
	private void handleMoveVehicleMovePlayer(
			Entity entity,
			double x,
			double y,
			double z,
			float yRot,
			float xRot,
			Operation<Void> original) {
		if (Hooks.bee && ((FFPlayer) player).fruits$isHaunting()) {
			player.absMoveTo(x, y, z, yRot, xRot);
		}
		original.call(entity, x, y, z, yRot, xRot);
	}
}
