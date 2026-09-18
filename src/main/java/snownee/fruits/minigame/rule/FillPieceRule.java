package snownee.fruits.minigame.rule;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class FillPieceRule extends MinigameRule {
	private final PieceType type;
	private final List<Integer> cells;

	private FillPieceRule(PieceType type, List<Integer> cells) {
		this.type = type;
		this.cells = List.copyOf(cells);
	}

	public static Pair<MinigameRule, Component> create(PieceType type, List<Integer> cells) {
		return Pair.of(
				new FillPieceRule(type, cells),
				Component.translatable("gui.fruitfulfun.minigame.rule.fill_piece", type.displayName()));
	}

	@Override
	public int priority() {
		return -100;
	}

	@Override
	public void onStart(MinigameRuleContext context) {
		for (int cell : cells) {
			context.setCell(cell, Piece.of(type));
		}
	}
}
