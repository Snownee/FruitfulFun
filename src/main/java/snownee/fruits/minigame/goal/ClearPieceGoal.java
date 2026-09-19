package snownee.fruits.minigame.goal;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.MinigameRule;

public final class ClearPieceGoal extends MinigameGoal {
	private final @Nullable PieceType piece;
	private final int count;

	public ClearPieceGoal(@Nullable PieceType piece, int count, List<ItemStack> rewards) {
		this(piece, count, rewards, List.of());
	}

	public ClearPieceGoal(
			@Nullable PieceType piece,
			int count,
			List<ItemStack> rewards,
			List<Pair<MinigameRule, Component>> rules) {
		super(rewards, rules);
		this.piece = piece;
		this.count = count;
	}

	public @Nullable PieceType piece() {
		return piece;
	}

	public int count() {
		return count;
	}

	@Override
	public ItemStack icon() {
		return pieceIcon(piece, count);
	}

	@Override
	public Component description() {
		return Component.translatable("gui.fruitfulfun.minigame.goal.clear_piece", count, pieceName(piece));
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
