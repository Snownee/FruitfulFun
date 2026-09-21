package snownee.fruits.market.network;

import java.util.List;
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
public record CSetOrderPacket(List<Change> changes) implements CustomPacketPayload {
	public static final Type<CSetOrderPacket> TYPE = new Type<>(FruitfulFun.id("market_set_order"));
	public static final StreamCodec<RegistryFriendlyByteBuf, Change> CHANGE_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			Change::slot,
			ItemStack.OPTIONAL_STREAM_CODEC,
			Change::order,
			Change::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CSetOrderPacket> STREAM_CODEC = CHANGE_CODEC
			.apply(ByteBufCodecs.list())
			.map(CSetOrderPacket::new, CSetOrderPacket::changes);

	public record Change(int slot, ItemStack order) {
	}

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
				boolean dirty = false;
				for (Change change : packet.changes()) {
					int slot = change.slot();
					if (slot < 0 || slot >= MarketMenu.MARKET_SLOTS) {
						continue;
					}
					ItemStack order = change.order();
					if (order.isEmpty()) {
						blockEntity.setOrderSilent(slot, ItemStack.EMPTY);
						dirty = true;
						continue;
					}
					if (!MarketCatalog.isUnlocked(player, order)) {
						continue;
					}
					blockEntity.setOrderSilent(slot, order.copyWithCount(Mth.clamp(order.getCount(), 1, order.getMaxStackSize())));
					dirty = true;
				}
				if (dirty) {
					blockEntity.changed();
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CSetOrderPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}

	public static void send(int slot, ItemStack order) {
		send(List.of(new Change(slot, order)));
	}

	public static void send(List<Change> changes) {
		KPacketSender.sendToServer(new CSetOrderPacket(List.copyOf(changes)));
	}
}
