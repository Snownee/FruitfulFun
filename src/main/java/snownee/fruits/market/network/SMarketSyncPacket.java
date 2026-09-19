package snownee.fruits.market.network;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FruitfulFun;
import snownee.fruits.market.MarketBlockEntity;
import snownee.fruits.util.ClientProxy;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SMarketSyncPacket(
		BlockPos pos,
		long money,
		List<ItemStack> orders) implements CustomPacketPayload {
	public static final Type<SMarketSyncPacket> TYPE = new Type<>(FruitfulFun.id("market_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SMarketSyncPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			SMarketSyncPacket::pos,
			ByteBufCodecs.LONG,
			SMarketSyncPacket::money,
			ItemStack.OPTIONAL_LIST_STREAM_CODEC,
			SMarketSyncPacket::orders,
			SMarketSyncPacket::new);

	@Override
	public Type<SMarketSyncPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SMarketSyncPacket> {
		@Override
		public void handle(SMarketSyncPacket packet, PayloadContext context) {
			context.execute(() -> ClientProxy.updateMarket(packet));
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SMarketSyncPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(MarketBlockEntity blockEntity, ServerPlayer player) {
		KPacketSender.send(new SMarketSyncPacket(
				blockEntity.getBlockPos(),
				blockEntity.getMoney(),
				blockEntity.orders()), player);
	}
}
