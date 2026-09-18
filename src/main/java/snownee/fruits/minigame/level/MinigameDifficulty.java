package snownee.fruits.minigame.level;

import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.util.Mth;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.goal.BeehiveGoal;
import snownee.fruits.minigame.goal.BreakAllGoal;
import snownee.fruits.minigame.goal.ClearPieceGoal;
import snownee.fruits.minigame.goal.LargeFruitGoal;
import snownee.fruits.minigame.goal.MinigameGoal;
import snownee.fruits.minigame.goal.SingleMoveGoal;
import snownee.fruits.minigame.goal.StreakGoal;
import snownee.fruits.minigame.rule.FruitPoolRule;
import snownee.fruits.minigame.rule.MinigameRule;

/**
 * 难度评估：把一组目标映射为 0–100 的单值难度数组。
 * <p>
 * 公式为 {@code clamp(base(目标) + Σ规则修正 + Σ上下文修正)}：
 * <ul>
 *     <li>{@code base} 只由目标类型与参数（数量、长度、次数）决定；</li>
 *     <li>规则修正处理目标自身携带规则带来的影响，如“禁止斜向”；</li>
 *     <li>上下文修正处理目标之间的相互作用，如同棋子重叠、池移除必需棋子、步数预算吃紧。</li>
 * </ul>
 * 评估在生成时静态执行一次，运行期不重估。
 */
public final class MinigameDifficulty {
	private MinigameDifficulty() {
	}

	/** 评估一整层目标，返回与输入顺序一致的难度数组。 */
	public static int[] evaluate(List<MinigameGoal> goals) {
		int size = goals.size();
		int[] result = new int[size];
		// 关卡级别的上下文：任一目标带“禁止斜向”即视为整层禁斜向
		boolean noDiagonal = goals.stream().anyMatch(
				goal -> goal.rules().stream().anyMatch(rule -> !rule.allowsDiagonal()));
		int required = 0;
		for (MinigameGoal goal : goals) {
			required += goal.target();
		}
		// 三个目标要求的总消除量超过步数预算时，整层都更吃紧
		boolean overflow = required > MinigameConfig.MOVE_LIMIT * MinigameLevelTuning.MOVES_PER_CLEAR_ESTIMATE;
		for (int i = 0; i < size; i++) {
			MinigameGoal goal = goals.get(i);
			int value = base(goal);
			if (noDiagonal && isLongLine(goal)) {
				value += MinigameLevelTuning.DELTA_NO_DIAGONAL_LONG_LINE;
			}
			PieceType piece = requiredPiece(goal);
			if (piece != null) {
				for (int j = 0; j < size; j++) {
					if (j == i) {
						continue;
					}
					if (requiredPiece(goals.get(j)) == piece) {
						value += MinigameLevelTuning.DELTA_SHARED_PIECE;
					}
					if (removesFromPool(goals.get(j), piece)) {
						value += MinigameLevelTuning.DELTA_MISSING_REQUIRED_PIECE;
					}
				}
			}
			if (overflow) {
				value += MinigameLevelTuning.DELTA_MOVE_BUDGET_OVERFLOW;
			}
			result[i] = Mth.clamp(value, MinigameLevelTuning.SCALE_MIN, MinigameLevelTuning.SCALE_MAX);
		}
		return result;
	}

	/** 目标自身的基础难度，仅取决于类型与参数。 */
	private static int base(MinigameGoal goal) {
		int value;
		if (goal instanceof ClearPieceGoal clear) {
			PieceType piece = clear.piece();
			if (piece == PieceType.LOOTBOX) {
				// 宝箱数量固定为 1，用固定值表达其额外操作成本
				value = MinigameLevelTuning.CLEAR_LOOTBOX_BASE;
			} else {
				value = clear.count() * MinigameLevelTuning.CLEAR_PER_COUNT + pieceBonus(piece);
			}
		} else if (goal instanceof SingleMoveGoal single) {
			value = MinigameLevelTuning.SINGLE_MOVE_BASE
					+ single.length() * MinigameLevelTuning.SINGLE_MOVE_PER_LENGTH
					+ single.times() * MinigameLevelTuning.SINGLE_MOVE_PER_TIME;
		} else if (goal instanceof StreakGoal streak) {
			value = MinigameLevelTuning.STREAK_BASE
					+ streak.length() * MinigameLevelTuning.STREAK_PER_LENGTH
					+ streak.times() * MinigameLevelTuning.STREAK_PER_TIME;
		} else if (goal instanceof BeehiveGoal) {
			value = MinigameLevelTuning.BEEHIVE_BASE;
		} else if (goal instanceof LargeFruitGoal large) {
			value = MinigameLevelTuning.LARGE_FRUIT_BASE
					+ large.target() * MinigameLevelTuning.LARGE_FRUIT_PER_COUNT;
		} else if (goal instanceof BreakAllGoal) {
			value = MinigameLevelTuning.BREAK_ALL_BASE;
		} else {
			throw new IllegalArgumentException("Unknown goal type: " + goal.getClass());
		}
		return Mth.clamp(value, MinigameLevelTuning.SCALE_MIN, MinigameLevelTuning.SCALE_MAX);
	}

	/** 特殊棋子的额外基础难度。 */
	private static int pieceBonus(@Nullable PieceType piece) {
		if (piece == PieceType.REDLOVE) {
			return MinigameLevelTuning.CLEAR_REDLOVE_BONUS;
		}
		if (piece == PieceType.POMEGRANATE) {
			return MinigameLevelTuning.CLEAR_POMEGRANATE_BONUS;
		}
		return 0;
	}

	/** 是否为对连线长度敏感的目标（用于与“禁止斜向”组合）。 */
	private static boolean isLongLine(MinigameGoal goal) {
		return goal instanceof StreakGoal
				|| goal instanceof SingleMoveGoal single && single.length() >= MinigameLevelTuning.LONG_LINE_LENGTH;
	}

	/** 目标依赖的棋子类型，无则返回 null。 */
	private static @Nullable PieceType requiredPiece(MinigameGoal goal) {
		if (goal instanceof ClearPieceGoal clear) {
			return clear.piece();
		}
		if (goal instanceof SingleMoveGoal single) {
			return single.piece();
		}
		if (goal instanceof BreakAllGoal breakAll) {
			return breakAll.piece();
		}
		return null;
	}

	/** 目标的规则是否会从随机池中移除指定棋子。 */
	private static boolean removesFromPool(MinigameGoal goal, PieceType piece) {
		for (MinigameRule rule : goal.rules()) {
			if (rule instanceof FruitPoolRule pool && pool.removed() && pool.type() == piece) {
				return true;
			}
		}
		return false;
	}
}
