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
import snownee.fruits.market.MarketCatalog;
import snownee.fruits.util.ClientProxy;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record SMarketCatalogPacket(
		BlockPos pos,
		long money,
		List<ItemStack> orders,
		List<ItemStack> catalog) implements CustomPacketPayload {
	public static final Type<SMarketCatalogPacket> TYPE = new Type<>(FruitfulFun.id("market_catalog"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SMarketCatalogPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			SMarketCatalogPacket::pos,
			ByteBufCodecs.LONG,
			SMarketCatalogPacket::money,
			ItemStack.OPTIONAL_LIST_STREAM_CODEC,
			SMarketCatalogPacket::orders,
			ItemStack.OPTIONAL_LIST_STREAM_CODEC,
			SMarketCatalogPacket::catalog,
			SMarketCatalogPacket::new);

	@Override
	public Type<SMarketCatalogPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<SMarketCatalogPacket> {
		@Override
		public void handle(SMarketCatalogPacket packet, PayloadContext context) {
			context.execute(() -> ClientProxy.openMarketCatalog(packet));
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SMarketCatalogPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(MarketBlockEntity blockEntity, ServerPlayer player) {
		KPacketSender.send(new SMarketCatalogPacket(
				blockEntity.getBlockPos(),
				blockEntity.getMoney(),
				blockEntity.orders(),
				MarketCatalog.compute(player)), player);
	}
}
