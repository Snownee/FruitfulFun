package snownee.fruits.minigame;

import net.minecraft.resources.Identifier;
import snownee.fruits.FruitfulFun;

public final class MinigameConfig {
	public static final Identifier LOOTBOX_LOOT_TABLE = FruitfulFun.id("minigame/lootbox1");
	public static final int SIZE = 7;
	public static final int CELL_COUNT = SIZE * SIZE;
	public static final int TIME_LIMIT_SECONDS = 30;
	public static final int MOVE_LIMIT = 20;
	public static final int BASE_SCORE = 10;
	public static final int OVERTIME_SECONDS = 10;
	public static final int OVERTIME_MOVES = 5;
	public static final int REWARD_SLOTS = 54;
	public static final int GOAL_COUNT = 3;
	public static final int MAX_GOAL_REWARDS = 4;
	public static final int MAX_GOAL_RULES = 4;
	public static final int MAX_SPECTATORS = 64;
	public static final int CASCADE_INTERVAL_TICKS = 8;
	public static final int CASCADE_SPEEDUP_INTERVAL = 4;
	public static final float CASCADE_MAX_SPEEDUP = 3f;
	public static final int ICE_BREAKS = 3;
}