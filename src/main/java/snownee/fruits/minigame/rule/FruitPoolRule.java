package snownee.fruits.minigame.rule;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.util.random.Weighted;
import snownee.fruits.minigame.PieceType;

public final class FruitPoolRule extends MinigameRule {
	private final PieceType type;
	private final int weight;

	private FruitPoolRule(PieceType type, int weight) {
		this.type = type;
		this.weight = weight;
	}

	public static Pair<MinigameRule, Component> add(PieceType type, int weight) {
		return Pair.of(
				new FruitPoolRule(type, weight),
				Component.translatable("gui.fruitfulfun.minigame.rule.pool_add", type.displayName(), weight));
	}

	public static Pair<MinigameRule, Component> remove(PieceType type) {
		return Pair.of(
				new FruitPoolRule(type, 0),
				Component.translatable("gui.fruitfulfun.minigame.rule.pool_remove", type.displayName()));
	}

	public PieceType type() {
		return type;
	}

	public boolean removed() {
		return weight <= 0;
	}

	@Override
	public void modifyPool(List<Weighted<PieceType>> pool) {
		pool.removeIf(entry -> entry.value() == type);
		if (weight > 0) {
			pool.add(new Weighted<>(type, weight));
		}
	}
}
