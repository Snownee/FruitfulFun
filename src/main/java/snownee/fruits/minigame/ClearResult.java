package snownee.fruits.minigame;

import java.util.List;

import org.jspecify.annotations.Nullable;

public record ClearResult(int score, List<ClearedPiece> pieces) {
	public record ClearedPiece(int index, Piece piece) {
	}

	public int size() {
		return pieces.size();
	}

	public int count(@Nullable PieceType type) {
		int count = 0;
		for (ClearedPiece piece : pieces) {
			if (type == null || piece.piece().is(type)) {
				count++;
			}
		}
		return count;
	}
}
