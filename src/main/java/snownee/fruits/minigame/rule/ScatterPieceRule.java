package snownee.fruits.minigame.rule;

import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class ScatterPieceRule extends MinigameRule {
	private final PieceType type;
	private final int count;

	public ScatterPieceRule(PieceType type, int count) {
		this.type = type;
		this.count = count;
	}

	@Override
	public void onStart(MinigameRuleContext context) {
		for (int i = 0; i < count; i++) {
			context.replaceRandom(Piece.of(type));
		}
	}
}
