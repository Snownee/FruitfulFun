package snownee.fruits.minigame;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

public final class PathRules {
	public static final int MIN_LENGTH = 3;

	private PathRules() {
	}

	public interface Board {
		@Nullable Piece piece(int index);

		boolean locked(int index);
	}

	public static boolean validPath(List<Integer> path, Board board) {
		return path.size() >= MIN_LENGTH && validPrefixLength(path, board) == path.size();
	}

	public static int validPrefixLength(List<Integer> path, Board board) {
		boolean[] seen = new boolean[MinigameConfig.CELL_COUNT];
		boolean beeRequired = hasBee(board);
		PieceType base = null;
		int prev = -1;
		for (int i = 0; i < path.size(); i++) {
			int index = path.get(i);
			if (index < 0 || index >= MinigameConfig.CELL_COUNT || seen[index] || board.locked(index)) {
				return i;
			}
			seen[index] = true;
			PieceType value = Objects.requireNonNull(board.piece(index)).type();
			if (value == PieceType.BEE) {
				if (i > 0) {
					return i;
				}
			} else if (i == 0 && beeRequired) {
				return 0;
			}
			if (!value.matches(base)) {
				return i;
			}
			if (prev >= 0 && !adjacent(prev, index)) {
				return i;
			}
			if (!value.wildcard()) {
				base = value;
			}
			prev = index;
		}
		return path.size();
	}

	public static boolean canAppend(List<Integer> path, Board board, int cell) {
		if (cell < 0 || cell >= MinigameConfig.CELL_COUNT || path.contains(cell) || board.locked(cell)) {
			return false;
		}
		PieceType value = Objects.requireNonNull(board.piece(cell)).type();
		if (path.isEmpty()) {
			return !value.unlinkable() && (value == PieceType.BEE || !hasBee(board));
		}
		if (value == PieceType.BEE) {
			return false;
		}
		if (!adjacent(path.getLast(), cell)) {
			return false;
		}
		return value.matches(baseType(path, board));
	}

	public static boolean hasBee(Board board) {
		for (int i = 0; i < MinigameConfig.CELL_COUNT; i++) {
			Piece piece = board.piece(i);
			if (piece != null && piece.is(PieceType.BEE)) {
				return true;
			}
		}
		return false;
	}

	public static @Nullable PieceType baseType(List<Integer> path, Board board) {
		for (int index : path) {
			PieceType type = Objects.requireNonNull(board.piece(index)).type();
			if (!type.wildcard()) {
				return type;
			}
		}
		return null;
	}

	public static boolean adjacent(int a, int b) {
		int ax = a % MinigameConfig.SIZE;
		int ay = a / MinigameConfig.SIZE;
		int bx = b % MinigameConfig.SIZE;
		int by = b / MinigameConfig.SIZE;
		return Math.abs(ax - bx) <= 1 && Math.abs(ay - by) <= 1 && (ax != bx || ay != by);
	}
}
