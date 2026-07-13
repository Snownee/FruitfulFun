package snownee.fruits.gadget.vac;

import java.util.Objects;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CGunShotPacket() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CGunShotPacket> TYPE = new CustomPacketPayload.Type<>(
			FruitfulFun.id("gun_shot"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CGunShotPacket> STREAM_CODEC = StreamCodec.unit(new CGunShotPacket());

	@Override
	public CustomPacketPayload.Type<CGunShotPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CGunShotPacket> {
		@Override
		public void handle(CGunShotPacket packet, PayloadContext context) {
			context.execute(() -> {
				ServerPlayer player = Objects.requireNonNull(context.serverPlayer());
				if (!player.isRemoved() && !player.isSpectator() && player.getMainHandItem().getItem() instanceof VacGunItem) {
					VacGunItem.shoot(player, InteractionHand.MAIN_HAND);
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CGunShotPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send() {
		KPacketSender.sendToServer(new CGunShotPacket());
	}
}
