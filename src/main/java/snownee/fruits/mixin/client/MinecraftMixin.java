package snownee.fruits.mixin.client;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import snownee.fruits.Hooks;
import snownee.fruits.bee.network.CHauntingActionPacket;
import snownee.fruits.gadget.BuzzyCrafterBlock;
import snownee.fruits.gadget.network.CClickCrafterPacket;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public HitResult hitResult;
	@Shadow
	@Nullable
	public ClientLevel level;
	@Shadow
	@Nullable
	public LocalPlayer player;
	@Shadow
	public int missTime;
	@Shadow
	@Nullable
	public MultiPlayerGameMode gameMode;

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void startAttackHaunting(CallbackInfoReturnable<Boolean> cir) {
		if (Hooks.bee && player != null && CHauntingActionPacket.canDoAction(player)) {
			CHauntingActionPacket.I.sendToServer(buf -> {});
			cir.setReturnValue(true);
		}
	}

	@Inject(
			method = "startAttack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"),
			cancellable = true)
	private void startAttackGadget(CallbackInfoReturnable<Boolean> cir) {
		if (player == null || player.isSpectator()) {
			return;
		}
		Objects.requireNonNull(hitResult);
		Objects.requireNonNull(level);
		BlockHitResult hit = (BlockHitResult) hitResult;
		BlockPos pos = hit.getBlockPos();
		BlockState blockState = level.getBlockState(pos);
		if (!(blockState.getBlock() instanceof BuzzyCrafterBlock block) || block.canBeDestroyed(blockState, level, pos, player, hit)) {
			return;
		}
		CClickCrafterPacket.send(hit);
		if (player.isCreative()) {
			player.swing(InteractionHand.MAIN_HAND);
			missTime = 10;
			Objects.requireNonNull(gameMode).stopDestroyBlock();
			cir.setReturnValue(false);
		}
	}
}
