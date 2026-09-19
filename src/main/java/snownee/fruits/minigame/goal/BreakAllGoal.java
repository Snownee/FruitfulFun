package snownee.fruits.minigame.goal;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.BoardPattern;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.FillPieceRule;

public final class BreakAllGoal extends MinigameGoal {
	private final PieceType piece;
	private final int count;
	private final int difficulty;

	public BreakAllGoal(PieceType piece, BoardPattern pattern, int difficulty, List<ItemStack> rewards) {
		super(rewards, List.of(FillPieceRule.create(pattern)));
		this.piece = piece;
		this.count = pattern.cells(MinigameConfig.SIZE).size();
		this.difficulty = difficulty;
	}

	public PieceType piece() {
		return piece;
	}

	public int difficulty() {
		return difficulty;
	}

	@Override
	public ItemStack icon() {
		return pieceIcon(piece, count);
	}

	@Override
	public Component description() {
		return Component.translatable("gui.fruitfulfun.minigame.goal.break_all", pieceName(piece));
	}

	@Override
	public int target() {
		return count;
	}

	@Override
	public int advance(ClearResult clear, int progress) {
		return Math.min(count, progress + clear.count(piece));
	}
}
