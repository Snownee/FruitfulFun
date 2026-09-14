package snownee.fruits.minigame.rule;

import java.util.List;

public final class MinigameRules {
	public static final List<MinigameRule> BASE = List.of(
			new LootboxRule(10),
			new LockOpponentRule(4),
			new FinaleRule());

	private MinigameRules() {
	}
}
