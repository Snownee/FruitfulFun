package snownee.fruits.market.network;

import java.util.Objects;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FruitfulFun;
import snownee.fruits.market.MarketBlockEntity;
import snownee.fruits.market.MarketCatalog;
import snownee.fruits.market.MarketMenu;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CSetOrderPacket(int slot, ItemStack order) implements CustomPacketPayload {
	public static final Type<CSetOrderPacket> TYPE = new Type<>(FruitfulFun.id("market_set_order"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CSetOrderPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			CSetOrderPacket::slot,
			ItemStack.OPTIONAL_STREAM_CODEC,
			CSetOrderPacket::order,
			CSetOrderPacket::new);

	@Override
	public Type<CSetOrderPacket> type() {
		return TYPE;
	}

	public static class Handler implements PlayPacketHandler<CSetOrderPacket> {
		@Override
		public void handle(CSetOrderPacket packet, PayloadContext context) {
			context.execute(() -> {
				ServerPlayer player = Objects.requireNonNull(context.serverPlayer());
				if (!(player.containerMenu instanceof MarketMenu menu)) {
					return;
				}
				MarketBlockEntity blockEntity = menu.blockEntity();
				if (blockEntity == null || !menu.stillValid(player)) {
					return;
				}
				int slot = packet.slot();
				if (slot < 0 || slot >= MarketMenu.MARKET_SLOTS) {
					return;
				}
				ItemStack order = packet.order();
				if (order.isEmpty()) {
					blockEntity.setOrder(slot, ItemStack.EMPTY);
					return;
				}
				if (!MarketCatalog.isUnlocked(player, order)) {
					return;
				}
				blockEntity.setOrder(slot, order.copyWithCount(Mth.clamp(order.getCount(), 1, order.getMaxStackSize())));
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CSetOrderPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(int slot, ItemStack order) {
		KPacketSender.sendToServer(new CSetOrderPacket(slot, order));
	}
}
