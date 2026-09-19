package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
	}

	public RandomSource random() {
		return random;
	}

	public void fillRandom() {
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] == null) {
				cells[i] = randomFruit();
			}
		}
	}

	public void setCell(int index, Piece piece) {
		cells[index] = piece;
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
		steps.add(new BoardStep.Place(new BoardStep.Entry(index, piece.type(), piece.data())));
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
		List<Integer> targets = new ArrayList<>();
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
			targets.add(index);
		}
		List<Integer> removed = damageIce(targets);
		for (int index : removed) {
			Piece piece = Objects.requireNonNull(cells[index]);
			cleared.add(new ClearedPiece(index, piece));
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

	/**
	 * Applies ice damage for one clearing wave and returns the cells that are actually removed.
	 * Surviving ice is replaced with an incremented break count; broken ice joins the result.
	 */
	private List<Integer> damageIce(List<Integer> targets) {
		Set<Integer> involved = new LinkedHashSet<>();
		for (int index : targets) {
			Piece piece = cells[index];
			if (piece != null && piece.is(PieceType.ICE)) {
				involved.add(index);
			}
		}
		for (int index : targets) {
			Piece piece = cells[index];
			if (piece == null || piece.is(PieceType.ICE)) {
				continue;
			}
			int x = index % MinigameConfig.SIZE;
			int y = index / MinigameConfig.SIZE;
			for (int[] offset : ORTHOGONAL) {
				int nx = x + offset[0];
				int ny = y + offset[1];
				if (nx < 0 || nx >= MinigameConfig.SIZE || ny < 0 || ny >= MinigameConfig.SIZE) {
					continue;
				}
				int neighbor = nx + ny * MinigameConfig.SIZE;
				Piece pieceAt = cells[neighbor];
				if (pieceAt != null && pieceAt.is(PieceType.ICE)) {
					involved.add(neighbor);
				}
			}
		}
		List<Integer> result = new ArrayList<>();
		for (int index : targets) {
			Piece piece = cells[index];
			if (piece == null || !piece.is(PieceType.ICE)) {
				result.add(index);
			}
		}
		for (int index : involved) {
			Piece piece = Objects.requireNonNull(cells[index]);
			int breaks = piece.iceBreaks() + 1;
			cells[index] = piece.withBreaks(breaks);
			steps.add(new BoardStep.Ice(index, breaks));
			if (breaks >= MinigameConfig.ICE_BREAKS) {
				result.add(index);
			}
		}
		return result;
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
				List<Integer> targets = new ArrayList<>();
				for (int cell : line) {
					Piece piece = cells[cell];
					if (piece == null || piece.type().passiveImmune()) {
						continue;
					}
					targets.add(cell);
				}
				if (targets.isEmpty()) {
					continue;
				}
				List<Integer> hit = damageIce(targets);
				for (int cell : hit) {
					Piece piece = Objects.requireNonNull(cells[cell]);
					cleared.add(new ClearedPiece(cell, piece));
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
		List<Integer> targets = new ArrayList<>();
		for (int index = 0; index < cells.length; index++) {
			Piece piece = cells[index];
			if (piece != null && piece.is(PieceType.LOOTBOX)) {
				targets.add(index);
			}
		}
		if (targets.isEmpty()) {
			return ClearResult.EMPTY;
		}
		List<Integer> removed = damageIce(targets);
		List<ClearedPiece> cleared = new ArrayList<>();
		for (int index : removed) {
			cleared.add(new ClearedPiece(index, Objects.requireNonNull(cells[index])));
			cells[index] = null;
		}
		steps.add(new BoardStep.Clear(List.copyOf(removed)));
		unlockAdjacent(targets);
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
		steps.clear();
		List<Integer> hit = damageIce(List.of(index));
		List<ClearedPiece> cleared = new ArrayList<>();
		for (int cell : hit) {
			cleared.add(new ClearedPiece(cell, Objects.requireNonNull(cells[cell])));
			cells[cell] = null;
		}
		steps.add(new BoardStep.Clear(List.copyOf(hit)));
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
			if (cells[i] == null && allowed.test(i)) {
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
			for (int x = 0; x < MinigameConfig.SIZE; x++) {
				int index = x + (MinigameConfig.SIZE - 1) * MinigameConfig.SIZE;
				Piece piece = cells[index];
				if (piece != null && piece.type().clearsAtBottom()) {
					bottom.add(index);
				}
			}
			if (bottom.isEmpty()) {
				return;
			}
			List<Integer> hit = damageIce(bottom);
			List<ClearedPiece> wave = new ArrayList<>();
			for (int index : hit) {
				ClearedPiece clearedPiece = new ClearedPiece(index, Objects.requireNonNull(cells[index]));
				cleared.add(clearedPiece);
				wave.add(clearedPiece);
				cells[index] = null;
			}
			steps.add(new BoardStep.Clear(List.copyOf(hit)));
			if (onWave != null) {
				onWave.accept(new ClearResult(
						wave.size() * MinigameConfig.BASE_SCORE,
						List.copyOf(wave),
						ClearResult.Cause.BOTTOM));
			}
			unlockAdjacent(hit);
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
			List<Piece> sources = new ArrayList<>();
			List<Integer> sourceRows = new ArrayList<>();
			List<Boolean> sourceLocked = new ArrayList<>();
			for (int y = MinigameConfig.SIZE - 1; y >= 0; y--) {
				int index = x + y * MinigameConfig.SIZE;
				Piece piece = cells[index];
				if (piece != null && !piece.type().fixed()) {
					sources.add(piece);
					sourceRows.add(y);
					sourceLocked.add(locked[index]);
				}
			}
			int src = 0;
			for (int y = MinigameConfig.SIZE - 1; y >= 0; y--) {
				int index = x + y * MinigameConfig.SIZE;
				Piece piece = cells[index];
				if (piece != null && piece.type().fixed()) {
					fromRows.set(index, y);
					continue;
				}
				if (src < sources.size()) {
					Piece value = sources.get(src);
					int from = sourceRows.get(src);
					cells[index] = value;
					locked[index] = sourceLocked.get(src);
					fromRows.set(index, from);
					if (from != y) {
						moved = true;
					}
					src++;
				} else {
					cells[index] = null;
					locked[index] = false;
				}
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