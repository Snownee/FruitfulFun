package snownee.fruits.minigame.level;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.goal.BeehiveGoal;
import snownee.fruits.minigame.goal.BreakAllGoal;
import snownee.fruits.minigame.goal.ClearPieceGoal;
import snownee.fruits.minigame.goal.LargeFruitGoal;
import snownee.fruits.minigame.goal.SingleMoveGoal;
import snownee.fruits.minigame.goal.StreakGoal;
import snownee.fruits.minigame.rule.FruitPoolRule;
import snownee.fruits.minigame.rule.NoDiagonalRule;
import snownee.fruits.minigame.rule.PlacePieceRule;
import snownee.fruits.minigame.rule.ScatterPieceRule;
import snownee.fruits.minigame.rule.SpawnPieceRule;

/**
 * 全部目标模板（硬编码，与原 SoloGoals 的 14 个目标一一对应）。
 * <p>
 * 每个目标的数值都是模板参数，随层数在 [第 1 层区间, 第 30 层区间] 内线性插值后随机取值；
 * 奖励固定为 1 个绿宝石。
 */
public final class GoalTemplates {
	private static final List<GoalTemplate> ALL = List.of(
			// 消除若干樱桃（普通水果，靠数量随层提高难度）
			new GoalTemplate(
					"clear_cherry",
					PieceType.CHERRY,
					null,
					(random, floor) -> new ClearPieceGoal(
							PieceType.CHERRY,
							between(random, floor, 8, 12, 14, 20),
							emerald())),
			// 消除若干柠檬（普通水果）
			new GoalTemplate(
					"clear_lemon",
					PieceType.LEMON,
					null,
					(random, floor) -> new ClearPieceGoal(
							PieceType.LEMON,
							between(random, floor, 8, 12, 14, 20),
							emerald())),
			// 消除若干甜橙（普通水果）
			new GoalTemplate(
					"clear_orange",
					PieceType.ORANGE,
					null,
					(random, floor) -> new ClearPieceGoal(
							PieceType.ORANGE,
							between(random, floor, 7, 12, 10, 18),
							emerald())),
			// 短连线：长度与次数随层提高
			new GoalTemplate(
					"single_move_short",
					null,
					null,
					(random, floor) -> new SingleMoveGoal(
							null,
							between(random, floor, 4, 6, 5, 7),
							between(random, floor, 2, 3, 3, 4),
							emerald())),
			// 长连线：高长度、低次数，基础难度较高
			new GoalTemplate(
					"single_move_long",
					null,
					null,
					(random, floor) -> new SingleMoveGoal(
							null,
							between(random, floor, 7, 8, 8, 10),
							between(random, floor, 1, 1, 1, 2),
							emerald())),
			// 连续连线：对稳定性要求高
			new GoalTemplate(
					"streak",
					null,
					null,
					(random, floor) -> new StreakGoal(
							between(random, floor, 4, 5, 5, 6),
							between(random, floor, 2, 2, 3, 3),
							emerald())),
			// 消除红心果：不会自然出现，由规则按连线长度生成；依赖红心果
			new GoalTemplate(
					"clear_redlove",
					PieceType.REDLOVE,
					null,
					(random, floor) -> new ClearPieceGoal(
							PieceType.REDLOVE,
							between(random, floor, 6, 8, 10, 13),
							emerald(),
							List.of(SpawnPieceRule.spawnPiece(PieceType.REDLOVE, 1, 4, 1)))),
			// 消除石榴：不可连线，只能沉底自消；低上限生成规则
			new GoalTemplate(
					"clear_pomegranate",
					PieceType.POMEGRANATE,
					null,
					(random, floor) -> new ClearPieceGoal(
							PieceType.POMEGRANATE,
							between(random, floor, 4, 5, 6, 8),
							emerald(),
							List.of(SpawnPieceRule.spawnPieceLow(PieceType.POMEGRANATE, 3)))),
			// 清除宝箱：开局撒一只蜜蜂，难度与数量无关，固定值
			new GoalTemplate(
					"clear_lootbox",
					PieceType.LOOTBOX,
					null,
					(random, floor) -> new ClearPieceGoal(
							PieceType.LOOTBOX,
							1,
							emerald(),
							List.of(Pair.of(
									new ScatterPieceRule(PieceType.BEE, 1),
									Component.translatable(
											"gui.fruitfulfun.minigame.rule.scatter_piece",
											PieceType.BEE.displayName()))))),
			// 送蜜蜂进蜂巢：固定难度
			new GoalTemplate(
					"beehive",
					null,
					null,
					(random, floor) -> new BeehiveGoal(1, emerald())),
			// 消除巨大水果：数量随层提高
			new GoalTemplate(
					"large_fruit",
					null,
					null,
					(random, floor) -> new LargeFruitGoal(between(random, floor, 1, 2, 2, 3), emerald())),
			// 禁斜向状态下清除紫颂果：与长连线目标组合时会额外增加难度
			new GoalTemplate(
					"clear_chorus_no_diagonal",
					PieceType.CHORUS,
					null,
					(random, floor) -> new ClearPieceGoal(
							PieceType.CHORUS,
							between(random, floor, 10, 12, 16, 20),
							emerald(),
							List.of(NoDiagonalRule.create()))),
			// 清除甜橙并移除金苹果、经过长连线放置金胡萝卜；与“清除甜橙”共享棋子
			new GoalTemplate(
					"clear_orange_golden_carrot",
					PieceType.ORANGE,
					PieceType.GOLDEN_APPLE,
					(random, floor) -> new ClearPieceGoal(
							PieceType.ORANGE,
							between(random, floor, 9, 12, 15, 20),
							emerald(),
							List.of(
									FruitPoolRule.remove(PieceType.GOLDEN_APPLE),
									PlacePieceRule.create(PieceType.GOLDEN_CARROT, 4)))),
			// 打碎棋盘中心的全部冰块：格子固定，难度固定且偏高
			new GoalTemplate(
					"break_all_ice",
					PieceType.ICE,
					null,
					(random, floor) -> new BreakAllGoal(PieceType.ICE, centerCells(), emerald())));

	private GoalTemplates() {
	}

	public static List<GoalTemplate> all() {
		return ALL;
	}

	/**
	 * 在随层数插值出的区间内均匀取一个整数。
	 *
	 * @param lo1  第 1 层的区间下界
	 * @param hi1  第 1 层的区间上界
	 * @param lo30 第 30 层（封顶）的区间下界
	 * @param hi30 第 30 层（封顶）的区间上界
	 */
	private static int between(RandomSource random, int floor, int lo1, int hi1, int lo30, int hi30) {
		int t = Math.min(floor, MinigameLevelTuning.RAMP_FLOOR);
		int lo = lerp(t, lo1, lo30);
		int hi = lerp(t, hi1, hi30);
		if (hi < lo) {
			hi = lo;
		}
		return lo + random.nextInt(hi - lo + 1);
	}

	/** 第 1 层到第 30 层的线性插值。 */
	private static int lerp(int t, int at1, int at30) {
		return at1 + (at30 - at1) * (t - 1) / (MinigameLevelTuning.RAMP_FLOOR - 1);
	}

	/** 棋盘中心 3x3 的格子索引。 */
	private static List<Integer> centerCells() {
		List<Integer> cells = new ArrayList<>();
		for (int y = 2; y <= 4; y++) {
			for (int x = 2; x <= 4; x++) {
				cells.add(x + y * MinigameConfig.SIZE);
			}
		}
		return cells;
	}

	/** 所有目标的统一奖励：1 个绿宝石。 */
	private static List<ItemStack> emerald() {
		return List.of(new ItemStack(Items.EMERALD));
	}
}
