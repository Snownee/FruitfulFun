package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.minigame.network.BoardState;

public final class BoardView implements PathRules.Board {
	private static final long CLEAR_MS = 140;
	private static final long FALL_MS = 240;
	private static final long MOVE_MS = 240;
	private static final long LOCK_MS = 200;
	private static final ItemStack BARRIER = Items.BARRIER.getDefaultInstance();

	private record Stage(ItemStack[] stacks, float @Nullable [] fromCol, float @Nullable [] fromRow, List<Integer> clear, long duration) {
	}

	private record Timeline(List<Stage> stages, Piece[] result) {
	}

	private final boolean animated;
	private Piece[] pieces = new Piece[MinigameConfig.CELL_COUNT];
	private ItemStack[] stacks = new ItemStack[MinigameConfig.CELL_COUNT];
	private boolean[] locked = new boolean[MinigameConfig.CELL_COUNT];
	private List<Stage> stages = List.of();
	private long animStart;
	private long animDuration;
	private boolean animating;
	private final Set<Integer> locking = new HashSet<>();
	private final Set<Integer> unlocking = new HashSet<>();
	private long lockAnimStart;

	private int x;
	private int y;
	private int cell;

	public BoardView(boolean animated) {
		this.animated = animated;
	}

	public void layout(int x, int y, int cell) {
		this.x = x;
		this.y = y;
		this.cell = cell;
	}

	public int gridSize() {
		return MinigameConfig.SIZE * cell;
	}

	public int centerX() {
		return x + gridSize() / 2;
	}

	public boolean busy() {
		return animating && Util.getMillis() - animStart < animDuration;
	}

	@Override
	public Piece piece(int index) {
		return pieces[index];
	}

	@Override
	public boolean locked(int index) {
		return locked[index];
	}

	public int cellAt(double mouseX, double mouseY, int margin) {
		double rx = mouseX - x;
		double ry = mouseY - y;
		int size = gridSize();
		if (rx < 0 || ry < 0 || rx >= size || ry >= size) {
			return -1;
		}
		int cx = (int) (rx / cell);
		int cy = (int) (ry / cell);
		double ox = rx - cx * cell;
		double oy = ry - cy * cell;
		if (ox < margin || oy < margin || ox >= cell - margin || oy >= cell - margin) {
			return -1;
		}
		return cx + cy * MinigameConfig.SIZE;
	}

	public void applySync(
			@Nullable BoardState state,
			List<BoardStep> steps,
			long lockedMask,
			boolean animateClear) {
		boolean[] newLocked = decodeLocked(lockedMask);
		if (animated) {
			locking.clear();
			unlocking.clear();
			for (int i = 0; i < newLocked.length; i++) {
				if (newLocked[i] && !locked[i]) {
					locking.add(i);
				} else if (!newLocked[i] && locked[i]) {
					unlocking.add(i);
				}
			}
			if (!locking.isEmpty() || !unlocking.isEmpty()) {
				lockAnimStart = Util.getMillis();
			}
		}
		if (animateClear && !steps.isEmpty()) {
			Timeline timeline = buildTimeline(pieces, steps);
			stages = timeline.stages();
			animDuration = 0;
			for (Stage stage : stages) {
				animDuration += stage.duration();
			}
			animStart = Util.getMillis();
			animating = true;
			if (state == null) {
				pieces = timeline.result();
			}
		} else if (!animating) {
			stages = List.of();
		}
		if (state != null) {
			pieces = decodeCells(state);
		}
		stacks = stacksOf(pieces);
		locked = newLocked;
	}

	private static Timeline buildTimeline(Piece[] prev, List<BoardStep> steps) {
		List<Stage> stages = new ArrayList<>();
		@Nullable Piece[] current = prev.clone();
		for (int i = 0; i < steps.size(); i++) {
			BoardStep step = steps.get(i);
			if (step instanceof BoardStep.Clear clear) {
				stages.add(new Stage(stacksOf(current), null, null, clear.cells(), CLEAR_MS));
				@Nullable Piece[] next = current.clone();
				for (int cell : clear.cells()) {
					next[cell] = null;
				}
				current = next;
			} else if (step instanceof BoardStep.Fall fall) {
				@Nullable Piece[] next = applyFall(current, fall.fromRows());
				if (i + 1 < steps.size() && steps.get(i + 1) instanceof BoardStep.Spawn spawn) {
					next = applySpawn(next, spawn.entries());
					float[] from = spawnSources(spawn.entries());
					for (int cell = 0; cell < from.length; cell++) {
						if (fall.fromRows().get(cell) >= 0) {
							from[cell] = fall.fromRows().get(cell);
						}
					}
					stages.add(new Stage(stacksOf(next), null, from, List.of(), FALL_MS));
					i++;
				} else {
					stages.add(new Stage(stacksOf(next), null, toFloats(fall.fromRows()), List.of(), FALL_MS));
				}
				current = next;
			} else if (step instanceof BoardStep.Spawn spawn) {
				@Nullable Piece[] next = applySpawn(current, spawn.entries());
				stages.add(new Stage(stacksOf(next), null, spawnSources(spawn.entries()), List.of(), FALL_MS));
				current = next;
			} else if (step instanceof BoardStep.Move move) {
				@Nullable Piece[] next = current.clone();
				next[move.to()] = next[move.from()];
				next[move.from()] = current[move.to()];
				float[] fromCol = new float[MinigameConfig.CELL_COUNT];
				float[] fromRow = new float[MinigameConfig.CELL_COUNT];
				for (int cell = 0; cell < fromCol.length; cell++) {
					fromCol[cell] = cell % MinigameConfig.SIZE;
					fromRow[cell] = cell / MinigameConfig.SIZE;
				}
				fromCol[move.from()] = move.to() % MinigameConfig.SIZE;
				fromRow[move.from()] = move.to() / MinigameConfig.SIZE;
				fromCol[move.to()] = move.from() % MinigameConfig.SIZE;
				fromRow[move.to()] = move.from() / MinigameConfig.SIZE;
				stages.add(new Stage(stacksOf(next), fromCol, fromRow, List.of(), MOVE_MS));
				current = next;
			}
		}
		Piece[] board = new Piece[current.length];
		for (int i = 0; i < current.length; i++) {
			board[i] = Objects.requireNonNull(current[i]);
		}
		return new Timeline(stages, board);
	}

	private static @Nullable Piece[] applySpawn(@Nullable Piece[] grid, List<BoardStep.Entry> entries) {
		@Nullable Piece[] next = grid.clone();
		for (BoardStep.Entry entry : entries) {
			next[entry.index()] = new Piece(entry.type(), entry.data(), null);
		}
		return next;
	}

	private static @Nullable Piece[] applyFall(@Nullable Piece[] current, List<Integer> fromRows) {
		@Nullable Piece[] result = new Piece[MinigameConfig.CELL_COUNT];
		for (int cell = 0; cell < MinigameConfig.CELL_COUNT; cell++) {
			int from = fromRows.get(cell);
			if (from >= 0) {
				result[cell] = current[cell % MinigameConfig.SIZE + from * MinigameConfig.SIZE];
			}
		}
		return result;
	}

	private static float[] spawnSources(List<BoardStep.Entry> entries) {
		boolean[] spawned = new boolean[MinigameConfig.CELL_COUNT];
		for (BoardStep.Entry entry : entries) {
			spawned[entry.index()] = true;
		}
		float[] result = new float[MinigameConfig.CELL_COUNT];
		for (int col = 0; col < MinigameConfig.SIZE; col++) {
			int count = 0;
			for (int row = 0; row < MinigameConfig.SIZE; row++) {
				if (spawned[col + row * MinigameConfig.SIZE]) {
					count++;
				}
			}
			for (int row = 0; row < MinigameConfig.SIZE; row++) {
				int cell = col + row * MinigameConfig.SIZE;
				result[cell] = spawned[cell] ? row - count : row;
			}
		}
		return result;
	}

	private static float[] toFloats(List<Integer> values) {
		float[] result = new float[MinigameConfig.CELL_COUNT];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}

	public void render(GuiGraphicsExtractor graphics, Collection<Integer> highlight) {
		graphics.enableScissor(x, y, x + gridSize(), y + gridSize());
		long now = Util.getMillis();
		float lockT = Math.min(1f, (now - lockAnimStart) / (float) LOCK_MS);
		if (lockT >= 1f) {
			locking.clear();
			unlocking.clear();
		}
		if (animating) {
			long elapsed = now - animStart;
			if (elapsed < animDuration) {
				renderStages(graphics, elapsed, lockT);
			} else {
				animating = false;
				renderStatic(graphics, lockT, highlight);
			}
		} else {
			renderStatic(graphics, lockT, highlight);
		}
		graphics.disableScissor();
	}

	private void renderStages(GuiGraphicsExtractor graphics, long elapsed, float lockT) {
		renderBackground(graphics);
		long acc = 0;
		for (Stage stage : stages) {
			if (elapsed < acc + stage.duration()) {
				float t = (elapsed - acc) / (float) stage.duration();
				if (stage.fromRow() == null) {
					renderClearStage(graphics, stage, t, lockT);
				} else {
					renderFallStage(graphics, stage, t, lockT);
				}
				return;
			}
			acc += stage.duration();
		}
	}

	private void renderStatic(GuiGraphicsExtractor graphics, float lockT, Collection<Integer> highlight) {
		renderBackground(graphics);
		for (int i = 0; i < MinigameConfig.CELL_COUNT; i++) {
			drawCell(graphics, stacks[i], cellX(i), cellY(i), tileScale(), lockVisual(i, lockT));
		}
		for (int index : highlight) {
			int px = cellX(index);
			int py = cellY(index);
			graphics.fill(px + 1, py + 1, px + cell - 1, py + cell - 1, 0x66FFD700);
			graphics.outline(px, py, cell, cell, 0xFFFFD700);
		}
	}

	private void renderClearStage(GuiGraphicsExtractor graphics, Stage stage, float t, float lockT) {
		boolean[] clear = new boolean[MinigameConfig.CELL_COUNT];
		for (int index : stage.clear()) {
			clear[index] = true;
		}
		float base = tileScale();
		ItemStack[] stageStacks = stage.stacks();
		for (int i = 0; i < MinigameConfig.CELL_COUNT; i++) {
			float scale = clear[i] ? base * (1f - t) : base;
			drawCell(graphics, stageStacks[i], cellX(i), cellY(i), scale, lockVisual(i, lockT));
		}
	}

	private void renderFallStage(GuiGraphicsExtractor graphics, Stage stage, float t, float lockT) {
		float scale = tileScale();
		float[] fromRow = Objects.requireNonNull(stage.fromRow());
		float[] fromCol = stage.fromCol();
		ItemStack[] stageStacks = stage.stacks();
		for (int row = 0; row < MinigameConfig.SIZE; row++) {
			for (int col = 0; col < MinigameConfig.SIZE; col++) {
				int index = col + row * MinigameConfig.SIZE;
				ItemStack stack = stageStacks[index];
				if (stack.isEmpty()) {
					continue;
				}
				float py = fromRow[index] + (row - fromRow[index]) * t;
				float px = col;
				if (fromCol != null) {
					px = fromCol[index] + (col - fromCol[index]) * t;
				}
				drawCell(
						graphics,
						stack,
						x + Math.round(px * cell),
						y + Math.round(py * cell),
						scale,
						lockVisual(index, lockT));
			}
		}
	}

	private void renderBackground(GuiGraphicsExtractor graphics) {
		int size = gridSize();
		graphics.fill(x - 4, y - 4, x + size + 4, y + size + 4, 0xFF3A2F1F);
		for (int i = 0; i < MinigameConfig.CELL_COUNT; i++) {
			graphics.fill(
					cellX(i) + 1,
					cellY(i) + 1,
					cellX(i) + cell - 1,
					cellY(i) + cell - 1,
					((i + i / MinigameConfig.SIZE) & 1) == 0 ? 0xFF8A7A5A : 0xFF7E6E50);
		}
	}

	private void drawCell(GuiGraphicsExtractor graphics, ItemStack stack, int px, int py, float scale, float lockVisual) {
		if (scale <= 0.01f) {
			return;
		}
		drawItem(graphics, stack, px, py, scale);
		if (lockVisual > 0f) {
			int alpha = (int) (0x80 * lockVisual);
			graphics.fill(px + 1, py + 1, px + cell - 1, py + cell - 1, alpha << 24);
			drawItem(graphics, BARRIER, px, py, scale * lockVisual);
		}
	}

	private void drawItem(GuiGraphicsExtractor graphics, ItemStack stack, int px, int py, float scale) {
		if (scale <= 0.01f || stack.isEmpty()) {
			return;
		}
		float offset = (cell - 16f * scale) / 2f;
		graphics.pose().pushMatrix();
		graphics.pose().translate(px + offset, py + offset);
		graphics.pose().scale(scale);
		graphics.fakeItem(stack, 0, 0);
		graphics.pose().popMatrix();
	}

	private int cellX(int index) {
		return x + index % MinigameConfig.SIZE * cell;
	}

	private int cellY(int index) {
		return y + index / MinigameConfig.SIZE * cell;
	}

	private float tileScale() {
		return cell * 0.8f / 16f;
	}

	private float lockVisual(int index, float lockT) {
		if (locked[index]) {
			return locking.contains(index) ? lockT : 1f;
		}
		return unlocking.contains(index) ? 1f - lockT : 0f;
	}

	private static Piece[] decodeCells(BoardState state) {
		byte[] codes = state.cells();
		@Nullable CompoundTag[] data = new CompoundTag[MinigameConfig.CELL_COUNT];
		for (BoardState.Entry entry : state.data()) {
			data[entry.index()] = entry.data();
		}
		Piece[] result = new Piece[MinigameConfig.CELL_COUNT];
		for (int i = 0; i < result.length; i++) {
			result[i] = new Piece(PieceType.byCode(codes[i]), data[i], null);
		}
		return result;
	}

	private static ItemStack[] stacksOf(@Nullable Piece[] grid) {
		ItemStack[] result = new ItemStack[grid.length];
		for (int i = 0; i < grid.length; i++) {
			Piece piece = grid[i];
			result[i] = piece == null ? ItemStack.EMPTY : piece.type().stack(piece);
		}
		return result;
	}

	private static boolean[] decodeLocked(long mask) {
		boolean[] result = new boolean[MinigameConfig.CELL_COUNT];
		for (int i = 0; i < result.length; i++) {
			result[i] = (mask & (1L << i)) != 0;
		}
		return result;
	}
}
