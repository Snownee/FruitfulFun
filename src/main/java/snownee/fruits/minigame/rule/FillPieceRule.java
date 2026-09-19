package snownee.fruits.minigame.rule;

import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import snownee.fruits.minigame.BoardPattern;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.Piece;

public final class FillPieceRule extends MinigameRule {
	private final BoardPattern pattern;

	private FillPieceRule(BoardPattern pattern) {
		this.pattern = pattern;
	}

	public static Pair<MinigameRule, Component> create(BoardPattern pattern) {
		return Pair.of(
				new FillPieceRule(pattern),
				Component.translatable("gui.fruitfulfun.minigame.rule.fill_piece"));
	}

	@Override
	public int priority() {
		return -100;
	}

	@Override
	public void onStart(MinigameRuleContext context) {
		for (Map.Entry<Integer, Piece> entry : pattern.pieces(MinigameConfig.SIZE).entrySet()) {
			context.setCell(entry.getKey(), entry.getValue());
		}
	}
}
