package snownee.fruits.minigame.goal;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.minigame.ClearResult;
import snownee.fruits.minigame.rule.MinigameRule;

public final class StreakGoal extends MinigameGoal {
	private final int length;
	private final int times;

	public StreakGoal(int length, int times, List<ItemStack> rewards) {
		this(length, times, rewards, List.of());
	}

	public StreakGoal(
			int length,
			int times,
			List<ItemStack> rewards,
			List<Pair<MinigameRule, Component>> rules) {
		super(rewards, rules);
		this.length = length;
		this.times = times;
	}

	public int length() {
		return length;
	}

	public int times() {
		return times;
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.BLAZE_POWDER, times);
	}

	@Override
	public Component description() {
		return Component.translatable("gui.fruitfulfun.minigame.goal.streak", length, times);
	}

	@Override
	public int target() {
		return times;
	}

	@Override
	public int advance(ClearResult clear, int progress) {
		if (progress >= times || clear.cause() == ClearResult.Cause.LOOTBOX) {
			return progress;
		}
		return clear.size() >= length ? progress + 1 : 0;
	}
}
