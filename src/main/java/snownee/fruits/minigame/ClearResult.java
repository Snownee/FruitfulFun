package snownee.fruits.minigame;

import java.util.List;

import org.jspecify.annotations.Nullable;

public record ClearResult(int score, List<ClearedPiece> pieces, Cause cause) {
	public static final ClearResult EMPTY = new ClearResult(0, List.of(), Cause.NONE);

	public enum Cause {
		PATH,
		BOTTOM,
		LOOTBOX,
		NONE
	}

	public record ClearedPiece(int index, Piece piece) {
	}

	public int size() {
		return pieces.size();
	}

	public int count(@Nullable PieceType type) {
		int count = 0;
		for (ClearedPiece piece : pieces) {
			if (type == null || piece.piece().type().sameFamily(type)) {
				count++;
			}
		}
		return count;
	}

	public int countLarge() {
		int count = 0;
		for (ClearedPiece piece : pieces) {
			if (piece.piece().type().isLarge()) {
				count++;
			}
		}
		return count;
	}
}
