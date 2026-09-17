package snownee.fruits.minigame.rule;

import java.util.List;

import net.minecraft.util.random.Weighted;
import snownee.fruits.minigame.PieceType;

public abstract class MinigameRule {
	public boolean allowsDiagonal() {
		return true;
	}

	public void modifyPool(List<Weighted<PieceType>> pool) {
	}

	public void onStart(MinigameRuleContext context) {
	}

	public void onClear(MinigameRuleContext context) {
	}

	public void onGoalsComplete(MinigameRuleContext context) {
	}
}
