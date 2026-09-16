package snownee.fruits.minigame.rule;

import java.util.List;
import java.util.function.IntPredicate;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.PathRules;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class BeehiveRule extends MinigameRule {
	public static final int MIN_DISTANCE = 4;

	public static Pair<MinigameRule, Component> create() {
		return Pair.of(new BeehiveRule(), Component.translatable("gui.fruitfulfun.minigame.rule.beehive"));
	}

	@Override
	public void onStart(MinigameRuleContext context) {
		if (context.remainingCount(PieceType.BEE) > 0) {
			context.replaceRandom(Piece.of(PieceType.BEEHIVE), farFrom(context.indicesOf(PieceType.BEE)));
		} else {
			int hive = context.replaceRandom(Piece.of(PieceType.BEEHIVE));
			context.replaceRandom(Piece.of(PieceType.BEE), farFrom(List.of(hive)));
		}
	}

	private static IntPredicate farFrom(List<Integer> references) {
		int max = 0;
		for (int i = 0; i < MinigameConfig.CELL_COUNT; i++) {
			max = Math.max(max, minDistance(references, i));
		}
		int threshold = Math.min(MIN_DISTANCE, max);
		return index -> minDistance(references, index) >= threshold;
	}

	private static int minDistance(List<Integer> references, int index) {
		int min = Integer.MAX_VALUE;
		for (int reference : references) {
			min = Math.min(min, PathRules.distance(reference, index));
		}
		return min;
	}
}
