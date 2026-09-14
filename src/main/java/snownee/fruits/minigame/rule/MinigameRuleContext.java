package snownee.fruits.minigame.rule;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import snownee.fruits.minigame.MinigameSession;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class MinigameRuleContext {
	private final MinigameSession session;
	private final List<Integer> path;
	private final @Nullable MinigameSession opponent;

	public MinigameRuleContext(MinigameSession session, List<Integer> path, @Nullable MinigameSession opponent) {
		this.session = session;
		this.path = path;
		this.opponent = opponent;
	}

	public List<Integer> path() {
		return path;
	}

	public int pathSize() {
		return path.size();
	}

	public int count(PieceType type) {
		int count = 0;
		for (int index : path) {
			if (Objects.requireNonNull(session.board().piece(index)).is(type)) {
				count++;
			}
		}
		return count;
	}

	public int countRemovals(PieceType type) {
		return session.board().countRemovals(path, type);
	}

	public int remainingCount(PieceType type) {
		int count = 0;
		for (Piece piece : session.board().cells()) {
			if (piece != null && piece.is(type)) {
				count++;
			}
		}
		return count - countRemovals(type);
	}

	public void spawn(Piece piece, int count) {
		session.board().addSpawn(piece, count);
	}

	public void replaceRandom(Piece piece) {
		session.board().replaceRandom(piece);
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
