package snownee.fruits.minigame.rule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class LargeFruitRule extends MinigameRule {
	public static final int MIN_LENGTH = 3;
	public static final int MAX_LENGTH = 6;

	private static final List<PieceType> LARGE_TYPES = List.of(
			PieceType.LARGE_ORANGE,
			PieceType.LARGE_LEMON,
			PieceType.LARGE_CHERRY,
			PieceType.LARGE_CHORUS,
			PieceType.LARGE_GOLDEN_APPLE);

	private final int count;

	public LargeFruitRule(int count) {
		this.count = count;
	}

	public static Pair<MinigameRule, Component> create(int count) {
		return Pair.of(new LargeFruitRule(count), Component.translatable("gui.fruitfulfun.minigame.rule.large_fruit", count));
	}

	@Override
	public void onStart(MinigameRuleContext context) {
		RandomSource random = context.random();
		Set<Integer> avoid = new HashSet<>(context.indicesOf(PieceType.BEE));
		avoid.addAll(context.indicesOf(PieceType.BEEHIVE));
		for (int i = 0; i < count; i++) {
			PieceType type = LARGE_TYPES.get(random.nextInt(LARGE_TYPES.size()));
			int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
			int index = context.replaceRandom(Piece.large(type, length), cell -> !avoid.contains(cell));
			if (index < 0) {
				return;
			}
			avoid.add(index);
		}
	}
}
