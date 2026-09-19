package snownee.fruits.minigame.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import snownee.fruits.minigame.FruitBoard;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.Piece;

public record BoardState(byte[] cells, long locked, List<Entry> data) {
	public static final BoardState EMPTY = new BoardState(new byte[MinigameConfig.CELL_COUNT], 0L, List.of());

	public static final StreamCodec<RegistryFriendlyByteBuf, BoardState> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.byteArray(MinigameConfig.CELL_COUNT),
			BoardState::cells,
			ByteBufCodecs.LONG,
			BoardState::locked,
			Entry.STREAM_CODEC.apply(ByteBufCodecs.list(MinigameConfig.CELL_COUNT)),
			BoardState::data,
			BoardState::new);

	public record Entry(int index, CompoundTag data) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Entry::index,
				ByteBufCodecs.COMPOUND_TAG,
				Entry::data,
				Entry::new);
	}

	public static BoardState of(FruitBoard board) {
		@Nullable Piece[] pieces = Objects.requireNonNull(board.cells());
		byte[] cells = new byte[MinigameConfig.CELL_COUNT];
		List<Entry> data = new ArrayList<>();
		for (int i = 0; i < cells.length; i++) {
			Piece piece = Objects.requireNonNull(pieces[i]);
			cells[i] = (byte) piece.type().code();
			if (piece.data() != null) {
				data.add(new Entry(i, piece.data()));
			}
		}
		return new BoardState(cells, board.lockedMask(), data);
	}
}
