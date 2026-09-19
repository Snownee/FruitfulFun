package snownee.fruits.minigame.rule;

import java.util.ArrayList;
import java.util.List;

public final class LockOpponentRule extends MinigameRule {
	private final int interval;

	public LockOpponentRule(int interval) {
		this.interval = interval;
	}

	public int interval() {
		return interval;
	}

	@Override
	public void onClear(MinigameRuleContext context) {
		List<Integer> path = context.path();
		List<Integer> locked = new ArrayList<>();
		for (int i = interval; i <= path.size(); i += interval) {
			locked.add(path.get(i - 1));
		}
		if (!locked.isEmpty()) {
			context.lockOpponent(locked);
		}
	}
}
