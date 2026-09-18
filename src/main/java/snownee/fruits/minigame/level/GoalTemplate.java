package snownee.fruits.minigame.level;

import org.jspecify.annotations.Nullable;

import net.minecraft.util.RandomSource;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.goal.MinigameGoal;

/**
 * 目标模板：把一个目标类型抽象成“按层数和随机源生成具体目标”的工厂。
 * <p>
 * 之前的 {@code SoloGoals} 是 14 个固定实例，无法随层数变化；这里改为模板，
 * magnitude（消除数量、连线长度、次数等）随层数在区间内取值，从而支持难度爬升。
 */
public final class GoalTemplate {
	/** 具体目标的构造入口；{@code floor} 用于决定 magnitude 的取值区间。 */
	public interface Factory {
		MinigameGoal build(RandomSource random, int floor);
	}

	/** 模板标识，仅用于调试与可读性。 */
	private final String id;
	/** 该目标依赖的棋子类型；用于“同棋子重叠”的难度修正，无则为 null。 */
	private final @Nullable PieceType requiredPiece;
	/** 该目标会从随机池中移除的棋子类型；用于判定与其他模板的硬冲突，不涉及则为 null。 */
	private final @Nullable PieceType removedFromPool;
	private final Factory factory;

	GoalTemplate(
			String id,
			@Nullable PieceType requiredPiece,
			@Nullable PieceType removedFromPool,
			Factory factory) {
		this.id = id;
		this.requiredPiece = requiredPiece;
		this.removedFromPool = removedFromPool;
		this.factory = factory;
	}

	public String id() {
		return id;
	}

	public @Nullable PieceType requiredPiece() {
		return requiredPiece;
	}

	public MinigameGoal build(RandomSource random, int floor) {
		return factory.build(random, floor);
	}

	/**
	 * 判定两个模板能否共存。当前仅处理“一方移除了另一方必需棋子”的硬冲突，
	 * 其余相互影响交由 {@link MinigameDifficulty} 以难度增减的方式表达。
	 */
	public boolean conflictsWith(GoalTemplate other) {
		return removedFromPool != null && removedFromPool == other.requiredPiece
				|| other.removedFromPool != null && other.removedFromPool == requiredPiece;
	}
}
