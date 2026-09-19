package snownee.fruits.minigame.goal;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.BeehiveRule;

public final class BeehiveGoal extends MinigameGoal {
	private final int count;

	public BeehiveGoal(int count, List<ItemStack> rewards) {
		super(rewards, List.of(BeehiveRule.create()));
		this.count = count;
	}

	@Override
	public ItemStack icon() {
		return pieceIcon(PieceType.BEEHIVE, count);
	}

	@Override
	public Component description() {
		return Component.translatable(
				"gui.fruitfulfun.minigame.goal.beehive",
				count,
				PieceType.BEE.displayName(),
				PieceType.BEEHIVE.displayName());
	}

	@Override
	public int target() {
		return count;
	}

	@Override
	public int advance(ClearResult clear, int progress) {
		return Math.min(count, progress + clear.count(PieceType.BEE));
	}
}
