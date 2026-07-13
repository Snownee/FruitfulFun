package snownee.fruits.gadget.crafter;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CClickCrafterPacket(BlockHitResult hit) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CClickCrafterPacket> TYPE = new CustomPacketPayload.Type<>(FruitfulFun.id("click_crafter"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CClickCrafterPacket> STREAM_CODEC = StreamCodec.composite(
			StreamCodec.of(FriendlyByteBuf::writeBlockHitResult, FriendlyByteBuf::readBlockHitResult),
			CClickCrafterPacket::hit,
			CClickCrafterPacket::new);

	@Override
	public CustomPacketPayload.Type<CClickCrafterPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CClickCrafterPacket> {
		@Override
		public void handle(CClickCrafterPacket packet, PayloadContext context) {
			context.execute(() -> {
				ServerPlayer player = Objects.requireNonNull(context.serverPlayer());
				BlockPos pos = packet.hit().getBlockPos();
				if (pos.distToCenterSqr(player.position()) > 256) {
					return;
				}
				BlockState blockState = player.level().getBlockState(pos);
				if (blockState.getBlock() instanceof BuzzyCrafterBlock block) {
					block.click(blockState, player.level(), pos, player, packet.hit());
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CClickCrafterPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(BlockHitResult hit) {
		KPacketSender.sendToServer(new CClickCrafterPacket(hit));
	}
}
