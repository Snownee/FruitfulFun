package snownee.fruits.minigame;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.Nullable;

public final class PathRules {
	public static final int MIN_LENGTH = 3;
	public static final int MAX_PATH = computeMaxPath();

	private PathRules() {
	}

	private static int computeMaxPath() {
		int edges = 0;
		for (int a = 0; a < MinigameConfig.CELL_COUNT; a++) {
			for (int b = a + 1; b < MinigameConfig.CELL_COUNT; b++) {
				if (adjacent(a, b)) {
					edges++;
				}
			}
		}
		return edges + 1;
	}

	public interface Board {
		@Nullable Piece piece(int index);

		boolean locked(int index);
	}

	public static boolean validPath(List<Integer> path, Board board) {
		return path.size() >= MIN_LENGTH && validPrefixLength(path, board) == path.size() && hasClearable(path, board);
	}

	private static boolean hasClearable(List<Integer> path, Board board) {
		for (int index : path) {
			PieceType type = Objects.requireNonNull(board.piece(index)).type();
			if (type != PieceType.BEE && !type.passThrough()) {
				return true;
			}
		}
		return false;
	}

	public static int validPrefixLength(List<Integer> path, Board board) {
		boolean[] seen = new boolean[MinigameConfig.CELL_COUNT];
		Set<Long> edges = new HashSet<>();
		boolean beeRequired = hasBee(board);
		PieceType base = null;
		int prev = -1;
		int limit = Math.min(path.size(), MAX_PATH);
		for (int i = 0; i < limit; i++) {
			int index = path.get(i);
			if (index < 0 || index >= MinigameConfig.CELL_COUNT || board.locked(index)) {
				return i;
			}
			PieceType value = Objects.requireNonNull(board.piece(index)).type();
			if (value == PieceType.BEE) {
				if (i > 0) {
					return i;
				}
			} else if (i == 0 && beeRequired) {
				return 0;
			}
			if (value.passThrough() && !beeRequired) {
				return i;
			}
			if (!value.passThrough() && seen[index]) {
				return i;
			}
			if (!value.matches(base)) {
				return i;
			}
			if (prev >= 0) {
				if (!adjacent(prev, index)) {
					return i;
				}
				if (!edges.add(edgeKey(prev, index))) {
					return i;
				}
			}
			seen[index] = true;
			if (!value.wildcard() && !value.passThrough()) {
				base = value;
			}
			prev = index;
		}
		return limit;
	}

	public static boolean canAppend(List<Integer> path, Board board, int cell) {
		if (cell < 0 || cell >= MinigameConfig.CELL_COUNT || board.locked(cell)) {
			return false;
		}
		PieceType value = Objects.requireNonNull(board.piece(cell)).type();
		boolean beeRequired = hasBee(board);
		if (path.isEmpty()) {
			return !value.unlinkable() && !value.passThrough() && (value == PieceType.BEE || !beeRequired);
		}
		if (path.size() >= MAX_PATH || value == PieceType.BEE || (value.passThrough() && !beeRequired)) {
			return false;
		}
		if (!value.passThrough() && path.contains(cell)) {
			return false;
		}
		if (!adjacent(path.getLast(), cell) || hasEdge(path, path.getLast(), cell)) {
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
			if (!type.wildcard() && !type.passThrough()) {
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

	public static int distance(int a, int b) {
		int ax = a % MinigameConfig.SIZE;
		int ay = a / MinigameConfig.SIZE;
		int bx = b % MinigameConfig.SIZE;
		int by = b / MinigameConfig.SIZE;
		return Math.abs(ax - bx) + Math.abs(ay - by);
	}

	private static boolean hasEdge(List<Integer> path, int a, int b) {
		long key = edgeKey(a, b);
		for (int i = 1; i < path.size(); i++) {
			if (edgeKey(path.get(i - 1), path.get(i)) == key) {
				return true;
			}
		}
		return false;
	}

	private static long edgeKey(int a, int b) {
		int lo = Math.min(a, b);
		int hi = Math.max(a, b);
		return (long) lo * MinigameConfig.CELL_COUNT + hi;
	}
}
