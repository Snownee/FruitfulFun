package snownee.fruits.minigame.goal;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.Piece;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.MinigameRule;
import snownee.fruits.minigame.rule.ScatterPieceRule;
import snownee.fruits.minigame.rule.SpawnPieceRule;

public final class SoloGoals {
	private static final List<MinigameGoal> POOL = List.of(
			new ClearPieceGoal(PieceType.CHERRY, 12, fruit(PieceType.CHERRY, 4)),
			new ClearPieceGoal(PieceType.LEMON, 12, fruit(PieceType.LEMON, 4)),
			new ClearPieceGoal(PieceType.ORANGE, 10, fruit(PieceType.ORANGE, 4)),
			new SingleMoveGoal(null, 5, 3, fruit(PieceType.GOLDEN_APPLE, 2)),
			new SingleMoveGoal(null, 8, 1, List.of(new ItemStack(Items.EMERALD))),
			redloveGoal(),
			pomegranateGoal(),
			lootboxGoal());

	private SoloGoals() {
	}

	public static List<MinigameGoal> random(RandomSource random) {
		List<MinigameGoal> pool = new ArrayList<>(POOL);
		List<MinigameGoal> goals = new ArrayList<>(MinigameConfig.GOAL_COUNT);
		for (int i = 0; i < MinigameConfig.GOAL_COUNT; i++) {
			goals.add(pool.remove(random.nextInt(pool.size())));
		}
		return List.copyOf(goals);
	}

	private static MinigameGoal redloveGoal() {
		PieceType type = PieceType.REDLOVE;
		int offset = 4;
		return new ClearPieceGoal(type, 8, fruit(type, 4), List.of(SpawnPieceRule.spawnPiece(type, 1, offset, 1)));
	}

	private static MinigameGoal pomegranateGoal() {
		PieceType type = PieceType.POMEGRANATE;
		int max = 3;
		return new ClearPieceGoal(type, 5, fruit(type, 4), List.of(SpawnPieceRule.spawnPieceLow(type, max)));
	}

	private static MinigameGoal lootboxGoal() {
		PieceType type = PieceType.LOOTBOX;
		List<Pair<MinigameRule, Component>> rules = List.of(Pair.of(
				new ScatterPieceRule(PieceType.BEE, 1),
				Component.translatable(
						"gui.fruitfulfun.minigame.rule.scatter_piece",
						PieceType.BEE.displayName())));
		return new ClearPieceGoal(type, 1, List.of(new ItemStack(Items.EMERALD)), rules);
	}

	private static List<ItemStack> fruit(PieceType type, int count) {
		return List.of(type.stack(Piece.of(type)).copyWithCount(count));
	}
}
