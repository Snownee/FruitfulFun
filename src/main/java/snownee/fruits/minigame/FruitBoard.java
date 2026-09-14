package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import net.minecraft.util.RandomSource;
import snownee.fruits.minigame.ClearResult.ClearedPiece;

public final class FruitBoard implements PathRules.Board {
	private static final int[][] ORTHOGONAL = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

	private final @Nullable Piece[] cells = new @Nullable Piece[MinigameConfig.CELL_COUNT];
	private final boolean[] locked = new boolean[MinigameConfig.CELL_COUNT];
	private final List<BoardStep> steps = new ArrayList<>();
	private final List<Piece> pendingSpawns = new ArrayList<>();
	private final RandomSource random;

	public FruitBoard(RandomSource random) {
		this.random = random;
		for (int i = 0; i < cells.length; i++) {
			cells[i] = randomFruit();
		}
	}

	public @Nullable Piece[] cells() {
		return cells;
	}

	@Override
	public @Nullable Piece piece(int index) {
		return cells[index];
	}

	@Override
	public boolean locked(int index) {
		return locked[index];
	}

	public long lockedMask() {
		long mask = 0L;
		for (int i = 0; i < locked.length; i++) {
			if (locked[i]) {
				mask |= 1L << i;
			}
		}
		return mask;
	}

	private Piece randomFruit() {
		return Piece.of(PieceType.FRUITS.getRandomOrThrow(random));
	}

	public List<BoardStep> steps() {
		return steps;
	}

	public boolean isValidPath(List<Integer> path) {
		return PathRules.validPath(path, this);
	}

	public @Nullable ClearResult clear(List<Integer> path) {
		if (!isValidPath(path)) {
			return null;
		}
		steps.clear();
		return removeAndRefill(path, path.getLast());
	}

	public ClearResult clearCells(List<Integer> indices) {
		return removeAndRefill(indices, null);
	}

	private ClearResult removeAndRefill(List<Integer> indices, @Nullable Integer beeTarget) {
		List<ClearedPiece> cleared = new ArrayList<>();
		List<Integer> removed = new ArrayList<>();
		int beeIndex = -1;
		for (int index : indices) {
			Piece piece = Objects.requireNonNull(cells[index]);
			if (piece.is(PieceType.BEE)) {
				beeIndex = index;
				continue;
			}
			cleared.add(new ClearedPiece(index, piece));
			removed.add(index);
			cells[index] = null;
		}
		steps.add(new BoardStep.Clear(List.copyOf(removed)));
		if (beeIndex >= 0 && beeTarget != null) {
			Piece bee = Objects.requireNonNull(cells[beeIndex]);
			cells[beeIndex] = cells[beeTarget];
			cells[beeTarget] = bee;
			steps.add(new BoardStep.Move(beeIndex, beeTarget));
		}
		unlockAdjacent(indices);
		applyGravity();
		removeBottom(cleared);
		refill();
		return new ClearResult(cleared.size() * MinigameConfig.BASE_SCORE, List.copyOf(cleared));
	}

	public ClearResult clearLootboxes() {
		List<ClearedPiece> cleared = new ArrayList<>();
		List<Integer> removed = new ArrayList<>();
		for (int index = 0; index < cells.length; index++) {
			Piece piece = cells[index];
			if (piece != null && piece.is(PieceType.LOOTBOX)) {
				cleared.add(new ClearedPiece(index, piece));
				removed.add(index);
				cells[index] = null;
			}
		}
		if (removed.isEmpty()) {
			return new ClearResult(0, List.of());
		}
		steps.add(new BoardStep.Clear(List.copyOf(removed)));
		unlockAdjacent(removed);
		applyGravity();
		removeBottom(cleared);
		refill();
		return new ClearResult(0, List.copyOf(cleared));
	}

	public List<Integer> findGroup(int index) {
		PieceType type = Objects.requireNonNull(cells[index]).type();
		boolean[] seen = new boolean[cells.length];
		List<Integer> group = new ArrayList<>();
		group.add(index);
		seen[index] = true;
		for (int i = 0; i < group.size(); i++) {
			int current = group.get(i);
			int x = current % MinigameConfig.SIZE;
			int y = current / MinigameConfig.SIZE;
			for (int dy = -1; dy <= 1; dy++) {
				for (int dx = -1; dx <= 1; dx++) {
					if (dx == 0 && dy == 0) {
						continue;
					}
					int nx = x + dx;
					int ny = y + dy;
					if (nx < 0 || nx >= MinigameConfig.SIZE || ny < 0 || ny >= MinigameConfig.SIZE) {
						continue;
					}
					int next = nx + ny * MinigameConfig.SIZE;
					Piece piece = cells[next];
					if (seen[next] || piece == null || !piece.is(type)) {
						continue;
					}
					seen[next] = true;
					group.add(next);
				}
			}
		}
		return group;
	}

	public int randomSeed() {
		List<Integer> candidates = new ArrayList<>();
		for (int i = 0; i < cells.length; i++) {
			Piece piece = cells[i];
			if (piece != null && !piece.is(PieceType.BEE) && !piece.type().unlinkable()) {
				candidates.add(i);
			}
		}
		if (candidates.isEmpty()) {
			return -1;
		}
		return candidates.get(random.nextInt(candidates.size()));
	}

	public @Nullable ClearResult clearLootbox(int index) {
		if (index < 0 || index >= cells.length || cells[index] == null || !cells[index].is(PieceType.LOOTBOX)) {
			return null;
		}
		List<ClearedPiece> cleared = new ArrayList<>();
		cleared.add(new ClearedPiece(index, Objects.requireNonNull(cells[index])));
		steps.clear();
		cells[index] = null;
		steps.add(new BoardStep.Clear(List.of(index)));
		unlockAdjacent(List.of(index));
		applyGravity();
		removeBottom(cleared);
		refill();
		return new ClearResult(0, List.copyOf(cleared));
	}

	public void addSpawn(Piece piece, int count) {
		for (int i = 0; i < count; i++) {
			pendingSpawns.add(piece);
		}
	}

	public void replaceRandom(Piece piece) {
		cells[random.nextInt(cells.length)] = piece;
	}

	public int countRemovals(List<Integer> path, PieceType type) {
		int count = 0;
		boolean[] removed = new boolean[MinigameConfig.CELL_COUNT];
		for (int index : path) {
			removed[index] = true;
			Piece piece = Objects.requireNonNull(cells[index]);
			if (piece.is(type) && !piece.is(PieceType.BEE)) {
				count++;
			}
		}
		if (type.clearsAtBottom()) {
			for (int x = 0; x < MinigameConfig.SIZE; x++) {
				for (int y = MinigameConfig.SIZE - 1; y >= 0; y--) {
					int index = x + y * MinigameConfig.SIZE;
					if (removed[index]) {
						continue;
					}
					Piece piece = cells[index];
					if (piece == null || !piece.is(type)) {
						break;
					}
					count++;
				}
			}
		}
		return count;
	}

	public void addJunk(List<Integer> indices) {
		for (int index : indices) {
			if (!cells[index].type().unlinkable()) {
				locked[index] = true;
			}
		}
	}

	private void unlockAdjacent(List<Integer> removedCells) {
		for (int index : removedCells) {
			int x = index % MinigameConfig.SIZE;
			int y = index / MinigameConfig.SIZE;
			for (int[] offset : ORTHOGONAL) {
				int nx = x + offset[0];
				int ny = y + offset[1];
				if (nx < 0 || nx >= MinigameConfig.SIZE || ny < 0 || ny >= MinigameConfig.SIZE) {
					continue;
				}
				locked[nx + ny * MinigameConfig.SIZE] = false;
			}
		}
	}

	private void removeBottom(List<ClearedPiece> cleared) {
		while (true) {
			List<Integer> bottom = new ArrayList<>();
			for (int x = 0; x < MinigameConfig.SIZE; x++) {
				int index = x + (MinigameConfig.SIZE - 1) * MinigameConfig.SIZE;
				Piece piece = cells[index];
				if (piece != null && piece.type().clearsAtBottom()) {
					cleared.add(new ClearedPiece(index, piece));
					cells[index] = null;
					bottom.add(index);
				}
			}
			if (bottom.isEmpty()) {
				return;
			}
			steps.add(new BoardStep.Clear(bottom));
			unlockAdjacent(bottom);
			applyGravity();
		}
	}

	private void applyGravity() {
		List<Integer> fromRows = new ArrayList<>();
		for (int i = 0; i < MinigameConfig.CELL_COUNT; i++) {
			fromRows.add(-1);
		}
		boolean moved = false;
		for (int x = 0; x < MinigameConfig.SIZE; x++) {
			int writeY = MinigameConfig.SIZE - 1;
			for (int y = MinigameConfig.SIZE - 1; y >= 0; y--) {
				int from = x + y * MinigameConfig.SIZE;
				Piece value = cells[from];
				if (value != null) {
					int to = x + writeY * MinigameConfig.SIZE;
					cells[to] = value;
					locked[to] = locked[from];
					fromRows.set(to, y);
					if (to != from) {
						cells[from] = null;
						locked[from] = false;
						moved = true;
					}
					writeY--;
				}
			}
			for (int y = writeY; y >= 0; y--) {
				int index = x + y * MinigameConfig.SIZE;
				cells[index] = null;
				locked[index] = false;
			}
		}
		if (moved) {
			steps.add(new BoardStep.Fall(fromRows));
			refill();
		}
	}

	private void refill() {
		List<Integer> empty = new ArrayList<>();
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] == null) {
				empty.add(i);
			}
		}
		if (empty.isEmpty()) {
			return;
		}
		List<BoardStep.Entry> entries = new ArrayList<>();
		for (int index : empty) {
			Piece piece = randomFruit();
			cells[index] = piece;
			entries.add(new BoardStep.Entry(index, piece.type(), piece.data()));
		}
		if (!pendingSpawns.isEmpty()) {
			List<Integer> slots = new ArrayList<>(empty);
			for (Piece piece : pendingSpawns) {
				if (slots.isEmpty()) {
					break;
				}
				int index = slots.remove(random.nextInt(slots.size()));
				cells[index] = piece;
				entries.replaceAll(entry -> entry.index() == index
						? new BoardStep.Entry(index, piece.type(), piece.data())
						: entry);
			}
			pendingSpawns.clear();
		}
		steps.add(new BoardStep.Spawn(entries));
	}
}