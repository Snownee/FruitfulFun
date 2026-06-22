package snownee.fruits.gadget.network;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.gadget.BuzzyCrafterBlock;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "click_crafter", dir = KiwiPacket.Direction.PLAY_TO_SERVER)
public class CClickCrafterPacket extends PacketHandler {
	public static CClickCrafterPacket I;

	public static void send(BlockHitResult hit) {
		I.sendToServer(buf -> {
			buf.writeBlockHitResult(hit);
		});
	}

	@Override
	public @Nullable CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		BlockHitResult hit = buf.readBlockHitResult();
		BlockPos pos = hit.getBlockPos();
		if (player == null || pos.distToCenterSqr(player.position()) > 256) {
			return null;
		}
		return executor.apply(() -> {
			BlockState blockState = player.level().getBlockState(pos);
			if (blockState.getBlock() instanceof BuzzyCrafterBlock block) {
				block.click(blockState, player.level(), pos, player, hit);
			}
		});
	}
}
