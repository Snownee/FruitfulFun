package snownee.fruits.minigame.rule;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class PlacePieceRule extends MinigameRule {
	private final PieceType type;
	private final int position;

	private PlacePieceRule(PieceType type, int position) {
		this.type = type;
		this.position = position;
	}

	public static Pair<MinigameRule, Component> create(PieceType type, int position) {
		return Pair.of(
				new PlacePieceRule(type, position),
				Component.translatable("gui.fruitfulfun.minigame.rule.place_piece", position, type.displayName()));
	}

	@Override
	public void onClear(MinigameRuleContext context) {
		if (context.cleared().cause() != ClearResult.Cause.PATH) {
			return;
		}
		List<Integer> path = context.path();
		if (path.size() < position) {
			return;
		}
		context.place(path.get(position - 1), Piece.of(type), path);
	}
}
