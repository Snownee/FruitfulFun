package snownee.fruits.minigame.rule;

public final class FinaleRule extends MinigameRule {
	@Override
	public void onGoalsComplete(MinigameRuleContext context) {
		context.startFinale();
	}
}
