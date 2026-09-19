package snownee.fruits.minigame.goal;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.LargeFruitRule;

public final class LargeFruitGoal extends MinigameGoal {
	private final int count;

	public LargeFruitGoal(int count, List<ItemStack> rewards) {
		super(rewards, List.of(LargeFruitRule.create(count)));
		this.count = count;
	}

	@Override
	public ItemStack icon() {
		return pieceIcon(PieceType.LARGE_ORANGE, count);
	}

	@Override
	public Component description() {
		return Component.translatable("gui.fruitfulfun.minigame.goal.large_fruit", count);
	}

	@Override
	public int target() {
		return count;
	}

	@Override
	public int advance(ClearResult clear, int progress) {
		return Math.min(count, progress + clear.countLarge());
	}
}
