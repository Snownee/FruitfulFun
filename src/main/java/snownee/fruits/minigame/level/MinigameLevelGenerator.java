package snownee.fruits.minigame.level;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.MinigameManager;
import snownee.fruits.minigame.goal.MinigameGoal;

/**
 * 关卡目标生成器：{@code (主种子, 层数)} → {@link LevelPlan} 的纯函数。
 * <p>
 * 确定性设计：
 * <ul>
 *     <li>{@code floorSeed = splitmix64(seed * φ ^ floor)}，每层独立可 O(1) 重算、可跳层；</li>
 *     <li>目标与棋盘各自用不同的 salt 派生子种子，互不干扰，保证改动目标生成不会改变棋盘；</li>
 *     <li>层内一切随机（含规则的 onStart）都来自棋盘随机源，因此整局可复现。</li>
 * </ul>
 * 难度设计：预算在 1–{@link MinigameLevelTuning#RAMP_FLOOR} 层线性上升后封顶；
 * 上升段要求难度和贴近预算以驱动爬升，封顶后下限固定为 {@link MinigameLevelTuning#CAPPED_MIN_SUM}。
 * 约束：难度和落在 [{@code low}, budget]，且同层过难（≥75）目标不超过 1 个。
 * 正常情况走有界拒绝采样，失败时回退到确定性穷举，尽量满足约束。
 */
public final class MinigameLevelGenerator {
	/** splitmix64 使用的黄金比例常数。 */
	private static final long GOLDEN = 0x9E3779B97F4A7C15L;
	/** 目标生成子种子的 salt。 */
	private static final long SALT_GOALS = 0x632BE59BD9B4E019L;
	/** 棋盘子种子的 salt。 */
	private static final long SALT_BOARD = 0x8CB92BA72F3D8DD7L;
	/** 回退枚举所用子种子的 salt。 */
	private static final long SALT_FALLBACK = 0xD1B54A32D192ED03L;

	private MinigameLevelGenerator() {
	}

	/** 生成指定层数的关卡方案；同 {@code (seed, floor)} 永远得到相同结果。 */
	public static LevelPlan generate(long seed, int floor) {
		int level = Math.max(1, floor);
		long floorSeed = splitmix64(seed * GOLDEN ^ level);
		long boardSeed = splitmix64(floorSeed ^ SALT_BOARD);
		int budget = budget(level);
		// 上升段：难度和需接近预算；封顶段：下限固定为 CAPPED_MIN_SUM
		int low = level <= MinigameLevelTuning.RAMP_FLOOR
				? Math.max(MinigameLevelTuning.MIN_SUM, budget - MinigameLevelTuning.RAMP_TARGET_BAND)
				: MinigameLevelTuning.CAPPED_MIN_SUM;
		RandomSource random = RandomSource.create(splitmix64(floorSeed ^ SALT_GOALS));
		List<GoalTemplate> all = GoalTemplates.all();
		for (int attempt = 0; attempt < MinigameLevelTuning.MAX_ATTEMPTS; attempt++) {
			List<GoalTemplate> picked = pick(random, all);
			if (conflicts(picked)) {
				continue;
			}
			List<MinigameGoal> goals = build(picked, random, level);
			int[] difficulties = MinigameDifficulty.evaluate(goals);
			int sum = sum(difficulties);
			if (sum < low || sum > budget) {
				continue;
			}
			if (hardCount(difficulties) > MinigameLevelTuning.MAX_HARD_GOALS) {
				continue;
			}
			return plan(level, seed, boardSeed, goals, difficulties, budget, false);
		}
		return fallback(level, seed, boardSeed, floorSeed, budget, low);
	}

	/** 第 {@code floor} 层的难度预算：1 层为 BUDGET_BASE，到 RAMP_FLOOR 层线性到达 BUDGET_CAP 后恒定。 */
	public static int budget(int floor) {
		int level = Math.min(Math.max(1, floor), MinigameLevelTuning.RAMP_FLOOR);
		return MinigameLevelTuning.BUDGET_BASE
				+ (MinigameLevelTuning.BUDGET_CAP - MinigameLevelTuning.BUDGET_BASE)
				* (level - 1)
				/ (MinigameLevelTuning.RAMP_FLOOR - 1);
	}

	/**
	 * 确定性穷举回退：枚举所有模板三元组，取“违反约束最少、其次最接近预算”的组合。
	 * 迭代顺序与随机源固定，因此结果仍可复现；仅在存在违反时才标记为 relaxed。
	 */
	private static LevelPlan fallback(int floor, long seed, long boardSeed, long floorSeed, int budget, int low) {
		RandomSource random = RandomSource.create(splitmix64(floorSeed ^ SALT_FALLBACK));
		List<GoalTemplate> all = GoalTemplates.all();
		LevelPlan best = null;
		int bestScore = Integer.MAX_VALUE;
		for (int i = 0; i < all.size(); i++) {
			for (int j = i + 1; j < all.size(); j++) {
				for (int k = j + 1; k < all.size(); k++) {
					List<GoalTemplate> picked = List.of(all.get(i), all.get(j), all.get(k));
					if (conflicts(picked)) {
						continue;
					}
					List<MinigameGoal> goals = build(picked, random, floor);
					int[] difficulties = MinigameDifficulty.evaluate(goals);
					int sum = sum(difficulties);
					int violations = (sum < low ? 1 : 0)
							+ (sum > budget ? 1 : 0)
							+ Math.max(0, hardCount(difficulties) - MinigameLevelTuning.MAX_HARD_GOALS);
					int score = violations * 100000 + Math.abs(sum - budget);
					if (score < bestScore) {
						bestScore = score;
						best = plan(floor, seed, boardSeed, goals, difficulties, budget, violations > 0);
					}
				}
			}
		}
		return best;
	}

	/** 无放回地抽取 GOAL_COUNT 个模板，顺序由随机源决定。 */
	private static List<GoalTemplate> pick(RandomSource random, List<GoalTemplate> all) {
		List<GoalTemplate> pool = new ArrayList<>(all);
		List<GoalTemplate> picked = new ArrayList<>(MinigameConfig.GOAL_COUNT);
		for (int i = 0; i < MinigameConfig.GOAL_COUNT; i++) {
			picked.add(pool.remove(random.nextInt(pool.size())));
		}
		return picked;
	}

	/** 按模板生成具体目标，消耗同一个随机源以保证可复现。 */
	private static List<MinigameGoal> build(List<GoalTemplate> templates, RandomSource random, int floor) {
		List<MinigameGoal> goals = new ArrayList<>(templates.size());
		for (GoalTemplate template : templates) {
			goals.add(template.build(random, floor));
		}
		return List.copyOf(goals);
	}

	/** 三元组内是否存在硬冲突。 */
	private static boolean conflicts(List<GoalTemplate> picked) {
		for (int i = 0; i < picked.size(); i++) {
			for (int j = i + 1; j < picked.size(); j++) {
				if (picked.get(i).conflictsWith(picked.get(j))) {
					return true;
				}
			}
		}
		return false;
	}

	private static int sum(int[] values) {
		int total = 0;
		for (int value : values) {
			total += value;
		}
		return total;
	}

	/** 达到过难阈值的目标数量。 */
	private static int hardCount(int[] values) {
		int count = 0;
		for (int value : values) {
			if (value >= MinigameLevelTuning.HARD_THRESHOLD) {
				count++;
			}
		}
		return count;
	}

	/** 组装最终的 {@link LevelPlan}。 */
	private static LevelPlan plan(
			int floor,
			long seed,
			long boardSeed,
			List<MinigameGoal> goals,
			int[] difficulties,
			int budget,
			boolean relaxed) {
		List<Integer> list = new ArrayList<>(difficulties.length);
		for (int difficulty : difficulties) {
			list.add(difficulty);
		}
		return new LevelPlan(
				floor,
				seed,
				boardSeed,
				goals,
				list,
				sum(difficulties),
				budget,
				relaxed,
				MinigameManager.duration(),
				MinigameConfig.MOVE_LIMIT);
	}

	/** splitmix64 混合函数，用于把主种子与层数派生成稳定的子种子。 */
	private static long splitmix64(long value) {
		long z = value + GOLDEN;
		z = (z ^ z >>> 30) * 0xBF58476D1CE4E5B9L;
		z = (z ^ z >>> 27) * 0x94D049BB133111EBL;
		return z ^ z >>> 31;
	}
}
