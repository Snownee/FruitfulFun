package snownee.fruits.minigame.level;

/**
 * 关卡生成与难度评估的可调常量。
 * <p>
 * 难度标尺为 0–100 的单值；关卡难度取三个目标难度之和（{@link LevelPlan#difficulty()}）。
 * 曲线在 1–{@link #RAMP_FLOOR} 层线性上升，之后封顶，难度和维持在
 * [{@link #CAPPED_MIN_SUM}, {@link #BUDGET_CAP}]，靠目标组合保持多样性。
 * 所有数值都集中在这里，调整平衡时只需改本文件。
 */
public final class MinigameLevelTuning {
	/** 单个目标难度的下界。 */
	public static final int SCALE_MIN = 0;
	/** 单个目标难度的上界。 */
	public static final int SCALE_MAX = 100;

	/** 难度上升到封顶所需层数；超过该层数后难度不再增长。 */
	public static final int RAMP_FLOOR = 30;
	/** 第 1 层的难度预算（三个目标难度之和）。 */
	public static final int BUDGET_BASE = 60;
	/** 封顶后的难度预算。 */
	public static final int BUDGET_CAP = 210;
	/** 难度和的硬性下限，低于此值的组合直接丢弃，避免过于无趣。 */
	public static final int MIN_SUM = 50;
	/** 封顶层（层数 > {@link #RAMP_FLOOR}）的难度和下限。 */
	public static final int CAPPED_MIN_SUM = 180;
	/** “过难目标”阈值，单个目标难度达到该值即视为过难。 */
	public static final int HARD_THRESHOLD = 75;
	/** 同一关卡允许出现的过难目标数量上限（保证两个过难目标不同层）。 */
	public static final int MAX_HARD_GOALS = 1;
	/** 主循环的有界拒绝采样次数，超出后走确定性穷举回退。 */
	public static final int MAX_ATTEMPTS = 64;
	/** 上升段要求难度和落在 [budget - 该值, budget]，以此驱动难度随层数爬升。 */
	public static final int RAMP_TARGET_BAND = 30;

	// —— 基础难度公式的各目标系数（详见 MinigameDifficulty.base）——
	/** 清除类目标：每消除 1 个棋子贡献的难度。 */
	public static final int CLEAR_PER_COUNT = 2;
	/** 清除红心果的额外难度：它不会自然出现，需要靠规则生成。 */
	public static final int CLEAR_REDLOVE_BONUS = 10;
	/** 清除石榴的额外难度：不可连线，只能沉底消除。 */
	public static final int CLEAR_POMEGRANATE_BONUS = 8;
	/** 清除宝箱的固定难度。 */
	public static final int CLEAR_LOOTBOX_BASE = 25;
	/** 单次连线目标的固定基础难度。 */
	public static final int SINGLE_MOVE_BASE = 4;
	/** 单次连线目标：每 1 点连线长度贡献的难度。 */
	public static final int SINGLE_MOVE_PER_LENGTH = 4;
	/** 单次连线目标：每 1 次达成贡献的难度。 */
	public static final int SINGLE_MOVE_PER_TIME = 6;
	/** 连续连线目标的固定基础难度。 */
	public static final int STREAK_BASE = 8;
	/** 连续连线目标：每 1 点连线长度贡献的难度。 */
	public static final int STREAK_PER_LENGTH = 4;
	/** 连续连线目标：每 1 次连续贡献的难度。 */
	public static final int STREAK_PER_TIME = 6;
	/** 送蜜蜂进蜂巢的固定难度。 */
	public static final int BEEHIVE_BASE = 70;
	/** 消除巨大水果的固定基础难度。 */
	public static final int LARGE_FRUIT_BASE = 22;
	/** 清除巨大水果：每个巨大水果贡献的难度。 */
	public static final int LARGE_FRUIT_PER_COUNT = 20;

	/** 被视为“长连线”的连线长度阈值，用于与“禁止斜向”的上下文修正搭配。 */
	public static final int LONG_LINE_LENGTH = 6;
	/** 估算每个步数平均能完成的消除量，用于判断步数预算是否吃紧。 */
	public static final int MOVES_PER_CLEAR_ESTIMATE = 3;

	// —— 上下文的难度修正 ——
	/** 关卡中存在“禁止斜向”规则时，长连线类目标追加的难度。 */
	public static final int DELTA_NO_DIAGONAL_LONG_LINE = 15;
	/** 三个目标所需总消除量超出步数预算时的追加难度。 */
	public static final int DELTA_MOVE_BUDGET_OVERFLOW = 10;
	/** 两个目标需要同一种棋子时，可一并推进，双方各减的难度。 */
	public static final int DELTA_SHARED_PIECE = -10;

	private MinigameLevelTuning() {
	}
}
