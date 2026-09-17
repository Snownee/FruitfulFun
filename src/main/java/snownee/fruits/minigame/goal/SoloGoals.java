package snownee.fruits.minigame.goal;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.PieceType;
import snownee.fruits.minigame.rule.FruitPoolRule;
import snownee.fruits.minigame.rule.MinigameRule;
import snownee.fruits.minigame.rule.NoDiagonalRule;
import snownee.fruits.minigame.rule.PlacePieceRule;
import snownee.fruits.minigame.rule.ScatterPieceRule;
import snownee.fruits.minigame.rule.SpawnPieceRule;

public final class SoloGoals {
	private static final List<MinigameGoal> POOL = List.of(
			new ClearPieceGoal(PieceType.CHERRY, 12, emerald()),
			new ClearPieceGoal(PieceType.LEMON, 12, emerald()),
			new ClearPieceGoal(PieceType.ORANGE, 10, emerald()),
			new SingleMoveGoal(null, 5, 3, emerald()),
			new SingleMoveGoal(null, 8, 1, emerald()),
			new StreakGoal(5, 3, emerald()),
			redloveGoal(),
			pomegranateGoal(),
			lootboxGoal(),
			beehiveGoal(),
			largeFruitGoal(),
			noDiagonalGoal(),
			goldenCarrotGoal());

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
		return new ClearPieceGoal(type, 8, emerald(), List.of(SpawnPieceRule.spawnPiece(type, 1, offset, 1)));
	}

	private static MinigameGoal pomegranateGoal() {
		PieceType type = PieceType.POMEGRANATE;
		int max = 3;
		return new ClearPieceGoal(type, 5, emerald(), List.of(SpawnPieceRule.spawnPieceLow(type, max)));
	}

	private static MinigameGoal lootboxGoal() {
		PieceType type = PieceType.LOOTBOX;
		List<Pair<MinigameRule, Component>> rules = List.of(Pair.of(
				new ScatterPieceRule(PieceType.BEE, 1),
				Component.translatable(
						"gui.fruitfulfun.minigame.rule.scatter_piece",
						PieceType.BEE.displayName())));
		return new ClearPieceGoal(type, 1, emerald(), rules);
	}

	private static MinigameGoal beehiveGoal() {
		return new BeehiveGoal(1, emerald());
	}

	private static MinigameGoal largeFruitGoal() {
		return new LargeFruitGoal(3, emerald());
	}

	private static MinigameGoal noDiagonalGoal() {
		PieceType type = PieceType.CHORUS;
		return new ClearPieceGoal(type, 12, emerald(), List.of(NoDiagonalRule.create()));
	}

	private static MinigameGoal goldenCarrotGoal() {
		PieceType type = PieceType.ORANGE;
		List<Pair<MinigameRule, Component>> rules = List.of(
				FruitPoolRule.remove(PieceType.GOLDEN_APPLE),
				PlacePieceRule.create(PieceType.GOLDEN_CARROT, 4));
		return new ClearPieceGoal(type, 12, emerald(), rules);
	}

	private static List<ItemStack> emerald() {
		return List.of(new ItemStack(Items.EMERALD));
	}
}
