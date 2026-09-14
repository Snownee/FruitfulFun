package snownee.fruits.minigame.rule;

import snownee.fruits.minigame.Piece;

public final class LootboxRule extends MinigameRule {
	private final int threshold;

	public LootboxRule(int threshold) {
		this.threshold = threshold;
	}

	public int threshold() {
		return threshold;
	}

	@Override
	public void onClear(MinigameRuleContext context) {
		if (context.pathSize() >= threshold) {
			context.spawn(Piece.lootbox(), 1);
		}
	}
}
