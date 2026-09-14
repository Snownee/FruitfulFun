package snownee.fruits.minigame.network;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.BoardStep;
import snownee.fruits.minigame.MinigameConfig;
import snownee.fruits.minigame.MinigameSession;
import snownee.fruits.minigame.goal.MinigameGoal;

public record PlayerSync(
		int score,
		int moves,
		int clears,
		long locked,
		List<BoardStep> steps,
		List<Integer> path,
		List<ItemStack> rewards,
		List<MinigameGoal.Display> goals,
		List<Entry> goalProgress,
		List<String> spectators,
		boolean started) {
	public static final PlayerSync EMPTY = new PlayerSync(
			-1, 0, 0, 0L, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), true);

	public record Entry(int index, int current) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Entry::index,
				ByteBufCodecs.VAR_INT,
				Entry::current,
				Entry::new);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSync> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			PlayerSync::score,
			ByteBufCodecs.VAR_INT,
			PlayerSync::moves,
			ByteBufCodecs.VAR_INT,
			PlayerSync::clears,
			ByteBufCodecs.LONG,
			PlayerSync::locked,
			BoardStep.STREAM_CODEC.apply(ByteBufCodecs.list(MinigameConfig.CELL_COUNT)),
			PlayerSync::steps,
			ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(MinigameConfig.CELL_COUNT)),
			PlayerSync::path,
			ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(MinigameConfig.REWARD_SLOTS)),
			PlayerSync::rewards,
			MinigameGoal.Display.STREAM_CODEC.apply(ByteBufCodecs.list(MinigameConfig.GOAL_COUNT)),
			PlayerSync::goals,
			Entry.STREAM_CODEC.apply(ByteBufCodecs.list(MinigameConfig.GOAL_COUNT)),
			PlayerSync::goalProgress,
			ByteBufCodecs.stringUtf8(32).apply(ByteBufCodecs.list(MinigameConfig.MAX_SPECTATORS)),
			PlayerSync::spectators,
			ByteBufCodecs.BOOL,
			PlayerSync::started,
			PlayerSync::new);

	public static PlayerSync of(MinigameSession session, boolean open) {
		return new PlayerSync(
				session.score(),
				session.moves(),
				session.clears(),
				session.board().lockedMask(),
				List.copyOf(session.board().steps()),
				session.currentPath(),
				session.rewards().getItems().stream().filter($ -> !$.isEmpty()).toList(),
				open ? session.goals().stream().map(MinigameGoal::display).toList() : List.of(),
				session.goalSync(open),
				session.spectators().stream().map(ServerPlayer::getScoreboardName).toList(),
				session.isStarted());
	}
}
