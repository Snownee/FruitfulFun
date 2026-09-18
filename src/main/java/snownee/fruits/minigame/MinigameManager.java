package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleMenuProvider;
import snownee.fruits.minigame.level.LevelPlan;
import snownee.fruits.minigame.level.MinigameLevelGenerator;
import snownee.fruits.minigame.network.CBattleTableActionPacket;
import snownee.fruits.minigame.network.SBattleTableSyncPacket;
import snownee.fruits.minigame.network.SMinigameSyncPacket;

public final class MinigameManager {
	private static final Map<UUID, MinigameSession> SESSIONS = Maps.newHashMap();
	private static final Map<UUID, UUID> INVITES = Maps.newHashMap();
	private static final Map<UUID, BattleTableBlockEntity> TABLE_MEMBERS = Maps.newHashMap();
	private static final Set<BattleTableBlockEntity> TABLES = Collections.newSetFromMap(new IdentityHashMap<>());
	private static int durationSeconds = MinigameConfig.TIME_LIMIT_SECONDS;

	private static final Map<UUID, MinigameSession> PENDING_REWARDS = Maps.newHashMap();

	private MinigameManager() {
	}

	public static int duration() {
		return durationSeconds;
	}

	public static void setDuration(int seconds) {
		durationSeconds = seconds;
	}

	public static void startSolo(ServerPlayer player) {
		startSolo(player, randomFloor(), RandomSource.create().nextLong());
	}

	public static @Nullable LevelPlan startSolo(ServerPlayer player, int floor, long seed) {
		MinigameSession session = SESSIONS.get(player.getUUID());
		if (session == null || session.isFinished()) {
			LevelPlan plan = MinigameLevelGenerator.generate(seed, floor);
			session = new MinigameSession(player, null, plan);
			SESSIONS.put(player.getUUID(), session);
			session.open();
			return plan;
		}
		session.open();
		return null;
	}

	public static int randomFloor() {
		return 1 + RandomSource.create().nextInt(10000);
	}

	public static void startSession(ServerPlayer player) {
		MinigameSession session = SESSIONS.get(player.getUUID());
		if (session == null || session.isFinished() || session.isStarted()) {
			return;
		}
		session.start();
		session.sync();
	}

	public static void openTable(ServerPlayer player, BattleTableBlockEntity table) {
		TABLES.add(table);
		if (table.active()) {
			UUID left = Objects.requireNonNull(table.left());
			UUID right = Objects.requireNonNull(table.right());
			if (left.equals(player.getUUID()) || right.equals(player.getUUID())) {
				SESSIONS.get(player.getUUID()).open();
			} else {
				spectateTable(player, table);
			}
			return;
		}
		BattleTableBlockEntity current = TABLE_MEMBERS.get(player.getUUID());
		if (current != null && current != table) {
			player.sendSystemMessage(Component.translatable("gui.fruitfulfun.battle_table.already_joined"));
			return;
		}
		if (current == table) {
			sendTable(table);
			return;
		}
		if (table.left() == null) {
			table.setLeft(player.getUUID());
		} else {
			table.spectators().add(player.getUUID());
		}
		TABLE_MEMBERS.put(player.getUUID(), table);
		sendTable(table);
	}

	public static void tableAction(ServerPlayer player, BlockPos pos, int action) {
		if (!(player.level().getBlockEntity(pos) instanceof BattleTableBlockEntity table)) {
			return;
		}
		if (action == CBattleTableActionPacket.LEAVE) {
			leaveTable(player, table);
		} else if (action == CBattleTableActionPacket.SPECTATE) {
			becomeSpectator(player, table);
		} else if (action == CBattleTableActionPacket.START) {
			startTable(player, table);
		} else if (!table.active()) {
			joinSeat(player, table, action);
		}
	}

	private static void joinSeat(ServerPlayer player, BattleTableBlockEntity table, int action) {
		if (TABLE_MEMBERS.get(player.getUUID()) != table) {
			return;
		}
		if (action == CBattleTableActionPacket.LEFT && table.left() == null) {
			table.spectators().remove(player.getUUID());
			if (player.getUUID().equals(table.right())) {
				table.setRight(null);
			}
			table.setLeft(player.getUUID());
		} else if (action == CBattleTableActionPacket.RIGHT && table.right() == null) {
			table.spectators().remove(player.getUUID());
			if (player.getUUID().equals(table.left())) {
				table.setLeft(null);
			}
			table.setRight(player.getUUID());
		} else {
			return;
		}
		sendTable(table);
	}

	private static void becomeSpectator(ServerPlayer player, BattleTableBlockEntity table) {
		if (TABLE_MEMBERS.get(player.getUUID()) != table) {
			return;
		}
		if (player.getUUID().equals(table.left())) {
			table.setLeft(null);
		} else if (player.getUUID().equals(table.right())) {
			table.setRight(null);
		} else {
			return;
		}
		table.spectators().add(player.getUUID());
		sendTable(table);
	}

	private static void leaveTable(ServerPlayer player, BattleTableBlockEntity table) {
		if (TABLE_MEMBERS.get(player.getUUID()) != table) {
			return;
		}
		TABLE_MEMBERS.remove(player.getUUID());
		table.spectators().remove(player.getUUID());
		if (player.getUUID().equals(table.left())) {
			table.setLeft(null);
		} else if (player.getUUID().equals(table.right())) {
			table.setRight(null);
		}
		SBattleTableSyncPacket.close(table, player);
		sendTable(table);
	}

	private static void startTable(ServerPlayer player, BattleTableBlockEntity table) {
		if (TABLE_MEMBERS.get(player.getUUID()) != table || !player.getUUID().equals(table.left()) || table.right() == null) {
			return;
		}
		ServerPlayer right = player.level().getServer().getPlayerList().getPlayer(table.right());
		if (right == null || SESSIONS.containsKey(player.getUUID()) || SESSIONS.containsKey(right.getUUID())) {
			return;
		}
		MinigameSession leftSession = new MinigameSession(player, table);
		MinigameSession rightSession = new MinigameSession(right, table);
		leftSession.linkOpponent(rightSession);
		rightSession.linkOpponent(leftSession);
		SESSIONS.put(player.getUUID(), leftSession);
		SESSIONS.put(right.getUUID(), rightSession);
		table.setActive(true);
		SBattleTableSyncPacket.close(table, player);
		SBattleTableSyncPacket.close(table, right);
		leftSession.open();
		rightSession.open();
		for (UUID spectatorId : table.spectators()) {
			ServerPlayer spectator = player.level().getServer().getPlayerList().getPlayer(spectatorId);
			if (spectator != null) {
				spectateTable(spectator, table);
			}
		}
	}

	private static void spectateTable(ServerPlayer spectator, BattleTableBlockEntity table) {
		BattleTableBlockEntity current = TABLE_MEMBERS.get(spectator.getUUID());
		if (current != null && current != table) {
			spectator.sendSystemMessage(Component.translatable("gui.fruitfulfun.battle_table.already_joined"));
			return;
		}
		MinigameSession session = SESSIONS.get(table.left());
		if (session == null) {
			return;
		}
		TABLES.add(table);
		TABLE_MEMBERS.put(spectator.getUUID(), table);
		table.spectators().add(spectator.getUUID());
		spectate(spectator, session.player());
	}

	private static void sendTable(BattleTableBlockEntity table) {
		for (UUID member : List.copyOf(TABLE_MEMBERS.keySet())) {
			if (TABLE_MEMBERS.get(member) != table) {
				continue;
			}
			ServerPlayer player = getPlayer(table, member);
			if (player == null || !nearTable(player, table)) {
				removeTableMember(player, table, member);
			} else {
				SBattleTableSyncPacket.send(table, player);
			}
		}
	}

	private static @Nullable ServerPlayer getPlayer(BattleTableBlockEntity table, UUID id) {
		ServerLevel level = Objects.requireNonNull((ServerLevel) table.getLevel());
		return level.getServer().getPlayerList().getPlayer(id);
	}

	private static boolean nearTable(ServerPlayer player, BattleTableBlockEntity table) {
		return player.level() == table.getLevel() && player.distanceToSqr(
				table.getBlockPos().getX() + 0.5,
				table.getBlockPos().getY() + 0.5,
				table.getBlockPos().getZ() + 0.5) <= 4096;
	}

	private static void removeTableMember(@Nullable ServerPlayer player, BattleTableBlockEntity table, UUID id) {
		TABLE_MEMBERS.remove(id);
		table.spectators().remove(id);
		if (id.equals(table.left())) {
			table.setLeft(null);
		} else if (id.equals(table.right())) {
			table.setRight(null);
		}
		MinigameSession session = SESSIONS.get(id);
		if (session != null) {
			session.removeSpectator(id);
		}
		if (player != null) {
			SBattleTableSyncPacket.close(table, player);
		}
	}

	public static void removeTable(BattleTableBlockEntity table) {
		TABLES.remove(table);
		for (UUID member : List.copyOf(TABLE_MEMBERS.keySet())) {
			if (TABLE_MEMBERS.get(member) == table) {
				ServerPlayer player = getPlayer(table, member);
				if (!table.active() && player != null) {
					SBattleTableSyncPacket.close(table, player);
				}
				TABLE_MEMBERS.remove(member);
			}
		}
		if (!table.active()) {
			table.clearWaiting();
		}
	}

	public static void invite(ServerPlayer from, ServerPlayer to) {
		if (from == to) {
			from.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.selfInvite"));
			return;
		}
		if (SESSIONS.containsKey(from.getUUID()) || SESSIONS.containsKey(to.getUUID())) {
			from.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.busy", to.getDisplayName()));
			return;
		}
		INVITES.put(to.getUUID(), from.getUUID());
		from.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.invited", to.getDisplayName()));
		MutableComponent accept = Component.translatable("command.fruitfulfun.minigame.acceptButton")
				.withStyle(style -> style
						.withColor(ChatFormatting.GREEN)
						.withClickEvent(new ClickEvent.RunCommand("fruitfulfun minigame accept"))
						.withHoverEvent(new HoverEvent.ShowText(Component.translatable("command.fruitfulfun.minigame.acceptButton.hover"))));
		to.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.inviteReceived", from.getDisplayName())
				.append(Component.literal(" "))
				.append(accept));
	}

	public static void accept(ServerPlayer player) {
		UUID inviterId = INVITES.remove(player.getUUID());
		ServerPlayer inviter = inviterId == null ? null : player.level().getServer().getPlayerList().getPlayer(inviterId);
		if (inviter == null || inviter.getUUID().equals(player.getUUID()) || SESSIONS.containsKey(inviter.getUUID())) {
			player.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.noInvite"));
			return;
		}
		MinigameSession a = new MinigameSession(inviter);
		MinigameSession b = new MinigameSession(player);
		a.linkOpponent(b);
		b.linkOpponent(a);
		SESSIONS.put(inviter.getUUID(), a);
		SESSIONS.put(player.getUUID(), b);
		a.open();
		b.open();
		Component started = Component.translatable("command.fruitfulfun.minigame.accepted");
		inviter.sendSystemMessage(started);
		player.sendSystemMessage(started);
	}

	public static void decline(ServerPlayer player) {
		UUID inviterId = INVITES.remove(player.getUUID());
		if (inviterId == null) {
			player.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.noInvite"));
			return;
		}
		Component declined = Component.translatable("command.fruitfulfun.minigame.declined");
		player.sendSystemMessage(declined);
		ServerPlayer inviter = player.level().getServer().getPlayerList().getPlayer(inviterId);
		if (inviter != null) {
			inviter.sendSystemMessage(declined);
		}
	}

	public static void submitPath(ServerPlayer player, List<Integer> path) {
		MinigameSession session = SESSIONS.get(player.getUUID());
		if (session == null || session.isFinished() || !session.isStarted() || session.isCascading()) {
			return;
		}
		if (session.timeUp()) {
			finishSession(session);
			return;
		}
		if (session.clear(path) < 0) {
			return;
		}
		if (session.moves() >= session.moveLimit()) {
			finishSession(session);
		} else {
			syncBoth(session);
		}
	}

	public static void spectate(ServerPlayer spectator, ServerPlayer target) {
		MinigameSession session = SESSIONS.get(target.getUUID());
		if (session == null || session.isFinished()) {
			spectator.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.spectate.none", target.getDisplayName()));
			return;
		}
		if (SESSIONS.containsKey(spectator.getUUID())) {
			spectator.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.busy", target.getDisplayName()));
			return;
		}
		unsubscribe(spectator.getUUID());
		session.addSpectator(spectator);
		SMinigameSyncPacket.send(session, true, spectator);
		spectator.sendSystemMessage(Component.translatable("command.fruitfulfun.minigame.spectating", target.getDisplayName()));
	}

	public static void updatePath(ServerPlayer player, List<Integer> path) {
		MinigameSession session = SESSIONS.get(player.getUUID());
		if (session == null || session.isFinished() || !session.isStarted() || session.isCascading()) {
			return;
		}
		session.setCurrentPath(path);
		notifySpectators(session);
		MinigameSession opponent = session.opponent();
		if (opponent != null) {
			notifySpectators(opponent);
			SMinigameSyncPacket.send(opponent, false, opponent.player());
		}
	}

	private static void notifySpectators(MinigameSession session) {
		for (ServerPlayer spectator : session.spectators()) {
			SMinigameSyncPacket.send(session, false, spectator);
		}
	}

	public static void quit(ServerPlayer player) {
		UUID id = player.getUUID();
		unsubscribe(id);
		MinigameSession finished = PENDING_REWARDS.remove(id);
		if (finished != null) {
			openRewards(finished);
		}
		MinigameSession session = SESSIONS.get(id);
		if (session != null) {
			if (session.timeUp()) {
				finishSession(session);
				return;
			}
			MinigameSession opponent = session.opponent();
			SESSIONS.remove(id);
			if (opponent != null) {
				SESSIONS.remove(opponent.playerId());
				awardWin(opponent, session);
				openRewards(session);
			} else {
				SESSIONS.remove(id);
				session.finish();
				session.sync();
				openRewards(session);
			}
			return;
		}
		MinigameSession opponentSession = findMatchOf(id);
		if (opponentSession != null) {
			SESSIONS.remove(opponentSession.playerId());
			awardWin(opponentSession, Objects.requireNonNull(opponentSession.opponent()));
		}
	}

	public static void onDisconnect(ServerPlayer player) {
		UUID id = player.getUUID();
		PENDING_REWARDS.remove(id);
		INVITES.remove(id);
		INVITES.values().removeIf(id::equals);
		BattleTableBlockEntity table = TABLE_MEMBERS.remove(id);
		if (table != null) {
			table.spectators().remove(id);
			if (!table.active()) {
				if (id.equals(table.left())) {
					table.setLeft(null);
				} else if (id.equals(table.right())) {
					table.setRight(null);
				}
				sendTable(table);
			}
		}
		unsubscribe(id);
		MinigameSession session = SESSIONS.remove(id);
		if (session == null) {
			MinigameSession opponentSession = findMatchOf(id);
			if (opponentSession != null) {
				SESSIONS.remove(opponentSession.playerId());
				awardWin(opponentSession, Objects.requireNonNull(opponentSession.opponent()));
			}
			return;
		}
		MinigameSession opponent = session.opponent();
		if (opponent == null) {
			session.finish();
			notifySpectators(session);
			return;
		}
		SESSIONS.remove(opponent.playerId());
		awardWin(opponent, session);
	}

	private static void unsubscribe(UUID playerId) {
		for (MinigameSession session : SESSIONS.values()) {
			session.removeSpectator(playerId);
		}
	}

	private static @Nullable MinigameSession findMatchOf(UUID playerId) {
		for (MinigameSession session : SESSIONS.values()) {
			if (session.opponent() != null && session.opponent().playerId().equals(playerId)) {
				return session;
			}
		}
		return null;
	}

	private static void awardWin(MinigameSession winner, MinigameSession loser) {
		winner.setResult(MinigameSession.RESULT_WIN);
		winner.finish();
		loser.setResult(MinigameSession.RESULT_LOSE);
		loser.finish();
		notifySpectators(loser);
		winner.sync();
		finishTable(winner);
		parkRewards(winner);
	}

	private static void openRewards(MinigameSession session) {
		if (session.rewards().isEmpty()) {
			return;
		}
		session.player().openMenu(new SimpleMenuProvider(
				(id, inventory, player) -> new RewardsChestMenu(id, inventory, session.rewards()),
				Component.translatable("gui.fruitfulfun.minigame.rewards")));
	}

	private static void parkRewards(MinigameSession session) {
		PENDING_REWARDS.put(session.playerId(), session);
	}

	public static void tick() {
		for (BattleTableBlockEntity table : List.copyOf(TABLES)) {
			if (!table.active()) {
				sendTable(table);
			}
		}
		if (SESSIONS.isEmpty()) {
			return;
		}
		for (MinigameSession session : List.copyOf(SESSIONS.values())) {
			if (session.tickCascade()) {
				session.sync();
			} else if (session.isCascadeFinished()) {
				finishSession(session);
			} else if (!session.isFinished() && session.timeUp()) {
				finishSession(session);
			}
		}
	}

	private static void syncBoth(MinigameSession session) {
		session.sync();
		if (session.opponent() != null) {
			session.opponent().sync();
		}
	}

	private static void finishSession(MinigameSession session) {
		session.finish();
		MinigameSession opponent = session.opponent();
		if (opponent == null) {
			session.sync();
			SESSIONS.remove(session.playerId());
			finishTable(session);
			parkRewards(session);
			return;
		}
		if (!opponent.isFinished()) {
			session.sync();
			opponent.player().sendSystemMessage(Component.translatable(
					"command.fruitfulfun.minigame.opponentFinished",
					session.player().getDisplayName()));
			SESSIONS.remove(session.playerId());
			parkRewards(session);
			return;
		}
		int cmp = Integer.compare(session.score(), opponent.score());
		if (cmp == 0) {
			session.overtime();
			opponent.overtime();
			SESSIONS.put(session.playerId(), session);
			SESSIONS.put(opponent.playerId(), opponent);
			Component overtime = Component.translatable("command.fruitfulfun.minigame.overtime");
			session.player().sendSystemMessage(overtime);
			opponent.player().sendSystemMessage(overtime);
			session.open();
			opponent.open();
			return;
		}
		session.setResult(cmp > 0 ? MinigameSession.RESULT_WIN : MinigameSession.RESULT_LOSE);
		opponent.setResult(cmp > 0 ? MinigameSession.RESULT_LOSE : MinigameSession.RESULT_WIN);
		session.sync();
		opponent.sync();
		SESSIONS.remove(session.playerId());
		SESSIONS.remove(opponent.playerId());
		finishTable(session);
		parkRewards(session);
		parkRewards(opponent);
	}

	private static void finishTable(MinigameSession session) {
		BattleTableBlockEntity table = session.table();
		if (table == null || !table.active()) {
			return;
		}
		table.setActive(false);
		for (UUID member : List.copyOf(TABLE_MEMBERS.keySet())) {
			if (TABLE_MEMBERS.get(member) == table) {
				TABLE_MEMBERS.remove(member);
			}
		}
		table.clearWaiting();
	}
}
