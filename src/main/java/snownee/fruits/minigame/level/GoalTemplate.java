package snownee.fruits.minigame.level;

import net.minecraft.util.RandomSource;
import snownee.fruits.minigame.goal.MinigameGoal;

/**
 * 目标模板：把一个目标类型抽象成“按层数和随机源生成具体目标”的工厂。
 * <p>
 * 之前的 {@code SoloGoals} 是 14 个固定实例，无法随层数变化；这里改为模板，
 * magnitude（消除数量、连线长度、次数等）随层数在区间内取值，从而支持难度爬升。
 * 模板携带 {@link #weight()}，供生成器做加权无放回抽取。
 */
public final class GoalTemplate {
	/** 具体目标的构造入口；{@code floor} 用于决定 magnitude 的取值区间。 */
	public interface Factory {
		MinigameGoal build(RandomSource random, int floor);
	}

	/** 模板标识，仅用于调试与可读性。 */
	private final String id;
	/** 抽取权重，越大越容易被选中。 */
	private final int weight;
	private final Factory factory;

	GoalTemplate(String id, int weight, Factory factory) {
		this.id = id;
		this.weight = weight;
		this.factory = factory;
	}

	public String id() {
		return id;
	}

	public int weight() {
		return weight;
	}

	public MinigameGoal build(RandomSource random, int floor) {
		return factory.build(random, floor);
	}
}
