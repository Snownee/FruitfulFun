package snownee.fruits.minigame.level;

import java.util.List;

import snownee.fruits.minigame.goal.MinigameGoal;

/**
 * 一层的完整生成结果，由 {@link MinigameLevelGenerator#generate(long, int)} 产出。
 * <p>
 * 该记录同时承载难度评估信息（{@link #goalDifficulties()}、{@link #difficulty()}、{@link #budget()}）
 * 与复现信息（{@link #seed()}、{@link #boardSeed()}）。难度仅存在于服务端，不下发客户端。
 *
 * @param floor              关卡层数
 * @param seed               生成该层所用的主种子
 * @param boardSeed          由种子派生的棋盘随机种子，保证同 (seed, floor) 复现同一棋盘
 * @param goals              本层的目标列表（固定 3 个）
 * @param goalDifficulties   与 {@code goals} 一一对应的难度评估值，范围 0–100
 * @param difficulty         整体难度，即各目标难度之和
 * @param budget             本层的难度预算上限
 * @param constraintRelaxed  是否因无法满足约束而放宽（正常情况为 false）
 * @param timeLimit          本层时间上限（秒）
 * @param moveLimit          本层步数上限
 */
public record LevelPlan(
		int floor,
		long seed,
		long boardSeed,
		List<MinigameGoal> goals,
		List<Integer> goalDifficulties,
		int difficulty,
		int budget,
		boolean constraintRelaxed,
		int timeLimit,
		int moveLimit) {
	public LevelPlan {
		goals = List.copyOf(goals);
		goalDifficulties = List.copyOf(goalDifficulties);
	}
}
