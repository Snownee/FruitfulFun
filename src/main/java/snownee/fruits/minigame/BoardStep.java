package snownee.fruits.minigame;

import java.util.List;

import org.jspecify.annotations.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public sealed interface BoardStep {
	record Clear(List<Integer> cells) implements BoardStep {
	}

	record Fall(List<Integer> fromRows) implements BoardStep {
	}

	record Spawn(List<Entry> entries) implements BoardStep {
	}

	record Place(Entry entry) implements BoardStep {
	}

	record Move(int from, int to, int duration) implements BoardStep {
	}

	record BeeMove(List<Integer> cells, boolean consumed, @Nullable CompoundTag hiveData, int duration) implements BoardStep {
	}

	record Ice(int index, int breaks) implements BoardStep {
	}

	record Entry(int index, PieceType type, @Nullable CompoundTag data) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public Entry decode(RegistryFriendlyByteBuf buf) {
				int index = buf.readVarInt();
				PieceType type = PieceType.byCode(buf.readVarInt());
				CompoundTag data = buf.readBoolean() ? ByteBufCodecs.COMPOUND_TAG.decode(buf) : null;
				return new Entry(index, type, data);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, Entry entry) {
				buf.writeVarInt(entry.index());
				buf.writeVarInt(entry.type().code());
				buf.writeBoolean(entry.data() != null);
				if (entry.data() != null) {
					ByteBufCodecs.COMPOUND_TAG.encode(buf, entry.data());
				}
			}
		};
	}

	StreamCodec<ByteBuf, List<Integer>> INT_LIST = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(MinigameConfig.CELL_COUNT));
	StreamCodec<ByteBuf, List<Integer>> PATH_LIST = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(PathRules.MAX_PATH));
	StreamCodec<RegistryFriendlyByteBuf, List<Entry>> ENTRY_LIST = Entry.STREAM_CODEC.apply(
			ByteBufCodecs.list(MinigameConfig.CELL_COUNT));

	StreamCodec<RegistryFriendlyByteBuf, BoardStep> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public BoardStep decode(RegistryFriendlyByteBuf buf) {
			return switch (buf.readVarInt()) {
				case 0 -> new Clear(INT_LIST.decode(buf));
				case 1 -> new Fall(INT_LIST.decode(buf));
				case 2 -> new Spawn(ENTRY_LIST.decode(buf));
				case 3 -> new Move(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
				case 4 -> new BeeMove(
						PATH_LIST.decode(buf),
						buf.readBoolean(),
						buf.readBoolean() ? ByteBufCodecs.COMPOUND_TAG.decode(buf) : null,
						buf.readVarInt());
				case 5 -> new Ice(buf.readVarInt(), buf.readVarInt());
				case 6 -> new Place(Entry.STREAM_CODEC.decode(buf));
				default -> throw new IllegalArgumentException("Unknown board step");
			};
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, BoardStep step) {
			switch (step) {
				case Clear clear -> {
					buf.writeVarInt(0);
					INT_LIST.encode(buf, clear.cells());
				}
				case Fall fall -> {
					buf.writeVarInt(1);
					INT_LIST.encode(buf, fall.fromRows());
				}
				case Spawn spawn -> {
					buf.writeVarInt(2);
					ENTRY_LIST.encode(buf, spawn.entries());
				}
				case Move move -> {
					buf.writeVarInt(3);
					buf.writeVarInt(move.from());
					buf.writeVarInt(move.to());
					buf.writeVarInt(move.duration());
				}
				case BeeMove beeMove -> {
					buf.writeVarInt(4);
					PATH_LIST.encode(buf, beeMove.cells());
					buf.writeBoolean(beeMove.consumed());
					buf.writeBoolean(beeMove.hiveData() != null);
					if (beeMove.hiveData() != null) {
						ByteBufCodecs.COMPOUND_TAG.encode(buf, beeMove.hiveData());
					}
					buf.writeVarInt(beeMove.duration());
				}
				case Ice ice -> {
					buf.writeVarInt(5);
					buf.writeVarInt(ice.index());
					buf.writeVarInt(ice.breaks());
				}
				case Place place -> {
					buf.writeVarInt(6);
					Entry.STREAM_CODEC.encode(buf, place.entry());
				}
			}
		}
	};
}
