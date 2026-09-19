package snownee.fruits.command;

import java.util.List;
import java.util.Locale;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import snownee.fruits.minigame.MinigameManager;
import snownee.fruits.minigame.goal.MinigameGoal;
import snownee.fruits.minigame.level.LevelPlan;
import snownee.fruits.minigame.level.MinigameLevelGenerator;
import snownee.fruits.minigame.level.MinigameLevelTuning;

public class MinigameCommand {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("minigame")
				.then(Commands.literal("solo")
						.executes($ -> solo($.getSource(), MinigameManager.randomFloor(), randomSeed()))
						.then(Commands.argument("floor", IntegerArgumentType.integer(1, 10000))
								.executes($ -> solo(
										$.getSource(),
										IntegerArgumentType.getInteger($, "floor"),
										randomSeed()))
								.then(Commands.argument("seed", LongArgumentType.longArg())
										.executes($ -> solo(
												$.getSource(),
												IntegerArgumentType.getInteger($, "floor"),
												LongArgumentType.getLong($, "seed"))))))
				.then(Commands.literal("plan")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.then(Commands.argument("floor", IntegerArgumentType.integer(1, 10000))
								.executes($ -> plan(
										$.getSource(),
										IntegerArgumentType.getInteger($, "floor"),
										randomSeed()))
								.then(Commands.argument("seed", LongArgumentType.longArg())
										.executes($ -> plan(
												$.getSource(),
												IntegerArgumentType.getInteger($, "floor"),
												LongArgumentType.getLong($, "seed"))))))
				.then(Commands.literal("scan")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.then(Commands.argument("from", IntegerArgumentType.integer(1, 10000))
								.then(Commands.argument("to", IntegerArgumentType.integer(1, 10000))
										.executes($ -> scan(
												$.getSource(),
												IntegerArgumentType.getInteger($, "from"),
												IntegerArgumentType.getInteger($, "to"),
												randomSeed()))
										.then(Commands.argument("seed", LongArgumentType.longArg())
												.executes($ -> scan(
														$.getSource(),
														IntegerArgumentType.getInteger($, "from"),
														IntegerArgumentType.getInteger($, "to"),
														LongArgumentType.getLong($, "seed")))))))
				.then(Commands.literal("invite")
						.then(Commands.argument("player", EntityArgument.player())
								.executes($ -> invite($.getSource(), EntityArgument.getPlayer($, "player")))))
				.then(Commands.literal("accept")
						.executes($ -> accept($.getSource())))
				.then(Commands.literal("decline")
						.executes($ -> decline($.getSource())))
				.then(Commands.literal("spectate")
						.then(Commands.argument("player", EntityArgument.player())
								.executes($ -> spectate($.getSource(), EntityArgument.getPlayer($, "player")))))
				.then(Commands.literal("duration")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.executes($ -> showDuration($.getSource()))
						.then(Commands.argument("seconds", IntegerArgumentType.integer(10, 3600))
								.executes($ -> setDuration($.getSource(), IntegerArgumentType.getInteger($, "seconds")))));
	}

	private static long randomSeed() {
		return 0;// RandomSource.create().nextLong();
	}

	private static int solo(CommandSourceStack source, int floor, long seed) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		LevelPlan plan = MinigameManager.startSolo(player, floor, seed);
		if (plan == null) {
			return 0;
		}
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.minigame.started"), false);
		if (Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source)) {
			sendPlan(source, plan);
		}
		return 1;
	}

	private static int plan(CommandSourceStack source, int floor, long seed) {
		sendPlan(source, MinigameLevelGenerator.generate(seed, floor));
		return 1;
	}

	private static int scan(CommandSourceStack source, int from, int to, long seed) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		int total = 0;
		int count = 0;
		int violations = 0;
		int relaxed = 0;
		boolean deterministic = true;
		for (int floor = from; floor <= to; floor++) {
			LevelPlan first = MinigameLevelGenerator.generate(seed, floor);
			LevelPlan second = MinigameLevelGenerator.generate(seed, floor);
			if (!samePlan(first, second)) {
				deterministic = false;
			}
			int difficulty = first.difficulty();
			min = Math.min(min, difficulty);
			max = Math.max(max, difficulty);
			total += difficulty;
			count++;
			if (hardCount(first) > MinigameLevelTuning.MAX_HARD_GOALS) {
				violations++;
			}
			if (first.constraintRelaxed()) {
				relaxed++;
			}
		}
		int finalMin = min;
		int finalMax = max;
		int finalViolations = violations;
		int finalRelaxed = relaxed;
		boolean finalDeterministic = deterministic;
		String average = String.format(Locale.ROOT, "%.1f", (double) total / count);
		source.sendSuccess(
				() -> Component.translatable(
						"command.fruitfulfun.minigame.plan.scan",
						from,
						to,
						finalMin,
						finalMax,
						average,
						finalViolations,
						finalRelaxed,
						finalDeterministic), false);
		return count;
	}

	private static void sendPlan(CommandSourceStack source, LevelPlan plan) {
		boolean relaxed = plan.constraintRelaxed();
		source.sendSuccess(
				() -> Component.translatable(
						"command.fruitfulfun.minigame.plan.header",
						plan.floor(),
						plan.seed(),
						plan.difficulty(),
						plan.budget()).append(relaxed
						? Component.translatable("command.fruitfulfun.minigame.plan.relaxed")
						: Component.empty()), false);
		List<MinigameGoal> goals = plan.goals();
		List<Integer> difficulties = plan.goalDifficulties();
		for (int i = 0; i < goals.size(); i++) {
			int index = i;
			source.sendSuccess(
					() -> Component.translatable(
							"command.fruitfulfun.minigame.plan.goal",
							goals.get(index).description(),
							difficulties.get(index)), false);
		}
	}

	private static int hardCount(LevelPlan plan) {
		int count = 0;
		for (int difficulty : plan.goalDifficulties()) {
			if (difficulty >= MinigameLevelTuning.HARD_THRESHOLD) {
				count++;
			}
		}
		return count;
	}

	private static boolean samePlan(LevelPlan first, LevelPlan second) {
		if (first.difficulty() != second.difficulty()
				|| first.constraintRelaxed() != second.constraintRelaxed()
				|| first.goalDifficulties().size() != second.goalDifficulties().size()) {
			return false;
		}
		for (int i = 0; i < first.goals().size(); i++) {
			MinigameGoal a = first.goals().get(i);
			MinigameGoal b = second.goals().get(i);
			if (a.getClass() != b.getClass() || a.target() != b.target()) {
				return false;
			}
		}
		return true;
	}

	private static int invite(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
		MinigameManager.invite(source.getPlayerOrException(), target);
		return 0;
	}

	private static int accept(CommandSourceStack source) throws CommandSyntaxException {
		MinigameManager.accept(source.getPlayerOrException());
		return 0;
	}

	private static int decline(CommandSourceStack source) throws CommandSyntaxException {
		MinigameManager.decline(source.getPlayerOrException());
		return 0;
	}

	private static int spectate(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
		MinigameManager.spectate(source.getPlayerOrException(), target);
		return 0;
	}

	private static int showDuration(CommandSourceStack source) {
		int duration = MinigameManager.duration();
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.minigame.duration.get", duration), false);
		return duration;
	}

	private static int setDuration(CommandSourceStack source, int seconds) {
		MinigameManager.setDuration(seconds);
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.minigame.duration.set", seconds), true);
		return seconds;
	}
}
