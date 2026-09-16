package snownee.fruits.minigame.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import org.jspecify.annotations.Nullable;

import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.MinigameSession;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class MinigameRuleContext {
	private final MinigameSession session;
	private final List<Integer> path;
	private final ClearResult cleared;
	private final @Nullable MinigameSession opponent;

	public MinigameRuleContext(
			MinigameSession session,
			List<Integer> path,
			ClearResult cleared,
			@Nullable MinigameSession opponent) {
		this.session = session;
		this.path = path;
		this.cleared = cleared;
		this.opponent = opponent;
	}

	public List<Integer> path() {
		return path;
	}

	public ClearResult cleared() {
		return cleared;
	}

	public int remainingCount(PieceType type) {
		int count = session.board().pendingCount(type);
		for (Piece piece : session.board().cells()) {
			if (piece != null && piece.is(type)) {
				count++;
			}
		}
		return count;
	}

	public List<Integer> indicesOf(PieceType type) {
		List<Integer> indices = new ArrayList<>();
		Piece[] cells = session.board().cells();
		for (int i = 0; i < cells.length; i++) {
			Piece piece = cells[i];
			if (piece != null && piece.is(type)) {
				indices.add(i);
			}
		}
		return indices;
	}

	public void spawn(Piece piece, int count) {
		session.board().addSpawn(piece, count);
	}

	public int replaceRandom(Piece piece) {
		return session.board().replaceRandom(piece);
	}

	public int replaceRandom(Piece piece, IntPredicate allowed) {
		return session.board().replaceRandom(piece, allowed);
	}

	public void lockOpponent(List<Integer> indices) {
		if (opponent == null || opponent.isFinished()) {
			return;
		}
		opponent.board().addJunk(indices);
	}

	public void startFinale() {
		session.startFinale();
	}
}
