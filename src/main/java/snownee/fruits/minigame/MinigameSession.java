package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import snownee.fruits.minigame.goal.MinigameGoal;
import snownee.fruits.minigame.network.PlayerSync;
import snownee.fruits.minigame.network.SMinigameSpectatorPacket;
import snownee.fruits.minigame.network.SMinigameSyncPacket;
import snownee.fruits.minigame.rule.MinigameRule;
import snownee.fruits.minigame.rule.MinigameRuleContext;
import snownee.fruits.minigame.rule.MinigameRules;

public final class MinigameSession {
	public static final int RESULT_NONE = -1;
	public static final int RESULT_TIE = 0;
	public static final int RESULT_WIN = 1;
	public static final int RESULT_LOSE = 2;

	private final ServerPlayer player;
	private final @Nullable BattleTableBlockEntity table;
	private final FruitBoard board;
	private final List<MinigameGoal> goals;
	private final List<MinigameRule> rules;
	private final int[] goalProgress;
	private final int[] goalSynced;
	private final List<ServerPlayer> spectators = new ArrayList<>();
	private long endAtMillis;
	private int moveLimit;
	private int score;
	private int moves;
	private int clears;
	private final SimpleContainer rewards = new SimpleContainer(MinigameConfig.REWARD_SLOTS);
	private boolean finished;
	private List<Integer> currentPath = List.of();
	private @Nullable MinigameSession opponent;
	private int result = RESULT_NONE;
	private boolean started;
	private boolean goalsDone;
	private boolean cascading;
	private int cascadeDelay;

	public MinigameSession(ServerPlayer player) {
		this(player, null, List.of(), true);
	}

	public MinigameSession(ServerPlayer player, @Nullable BattleTableBlockEntity table) {
		this(player, table, List.of(), true);
	}

	public MinigameSession(
			ServerPlayer player,
			@Nullable BattleTableBlockEntity table,
			List<MinigameGoal> goals,
			boolean autoStart) {
		this.player = player;
		this.table = table;
		this.board = new FruitBoard(RandomSource.create());
		this.goals = goals;
		this.rules = collectRules(goals);
		this.goalProgress = new int[goals.size()];
		this.goalSynced = new int[goals.size()];
		this.started = autoStart;
		this.endAtMillis = autoStart ? Util.getMillis() + MinigameManager.duration() * 1000L : 0L;
		this.moveLimit = MinigameConfig.MOVE_LIMIT;
		MinigameRuleContext context = new MinigameRuleContext(this, List.of(), opponent);
		for (MinigameRule rule : rules) {
			rule.onStart(context);
		}
	}

	private static List<MinigameRule> collectRules(List<MinigameGoal> goals) {
		List<MinigameRule> rules = new ArrayList<>(MinigameRules.BASE);
		for (MinigameGoal goal : goals) {
			rules.addAll(goal.rules());
		}
		return List.copyOf(rules);
	}

	public UUID playerId() {
		return player.getUUID();
	}

	public ServerPlayer player() {
		return player;
	}

	public @Nullable BattleTableBlockEntity table() {
		return table;
	}

	public FruitBoard board() {
		return board;
	}

	public int score() {
		return score;
	}

	public int moves() {
		return moves;
	}

	public int clears() {
		return clears;
	}

	public SimpleContainer rewards() {
		return rewards;
	}

	public int moveLimit() {
		return moveLimit;
	}

	public int timeRemaining() {
		if (endAtMillis <= 0) {
			return -1;
		}
		return (int) Math.max(0, (endAtMillis - Util.getMillis()) / 1000);
	}

	public boolean timeUp() {
		return endAtMillis > 0 && Util.getMillis() >= endAtMillis;
	}

	public boolean isStarted() {
		return started;
	}

	public void start() {
		started = true;
	}

	public boolean isFinished() {
		return finished;
	}

	public List<Integer> currentPath() {
		return currentPath;
	}

	public void setCurrentPath(List<Integer> path) {
		currentPath = List.copyOf(path);
	}

	public @Nullable MinigameSession opponent() {
		return opponent;
	}

	public String playerName() {
		return player.getScoreboardName();
	}

	public String opponentName() {
		return opponent == null ? "" : opponent.player.getScoreboardName();
	}

	public int opponentScore() {
		return opponent == null ? -1 : opponent.score;
	}

	public List<ServerPlayer> spectators() {
		return spectators;
	}

	public void addSpectator(ServerPlayer spectator) {
		if (spectators.add(spectator)) {
			notifySpectator(spectator, true);
		}
	}

	public boolean removeSpectator(UUID playerId) {
		for (Iterator<ServerPlayer> iterator = spectators.iterator(); iterator.hasNext(); ) {
			ServerPlayer spectator = iterator.next();
			if (spectator.getUUID().equals(playerId)) {
				iterator.remove();
				notifySpectator(spectator, false);
				return true;
			}
		}
		return false;
	}

	private void notifySpectator(ServerPlayer spectator, boolean joined) {
		SMinigameSpectatorPacket.send(this, spectator.getScoreboardName(), joined);
	}

	public int result() {
		return result;
	}

	public void linkOpponent(MinigameSession opponent) {
		this.opponent = opponent;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int clear(List<Integer> path) {
		if (finished) {
			return -1;
		}
		if (path.size() == 1) {
			ClearResult lootbox = board.clearLootbox(path.getFirst());
			if (lootbox != null) {
				currentPath = List.of();
				clears++;
				player.awardStat(MinigameModule.MINIGAME_PIECES_CLEARED, lootbox.size());
				rollLoot(Objects.requireNonNull(lootbox.pieces().getFirst().piece().serverData()));
				advanceGoals(lootbox);
				return 0;
			}
		}
		if (!board.isValidPath(path)) {
			return -1;
		}
		MinigameRuleContext context = new MinigameRuleContext(this, path, opponent);
		for (MinigameRule rule : rules) {
			rule.onClear(context);
		}
		ClearResult result = board.clear(path);
		if (result == null) {
			return -1;
		}
		currentPath = List.of();
		moves++;
		clears++;
		score += result.score();
		player.awardStat(MinigameModule.MINIGAME_PIECES_CLEARED, result.size());
		player.awardStat(MinigameModule.MINIGAME_LINE_CLEARS);
		advanceGoals(result);
		return result.score();
	}

	private void advanceGoals(ClearResult result) {
		boolean allDone = !goals.isEmpty();
		for (int i = 0; i < goals.size(); i++) {
			MinigameGoal goal = goals.get(i);
			int before = goalProgress[i];
			int after = goal.advance(result, before);
			goalProgress[i] = after;
			if (before < goal.target() && after >= goal.target()) {
				player.awardStat(MinigameModule.MINIGAME_GOALS_COMPLETED);
				goal.rewards().forEach(this::addReward);
			}
			if (goalProgress[i] < goal.target()) {
				allDone = false;
			}
		}
		if (allDone && !goalsDone) {
			goalsDone = true;
			MinigameRuleContext context = new MinigameRuleContext(this, List.of(), opponent);
			for (MinigameRule rule : rules) {
				rule.onGoalsComplete(context);
			}
		}
	}

	public void startFinale() {
		if (cascading || finished) {
			return;
		}
		cascading = true;
		cascadeDelay = MinigameConfig.CASCADE_INTERVAL_TICKS;
	}

	public boolean isCascading() {
		return cascading;
	}

	/**
	 * @return whether a cascade iteration was performed this tick
	 */
	public boolean tickCascade() {
		if (!cascading || finished) {
			return false;
		}
		if (cascadeDelay > 0) {
			cascadeDelay--;
			return false;
		}
		if (moves >= moveLimit) {
			return false;
		}
		cascadeDelay = MinigameConfig.CASCADE_INTERVAL_TICKS;
		board.steps().clear();
		clearLootboxes();
		moves++;
		int seed = board.randomSeed();
		if (seed >= 0) {
			cascadeClear(board.findGroup(seed));
		}
		clearLootboxes();
		clears++;
		return true;
	}

	public boolean isCascadeFinished() {
		return cascading && !finished && moves >= moveLimit && cascadeDelay <= 0;
	}

	private void cascadeClear(List<Integer> indices) {
		MinigameRuleContext context = new MinigameRuleContext(this, indices, opponent);
		for (MinigameRule rule : rules) {
			rule.onClear(context);
		}
		ClearResult result = board.clearCells(indices);
		score += result.score();
		player.awardStat(MinigameModule.MINIGAME_PIECES_CLEARED, result.size());
		advanceGoals(result);
	}

	private void clearLootboxes() {
		ClearResult result = board.clearLootboxes();
		if (result.pieces().isEmpty()) {
			return;
		}
		for (ClearResult.ClearedPiece cleared : result.pieces()) {
			if (cleared.piece().is(PieceType.LOOTBOX)) {
				rollLoot(Objects.requireNonNull(cleared.piece().serverData()));
			}
		}
		player.awardStat(MinigameModule.MINIGAME_PIECES_CLEARED, result.size());
	}

	public List<MinigameGoal> goals() {
		return goals;
	}

	/**
	 * @return every goal entry when {@code open}, otherwise only the entries whose progress changed since the last sync
	 */
	public List<PlayerSync.Entry> goalSync(boolean open) {
		List<PlayerSync.Entry> entries = new ArrayList<>(goals.size());
		for (int i = 0; i < goals.size(); i++) {
			if (open || goalProgress[i] != goalSynced[i]) {
				goalSynced[i] = goalProgress[i];
				entries.add(new PlayerSync.Entry(i, goalProgress[i]));
			}
		}
		return List.copyOf(entries);
	}

	private void rollLoot(CompoundTag serverData) {
		Identifier id = Identifier.parse(serverData.getString(Piece.LOOT_TABLE_KEY).orElseThrow());
		ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
		ServerLevel level = player.level();
		LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
		LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
		for (ItemStack stack : table.getRandomItems(params)) {
			addReward(stack);
		}
	}

	private void addReward(ItemStack stack) {
		ItemStack leftover = rewards.addItem(stack.copy());
		if (!leftover.isEmpty() && !player.getInventory().add(leftover)) {
			player.drop(leftover, false);
		}
	}

	public void finish() {
		finished = true;
	}

	public void overtime() {
		endAtMillis = Util.getMillis() + MinigameConfig.OVERTIME_SECONDS * 1000L;
		moveLimit += MinigameConfig.OVERTIME_MOVES;
		finished = false;
		result = RESULT_NONE;
	}

	public void open() {
		SMinigameSyncPacket.send(this, true);
	}

	public void sync() {
		SMinigameSyncPacket.send(this, false);
	}
}
