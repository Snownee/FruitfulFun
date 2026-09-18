package snownee.fruits.minigame.level;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.minigame.BoardPattern;
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
 * 全部目标模板（硬编码）。每个模板带有抽取权重，数值随层数在
 * [第 1 层区间, 第 30 层区间] 内线性插值后随机取值；奖励固定为 1 个绿宝石。
 */
public final class GoalTemplates {
	/**
	 * 四种基础水果，供“清除任意基础水果”和“禁斜向”模板随机挑选。
	 */
	private static final List<PieceType> BASIC_FRUITS = List.of(
			PieceType.ORANGE,
			PieceType.LEMON,
			PieceType.CHERRY,
			PieceType.CHORUS);

	/**
	 * 冰块图案及其固定难度；难度直接作为该目标的难度值，不再按格数推算。
	 */
	private record IcePattern(BoardPattern pattern, int difficulty) {
	}

	/**
	 * 打碎冰块模板可用的排列图案（字符串行，'#' 为冰块）与各自的难度。
	 */
	private static final List<IcePattern> ICE_PATTERNS = List.of(
			new IcePattern(
					BoardPattern.of(
							"###",
							"###",
							"###"), 85),
			new IcePattern(
					BoardPattern.of(
							"#.#",
							".#.",
							"#.#"), 60),
			new IcePattern(
					BoardPattern.of(
							".#.",
							"###",
							".#."), 54),
			new IcePattern(
					BoardPattern.of(
							"#.#",
							"...",
							"#.#"), 48),
			new IcePattern(
					BoardPattern.of(
							".......",
							".......",
							".......",
							".......",
							".......",
							"#######",
							"#######"), 58),
			new IcePattern(
					BoardPattern.of(
							"##...##",
							"#.....#",
							".......",
							".......",
							".......",
							"#.....#",
							"##...##"), 48));

	private static final List<GoalTemplate> ALL = List.of(
			// 清除任意一种基础水果：四种基础水果中随机选一种，靠数量随层提高难度
			new GoalTemplate(
					"clear_basic",
					20,
					(random, floor) -> new ClearPieceGoal(
							BASIC_FRUITS.get(random.nextInt(BASIC_FRUITS.size())),
							between(random, floor, 8, 12, 14, 20),
							emerald())),
			// 短连线：长度与次数随层提高
			new GoalTemplate(
					"single_move_short",
					12,
					(random, floor) -> new SingleMoveGoal(
							null,
							between(random, floor, 4, 6, 5, 7),
							between(random, floor, 2, 3, 3, 4),
							emerald())),
			// 长连线：高长度、低次数，基础难度较高
			new GoalTemplate(
					"single_move_long",
					8,
					(random, floor) -> new SingleMoveGoal(
							null,
							between(random, floor, 7, 8, 8, 10),
							between(random, floor, 1, 1, 1, 2),
							emerald())),
			// 连续连线：对稳定性要求高
			new GoalTemplate(
					"streak",
					10,
					(random, floor) -> new StreakGoal(
							between(random, floor, 4, 5, 5, 6),
							between(random, floor, 2, 2, 3, 3),
							emerald())),
			// 消除红心果：不会自然出现，由规则按连线长度生成
			new GoalTemplate(
					"clear_redlove",
					8,
					(random, floor) -> new ClearPieceGoal(
							PieceType.REDLOVE,
							between(random, floor, 6, 8, 10, 13),
							emerald(),
							List.of(SpawnPieceRule.spawnPiece(PieceType.REDLOVE, 1, 4, 1)))),
			// 消除石榴：不可连线，只能沉底自消；低上限生成规则
			new GoalTemplate(
					"clear_pomegranate",
					10,
					(random, floor) -> new ClearPieceGoal(
							PieceType.POMEGRANATE,
							between(random, floor, 4, 5, 6, 8),
							emerald(),
							List.of(SpawnPieceRule.spawnPieceLow(PieceType.POMEGRANATE, 3)))),
			// 清除宝箱：开局撒一只蜜蜂，难度与数量无关，固定值
			new GoalTemplate(
					"clear_lootbox",
					6,
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
					5,
					(random, floor) -> new BeehiveGoal(1, emerald())),
			// 消除巨大水果：数量随层提高
			new GoalTemplate(
					"large_fruit",
					6,
					(random, floor) -> new LargeFruitGoal(between(random, floor, 1, 2, 2, 3), emerald())),
			// 禁斜向：先移除一种基础水果，再从剩余三种中随机选一种作为目标
			new GoalTemplate(
					"clear_no_diagonal",
					7,
					(random, floor) -> {
						List<PieceType> pool = new ArrayList<>(BASIC_FRUITS);
						PieceType removed = pool.remove(random.nextInt(pool.size()));
						PieceType target = pool.get(random.nextInt(pool.size()));
						return new ClearPieceGoal(
								target,
								between(random, floor, 10, 12, 16, 20),
								emerald(),
								List.of(NoDiagonalRule.create(), FruitPoolRule.remove(removed)));
					}),
			// 清除甜橙并移除金苹果、经过长连线放置金胡萝卜；与“清除甜橙”共享棋子
			new GoalTemplate(
					"clear_orange_golden_carrot",
					8,
					(random, floor) -> new ClearPieceGoal(
							PieceType.ORANGE,
							between(random, floor, 9, 12, 15, 20),
							emerald(),
							List.of(
									FruitPoolRule.remove(PieceType.GOLDEN_APPLE),
									PlacePieceRule.create(PieceType.GOLDEN_CARROT, 4)))),
			// 打碎冰块：从多种排列图案中随机选一种，难度取图案自带的固定值
			new GoalTemplate(
					"break_all_ice",
					4,
					(random, floor) -> {
						IcePattern ice = ICE_PATTERNS.get(random.nextInt(ICE_PATTERNS.size()));
						return new BreakAllGoal(
								PieceType.ICE,
								ice.pattern(),
								ice.difficulty(),
								emerald());
					}));

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

	/**
	 * 第 1 层到第 30 层的线性插值。
	 */
	private static int lerp(int t, int at1, int at30) {
		return at1 + (at30 - at1) * (t - 1) / (MinigameLevelTuning.RAMP_FLOOR - 1);
	}

	/**
	 * 所有目标的统一奖励：1 个绿宝石。
	 */
	private static List<ItemStack> emerald() {
		return List.of(new ItemStack(Items.EMERALD));
	}
}
