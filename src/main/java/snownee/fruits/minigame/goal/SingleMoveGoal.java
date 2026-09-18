package snownee.fruits.minigame.goal;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.MinigameRule;

public final class SingleMoveGoal extends MinigameGoal {
	private final @Nullable PieceType piece;
	private final int length;
	private final int times;

	public SingleMoveGoal(@Nullable PieceType piece, int length, int times, List<ItemStack> rewards) {
		this(piece, length, times, rewards, List.of());
	}

	public SingleMoveGoal(
			@Nullable PieceType piece, int length,
			int times,
			List<ItemStack> rewards,
			List<Pair<MinigameRule, Component>> rules) {
		super(rewards, rules);
		this.length = length;
		this.times = times;
		this.piece = piece;
	}

	public int length() {
		return length;
	}

	public @Nullable PieceType piece() {
		return piece;
	}

	public int times() {
		return times;
	}

	@Override
	public ItemStack icon() {
		return pieceIcon(piece, length);
	}

	@Override
	public Component description() {
		return Component.translatable("gui.fruitfulfun.minigame.goal.single_move", length, pieceName(piece));
	}

	@Override
	public int target() {
		return times;
	}

	@Override
	public int advance(ClearResult clear, int progress) {
		if (clear.size() < length || clear.count(piece) < length) {
			return progress;
		}
		return Math.min(times, progress + 1);
	}
}
