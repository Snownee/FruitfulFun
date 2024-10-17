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
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.GhostFakePlayer;
import snownee.fruits.bee.network.SHauntingParticles;
import snownee.fruits.duck.FFPlayer;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
	private void handlePlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleSetCarriedItem", at = @At("HEAD"), cancellable = true)
	private void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleUseItem", at = @At("HEAD"), cancellable = true)
	private void handleUseItem(ServerboundUseItemPacket packet, CallbackInfo ci) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
	private void handleContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(method = "handlePlayerCommand", at = @At("HEAD"), cancellable = true)
	private void handlePlayerCommand(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
		if (Hooks.bee && packet.getAction() == ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY &&
				FFPlayer.of(player).fruits$isHaunting()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "handleInteract",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V"),
			cancellable = true)
	private void handleInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
		if (!Hooks.bee || !FFPlayer.of(player).fruits$isHaunting()) {
			return;
		}
		ServerboundInteractPacket.ActionType actionType = packet.action.getType();
		boolean hauntingNormalEntity = BeeModule.isHauntingNormalEntity(player, null);
		if (actionType == ServerboundInteractPacket.ActionType.INTERACT_AT && !hauntingNormalEntity) {
			return;
		} else if (!FFCommonConfig.hauntingInteraction && hauntingNormalEntity &&
				actionType != ServerboundInteractPacket.ActionType.ATTACK) {
			GhostFakePlayer fakePlayer = null;
			try {
				fakePlayer = GhostFakePlayer.getOrCreate(player);
				fakePlayer.connection.handleInteract(packet);
			} catch (Exception e) {
				FruitfulFun.LOGGER.error("Failed to handle Interact packet", e);
			} finally {
				if (fakePlayer != null) {
					fakePlayer.getInventory().dropAll();
					fakePlayer.discard();
				}
			}
		}
		ci.cancel();
	}

//	@WrapOperation(
//			method = "handleInteract", at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;dispatch(Lnet/minecraft/network/protocol/game/ServerboundInteractPacket$Handler;)V"))
//	private void spawnInteractParticles(
//			ServerboundInteractPacket instance,
//			ServerboundInteractPacket.Handler handler,
//			Operation<Void> original,
//			@Local Entity entity) {
//		if (player.getClass() == GhostFakePlayer.class) {
//			SHauntingParticles.spawnOnEntity(player, entity);
//		}
//		original.call(instance, handler);
//	}

	@Inject(
			method = "handleUseItemOn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;ackBlockChangesUpTo(I)V"), cancellable = true)
	private void handleUseItemOn(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
		if (!Hooks.bee || !FFPlayer.of(player).fruits$isHaunting()) {
			return;
		}
		if (!FFCommonConfig.hauntingInteraction || !BeeModule.isHauntingNormalEntity(player, null)) {
			ci.cancel();
			return;
		}
		GhostFakePlayer fakePlayer = null;
		try {
			fakePlayer = GhostFakePlayer.getOrCreate(player);
			fakePlayer.connection.handleUseItemOn(packet);
		} catch (Exception e) {
			FruitfulFun.LOGGER.error("Failed to handle UseItemOn packet", e);
		} finally {
			if (fakePlayer != null) {
				fakePlayer.getInventory().dropAll();
				fakePlayer.discard();
			}
		}
		ci.cancel();
	}

	@WrapOperation(
			method = "handleUseItemOn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerPlayerGameMode;useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult spawnUseItemOnParticles(
			ServerPlayerGameMode instance,
			ServerPlayer player,
			Level level,
			ItemStack stack,
			InteractionHand hand,
			BlockHitResult hitResult,
			Operation<InteractionResult> original) {
		InteractionResult result = original.call(instance, player, level, stack, hand, hitResult);
		if (FFCommonConfig.hauntingInteractionParticles && result.consumesAction() && player instanceof GhostFakePlayer fakePlayer) {
			if (fakePlayer.getOwnerUUID() != null) {
				Player owner = player.serverLevel().getPlayerByUUID(fakePlayer.getOwnerUUID());
				if (owner != null) {
					Hooks.awardSimpleAdvancement(owner, "haunting_interaction");
				}
			}
			SHauntingParticles.send(player.serverLevel(), hitResult.getLocation());
		}
		return result;
	}

	@WrapOperation(
			method = {"handleMoveVehicle", "tick"}, at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
	private Entity getRootVehicle(ServerPlayer player, Operation<Entity> original) {
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			return FFPlayer.of(player).fruits$hauntingTarget();
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
		if (Hooks.bee && FFPlayer.of(player).fruits$isHaunting()) {
			player.absMoveTo(x, y, z, yRot, xRot);
		}
		original.call(entity, x, y, z, yRot, xRot);
	}
}
