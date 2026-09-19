package snownee.fruits.minigame.rule;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;

public final class NoDiagonalRule extends MinigameRule {
	public static Pair<MinigameRule, Component> create() {
		return Pair.of(new NoDiagonalRule(), Component.translatable("gui.fruitfulfun.minigame.rule.no_diagonal"));
	}

	@Override
	public boolean allowsDiagonal() {
		return false;
	}
}
