package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import org.jspecify.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import snownee.fruits.minigame.ClearResult.ClearedPiece;

public final class FruitBoard implements PathRules.Board {
	private static final int[][] ORTHOGONAL = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
	private static final int BEE_MOVE_MS = 100;

	private final @Nullable Piece[] cells = new @Nullable Piece[MinigameConfig.CELL_COUNT];
	private final boolean[] locked = new boolean[MinigameConfig.CELL_COUNT];
	private final List<BoardStep> steps = new ArrayList<>();
	private final List<Piece> pendingSpawns = new ArrayList<>();
	private final RandomSource random;
	private final WeightedList<PieceType> pool;
	private final boolean allowDiagonal;

	public FruitBoard(RandomSource random, WeightedList<PieceType> pool, boolean allowDiagonal) {
		this.random = random;
		this.pool = pool;
		this.allowDiagonal = allowDiagonal;
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

	@Override
	public boolean allowsDiagonal() {
		return allowDiagonal;
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
		return Piece.of(pool.getRandomOrThrow(random));
	}

	public List<BoardStep> steps() {
		return steps;
	}

	public boolean isValidPath(List<Integer> path) {
		return PathRules.validPath(path, this);
	}

	public @Nullable ClearResult clear(List<Integer> path, Consumer<ClearResult> onWave) {
		if (!isValidPath(path)) {
			return null;
		}
		steps.clear();
		return removeAndRefill(path, path.getLast(), onWave);
	}

	public ClearResult clearCells(List<Integer> indices) {
		return removeAndRefill(indices, null, null);
	}

	/**
	 * @return whether the piece was placed; fails when the cell is occupied
	 */
	public boolean place(int index, Piece piece) {
		if (index < 0 || index >= cells.length || cells[index] != null) {
			return false;
		}
		cells[index] = piece;
		steps.add(new BoardStep.Spawn(List.of(new BoardStep.Entry(index, piece.type(), piece.data()))));
		return true;
	}

	public boolean placeRandom(List<Integer> candidates, Piece piece) {
		List<Integer> free = new ArrayList<>();
		for (int index : candidates) {
			if (index >= 0 && index < cells.length && cells[index] == null) {
				free.add(index);
			}
		}
		if (free.isEmpty()) {
			return false;
		}
		return place(free.get(random.nextInt(free.size())), piece);
	}

	private ClearResult removeAndRefill(
			List<Integer> indices,
			@Nullable Integer beeTarget,
			@Nullable Consumer<ClearResult> onWave) {
		List<ClearedPiece> cleared = new ArrayList<>();
		List<Integer> removed = new ArrayList<>();
		int beeIndex = -1;
		for (int index : indices) {
			Piece piece = Objects.requireNonNull(cells[index]);
			if (piece.is(PieceType.BEE)) {
				beeIndex = index;
				continue;
			}
			if (piece.type().passThrough()) {
				continue;
			}
			cleared.add(new ClearedPiece(index, piece));
			removed.add(index);
			cells[index] = null;
		}
		steps.add(new BoardStep.Clear(List.copyOf(removed)));
		if (beeIndex >= 0 && beeTarget != null) {
			Piece bee = Objects.requireNonNull(cells[beeIndex]);
			cells[beeIndex] = null;
			int duration = (indices.size() - 1) * BEE_MOVE_MS;
			Piece target = cells[beeTarget];
			if (target != null && target.type().passThrough()) {
				CompoundTag data = target.data() == null ? new CompoundTag() : target.data().copy();
				data.putInt(Piece.BEES_KEY, data.getInt(Piece.BEES_KEY).orElse(0) + 1);
				cells[beeTarget] = new Piece(target.type(), data, target.serverData());
				cleared.add(new ClearedPiece(beeIndex, bee));
				steps.add(new BoardStep.BeeMove(List.copyOf(indices), true, data, duration));
			} else {
				cells[beeTarget] = bee;
				steps.add(new BoardStep.BeeMove(List.copyOf(indices), false, null, duration));
			}
		}
		List<ClearedPiece> lineCleared = List.copyOf(cleared);
		applyLargeEffects(cleared);
		if (onWave != null) {
			onWave.accept(new ClearResult(
					lineCleared.size() * MinigameConfig.BASE_SCORE,
					lineCleared,
					ClearResult.Cause.PATH));
		}
		unlockAdjacent(indices);
		applyGravity();
		removeBottom(cleared, onWave);
		refill();
		return new ClearResult(
				cleared.size() * MinigameConfig.BASE_SCORE,
				List.copyOf(cleared),
				ClearResult.Cause.NONE);
	}

	private void applyLargeEffects(List<ClearedPiece> cleared) {
		boolean[] triggered = new boolean[MinigameConfig.CELL_COUNT];
		while (true) {
			List<Integer> pending = new ArrayList<>();
			for (ClearedPiece piece : cleared) {
				if (piece.piece().type().isLarge() && !triggered[piece.index()]) {
					triggered[piece.index()] = true;
					pending.add(piece.index());
				}
			}
			if (pending.isEmpty()) {
				return;
			}
			for (int index : pending) {
				List<Integer> line = new ArrayList<>();
				if (random.nextBoolean()) {
					int y = index / MinigameConfig.SIZE;
					for (int x = 0; x < MinigameConfig.SIZE; x++) {
						line.add(x + y * MinigameConfig.SIZE);
					}
				} else {
					int x = index % MinigameConfig.SIZE;
					for (int y = 0; y < MinigameConfig.SIZE; y++) {
						line.add(x + y * MinigameConfig.SIZE);
					}
				}
				List<Integer> hit = new ArrayList<>();
				for (int cell : line) {
					Piece piece = cells[cell];
					if (piece == null || piece.type().passiveImmune()) {
						continue;
					}
					cleared.add(new ClearedPiece(cell, piece));
					hit.add(cell);
					cells[cell] = null;
				}
				if (!hit.isEmpty()) {
					steps.add(new BoardStep.Clear(List.copyOf(hit)));
					unlockAdjacent(hit);
				}
			}
		}
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
			return ClearResult.EMPTY;
		}
		steps.add(new BoardStep.Clear(List.copyOf(removed)));
		unlockAdjacent(removed);
		applyGravity();
		removeBottom(cleared, null);
		refill();
		return new ClearResult(0, List.copyOf(cleared), ClearResult.Cause.NONE);
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
					if (seen[next] || piece == null || !piece.type().sameFamily(type)) {
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
			if (piece != null
					&& !piece.is(PieceType.BEE)
					&& !piece.type().unlinkable()
					&& !piece.type().passThrough()) {
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
		removeBottom(cleared, null);
		refill();
		return new ClearResult(0, List.copyOf(cleared), ClearResult.Cause.LOOTBOX);
	}

	public void addSpawn(Piece piece, int count) {
		for (int i = 0; i < count; i++) {
			pendingSpawns.add(piece);
		}
	}

	public int pendingCount(PieceType type) {
		int count = 0;
		for (Piece piece : pendingSpawns) {
			if (piece.type().sameFamily(type)) {
				count++;
			}
		}
		return count;
	}

	public int replaceRandom(Piece piece) {
		return replaceRandom(piece, $ -> true);
	}

	public int replaceRandom(Piece piece, IntPredicate allowed) {
		List<Integer> candidates = new ArrayList<>();
		for (int i = 0; i < cells.length; i++) {
			if (allowed.test(i)) {
				candidates.add(i);
			}
		}
		if (candidates.isEmpty()) {
			return -1;
		}
		int index = candidates.get(random.nextInt(candidates.size()));
		cells[index] = piece;
		return index;
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

	private void removeBottom(List<ClearedPiece> cleared, @Nullable Consumer<ClearResult> onWave) {
		while (true) {
			List<Integer> bottom = new ArrayList<>();
			List<ClearedPiece> wave = new ArrayList<>();
			for (int x = 0; x < MinigameConfig.SIZE; x++) {
				int index = x + (MinigameConfig.SIZE - 1) * MinigameConfig.SIZE;
				Piece piece = cells[index];
				if (piece != null && piece.type().clearsAtBottom()) {
					ClearedPiece clearedPiece = new ClearedPiece(index, piece);
					cleared.add(clearedPiece);
					wave.add(clearedPiece);
					cells[index] = null;
					bottom.add(index);
				}
			}
			if (bottom.isEmpty()) {
				return;
			}
			steps.add(new BoardStep.Clear(bottom));
			if (onWave != null) {
				onWave.accept(new ClearResult(
						wave.size() * MinigameConfig.BASE_SCORE,
						List.copyOf(wave),
						ClearResult.Cause.BOTTOM));
			}
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