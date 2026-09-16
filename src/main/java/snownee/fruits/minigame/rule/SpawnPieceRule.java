package snownee.fruits.minigame.rule;

import java.util.function.ToIntFunction;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;

public final class SpawnPieceRule extends MinigameRule {
	private final PieceType type;
	private final ToIntFunction<MinigameRuleContext> count;

	public SpawnPieceRule(PieceType type, ToIntFunction<MinigameRuleContext> count) {
		this.type = type;
		this.count = count;
	}

	public static Pair<MinigameRule, Component> spawnPiece(PieceType type, int count, int start, int step) {
		return Pair.of(
				new SpawnPieceRule(type, context -> context.cleared().cause() == ClearResult.Cause.PATH
						? count * ((context.cleared().size() - start) / step)
						: 0),
				Component.translatable(
						"gui.fruitfulfun.minigame.rule.spawn_piece",
						start,
						step,
						count,
						type.displayName()));
	}

	public static Pair<MinigameRule, Component> spawnPieceLow(PieceType type, int max) {
		return Pair.of(
				new SpawnPieceRule(type, context -> context.remainingCount(type) < max ? 1 : 0),
				Component.translatable(
						"gui.fruitfulfun.minigame.rule.spawn_piece_low",
						type.displayName(),
						max));
	}

	public PieceType type() {
		return type;
	}

	@Override
	public void onClear(MinigameRuleContext context) {
		int count = this.count.applyAsInt(context);
		if (count > 0) {
			context.spawn(Piece.of(type), count);
		}
	}
}
