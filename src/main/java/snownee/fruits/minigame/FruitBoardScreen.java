package snownee.fruits.minigame;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.network.CQuitMinigamePacket;
import snownee.fruits.minigame.network.CStartMinigamePacket;
import snownee.fruits.minigame.network.CSubmitPathPacket;
import snownee.fruits.minigame.network.CUpdatePathPacket;
import snownee.fruits.minigame.network.SMinigameSpectatorPacket;
import snownee.fruits.minigame.network.SMinigameSyncPacket;

public class FruitBoardScreen extends Screen {
	private static final int VS_GAP = 24;
	private static final int MAX_CELL = 30;
	private static final long INTRO_MS = 2000;
	private static final int REWARD_PER_ROW = 9;
	private static final int REWARD_DISPLAY_MAX = 18;
	private static final int START_BUTTON_WIDTH = 100;
	private static final int START_BUTTON_HEIGHT = 20;
	private static final int TAB_LIST_COLUMN = 80;
	private static final int TAB_LIST_ROWS = 20;
	private static final SystemToast.SystemToastId MINIGAME_TOAST = new SystemToast.SystemToastId();

	private final GoalPanel goalPanel;
	private final BoardView ownBoard = new BoardView(true);
	private final BoardView opponentBoard = new BoardView(true);
	private final List<Integer> path = new ArrayList<>();

	private int score;
	private int moves;
	private int clears;
	private int moveLimit;
	private int timeRemaining;
	private boolean finished;
	private String playerName = "";
	private String opponentName = "";
	private int opponentScore = -1;
	private int opponentMoves;
	private int opponentClears;
	private List<ItemStack> rewards = List.of();
	private List<Integer> selfPath = List.of();
	private List<Integer> opponentPath = List.of();
	private int result = MinigameSession.RESULT_NONE;
	private boolean started = true;
	private List<String> spectators = List.of();
	private long receiveMillis;
	private final long introStart = Util.getMillis();
	private int introSound = -1;
	private boolean spectating;
	private @Nullable Button startButton;

	public FruitBoardScreen(SMinigameSyncPacket packet) {
		this(packet, packet.spectating());
	}

	public FruitBoardScreen(SMinigameSyncPacket packet, boolean spectating) {
		super(Component.translatable("gui.fruitfulfun.minigame"));
		this.spectating = spectating;
		this.goalPanel = new GoalPanel(font);
		update(packet);
	}

	@Override
	protected void init() {
		goalPanel.layout(width);
		startButton = addRenderableWidget(Button.builder(
						Component.translatable("gui.fruitfulfun.minigame.start"),
						_ -> CStartMinigamePacket.send())
				.bounds(startButtonX(), startButtonY(), START_BUTTON_WIDTH, START_BUTTON_HEIGHT)
				.build());
		updateStartButton();
	}

	private void updateStartButton() {
		if (startButton == null) {
			return;
		}
		startButton.visible = !started && !spectating && !over();
		startButton.setRectangle(START_BUTTON_WIDTH, START_BUTTON_HEIGHT, startButtonX(), startButtonY());
	}

	public void update(SMinigameSyncPacket packet) {
		boolean ownClear = !packet.open() && packet.self().clears() > clears;
		boolean opponentClear = !packet.open() && packet.opponent().clears() > opponentClears;
		int previousScore = score;
		int previousResult = result;
		boolean previousFinished = finished;
		ownBoard.applySync(packet.board().orElse(null), packet.self().steps(), packet.self().locked(), ownClear);
		opponentBoard.applySync(
				packet.opponentBoard().orElse(null),
				packet.opponent().steps(),
				packet.opponent().locked(),
				opponentClear);
		score = packet.self().score();
		moves = packet.self().moves();
		clears = packet.self().clears();
		opponentScore = packet.opponent().score();
		opponentMoves = packet.opponent().moves();
		opponentClears = packet.opponent().clears();
		rewards = packet.self().rewards();
		moveLimit = packet.moveLimit();
		timeRemaining = packet.timeRemaining();
		playerName = packet.playerName();
		opponentName = packet.opponentName();
		result = packet.result();
		started = packet.self().started();
		spectators = packet.self().spectators();
		selfPath = packet.self().path();
		opponentPath = packet.opponent().path();
		spectating = spectating || packet.spectating();
		receiveMillis = Util.getMillis();
		if (packet.open()) {
			finished = packet.finished();
			path.clear();
			goalPanel.setDisplays(packet.self().goals());
		} else {
			finished = finished || packet.finished();
			if (finished) {
				path.clear();
			} else if (prunePath()) {
				sendPathUpdate();
			}
		}
		if (goalPanel.applyProgress(packet.self().goalProgress()) && !packet.open()) {
			playUi(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.6f, 0.6f);
		}
		if (!packet.open() && score > previousScore) {
			playUi(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 0.7f);
		}
		if (result != previousResult) {
			playResultSound(result);
		} else if (!previousFinished && finished) {
			playUi(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.6f);
		}
		updateStartButton();
	}

	private boolean prunePath() {
		int valid = PathRules.validPrefixLength(path, ownBoard);
		if (valid >= path.size()) {
			return false;
		}
		path.subList(valid, path.size()).clear();
		return true;
	}

	private boolean versus() {
		return !opponentName.isEmpty();
	}

	private int goalPanelLeft() {
		return goalPanel.reservedWidth();
	}

	private int cellSize() {
		int count = versus() ? 2 : 1;
		int gap = count > 1 ? VS_GAP : 0;
		int available = width - 32 - gap * (count - 1);
		int max = Math.min(MAX_CELL, available / (count * MinigameConfig.SIZE));
		int reserved = goalPanelLeft();
		if (reserved > 0) {
			int centred = (width - reserved * 2) / (count * MinigameConfig.SIZE);
			max = Math.min(
					Math.min(max, Math.max(centred, max * 2 / 3)),
					(width - reserved) / (count * MinigameConfig.SIZE));
		}
		return Math.max(6, max);
	}

	private int boardX(int index, int cell) {
		int gridSize = MinigameConfig.SIZE * cell;
		if (!versus()) {
			return Math.max((width - gridSize) / 2, goalPanelLeft());
		}
		int total = gridSize * 2 + VS_GAP;
		return (width - total) / 2 + index * (gridSize + VS_GAP);
	}

	private int boardY(int cell) {
		return (height - MinigameConfig.SIZE * cell) / 2 + 16;
	}

	private boolean inIntro() {
		return !spectating && versus() && Util.getMillis() - introStart < INTRO_MS;
	}

	private boolean inputBlocked() {
		return !started || inIntro() || finished || ownBoard.busy();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean flag) {
		if (spectating) {
			return false;
		}
		if (event.button() == 1) {
			if (!path.isEmpty()) {
				path.clear();
				playUi(SoundEvents.NOTE_BLOCK_HAT.value(), 0.5f, 0.4f);
				CUpdatePathPacket.send(List.of());
			}
			return true;
		}
		if (event.button() == 0 && !inputBlocked()) {
			int cell = ownBoard.cellAt(event.x(), event.y(), 0);
			if (cell >= 0 && !ownBoard.locked(cell)) {
				PieceType type = ownBoard.piece(cell).type();
				if (type.unlinkable()) {
					if (type == PieceType.LOOTBOX) {
						CSubmitPathPacket.send(List.of(cell));
						playUi(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 0.7f);
					}
					return true;
				}
				if (!PathRules.canAppend(List.of(), ownBoard, cell)) {
					return true;
				}
				path.clear();
				path.add(cell);
				playStepSound();
				sendPathUpdate();
				return true;
			}
		}
		return super.mouseClicked(event, flag);
	}

	private void sendPathUpdate() {
		CUpdatePathPacket.send(List.copyOf(path));
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (spectating) {
			return false;
		}
		if (event.button() == 0 && !inputBlocked() && !path.isEmpty()) {
			int cell = ownBoard.cellAt(event.x(), event.y(), Math.max(1, cellSize() / 5));
			if (cell >= 0) {
				if (path.size() >= 2 && cell == path.get(path.size() - 2)) {
					path.removeLast();
					playUi(SoundEvents.NOTE_BLOCK_HAT.value(), 0.6f, 0.4f);
					sendPathUpdate();
				} else if (PathRules.canAppend(path, ownBoard, cell)) {
					path.add(cell);
					playStepSound();
					sendPathUpdate();
				}
			}
			return true;
		}
		return super.mouseDragged(event, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (spectating) {
			return false;
		}
		if (event.button() == 0 && !path.isEmpty()) {
			if (!finished && path.size() >= PathRules.MIN_LENGTH) {
				CSubmitPathPacket.send(List.copyOf(path));
			}
			path.clear();
			CUpdatePathPacket.send(List.of());
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public void onClose() {
		CQuitMinigamePacket.send();
		super.onClose();
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		goalPanel.layout(width);
		int cell = cellSize();
		int oy = boardY(cell);
		int remaining = -1;
		if (timeRemaining >= 0) {
			remaining = timeRemaining - (int) ((Util.getMillis() - receiveMillis) / 1000);
			if (remaining <= 0) {
				remaining = 0;
				finished = true;
			}
		}

		boolean vs = versus();
		ownBoard.layout(boardX(0, cell), oy, cell);
		opponentBoard.layout(vs ? boardX(1, cell) : boardX(0, cell), oy, cell);
		graphics.centeredText(
				font,
				spectating ? Component.translatable("gui.fruitfulfun.minigame.spectatingTitle") : title,
				width / 2,
				16,
				0xFFFFFFFF);
		if (spectating) {
			if (remaining >= 0) {
				graphics.centeredText(
						font,
						Component.translatable("gui.fruitfulfun.minigame.time", remaining),
						width / 2,
						34,
						0xFFFFFFFF);
			}
			graphics.centeredText(
					font,
					boardHeader(playerName, score, moves),
					ownBoard.centerX(),
					oy - 12,
					0xFF66FF66);
			if (vs) {
				graphics.centeredText(
						font,
						boardHeader(opponentName, opponentScore, opponentMoves),
						opponentBoard.centerX(),
						oy - 12,
						0xFFFF8888);
			}
		} else if (vs) {
			graphics.centeredText(
					font,
					Component.translatable("gui.fruitfulfun.minigame.status", moves, moveLimit, remaining),
					width / 2,
					34,
					0xFFFFFFFF);
			graphics.centeredText(
					font,
					Component.translatable("gui.fruitfulfun.minigame.you", score),
					ownBoard.centerX(),
					oy - 12,
					0xFF66FF66);
			graphics.centeredText(
					font,
					Component.translatable("gui.fruitfulfun.minigame.opponentHeader", opponentName, opponentScore),
					opponentBoard.centerX(),
					oy - 12,
					0xFFFF8888);
		} else if (started) {
			Component hud = remaining < 0
					? Component.translatable("gui.fruitfulfun.minigame.hud.untimed", score, moves, moveLimit)
					: Component.translatable("gui.fruitfulfun.minigame.hud", score, moves, moveLimit, remaining);
			graphics.centeredText(font, hud, width / 2, 34, 0xFFFFFFFF);
		}
		ownBoard.render(graphics, spectating ? selfPath : path);
		if (vs) {
			opponentBoard.render(graphics, opponentPath);
		}

		if (started && !over()) {
			goalPanel.renderPanel(graphics, boardY(cell), height - boardY(cell) - 40);
		}
		renderRewards(graphics);

		if (inIntro()) {
			int step = (int) ((Util.getMillis() - introStart) / 500);
			if (step > introSound) {
				introSound = step;
				playUi(SoundEvents.NOTE_BLOCK_PLING.value(), step >= 3 ? 2f : 1f, 0.6f);
			}
			renderIntro(graphics);
		}

		if (over()) {
			renderFinishOverlay(graphics, finishOverlayMessage());
		} else if (!started) {
			renderStartOverlay(graphics);
		}
		if (!over() && (started || spectating) && InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_TAB)) {
			renderSpectators(graphics);
		}

		super.extractRenderState(graphics, mouseX, mouseY, a);
	}

	public static void showSpectatorToast(SMinigameSpectatorPacket packet) {
		SystemToast.add(
				Minecraft.getInstance().getToastManager(),
				MINIGAME_TOAST,
				Component.translatable("gui.fruitfulfun.minigame"),
				Component.translatable(
						packet.joined()
								? "gui.fruitfulfun.minigame.spectatorJoined"
								: "gui.fruitfulfun.minigame.spectatorLeft",
						packet.name()));
	}

	private void renderSpectators(GuiGraphicsExtractor graphics) {
		int count = spectators.size();
		int rows = Math.min(count, TAB_LIST_ROWS);
		int columns = Math.max(1, (count + TAB_LIST_ROWS - 1) / TAB_LIST_ROWS);
		int listWidth = columns * TAB_LIST_COLUMN;
		int x = width / 2 - listWidth / 2;
		int y = 10;
		graphics.fill(x - 1, y - 1, x + listWidth + 1, y + 10, 0x80000000);
		graphics.centeredText(
				font,
				Component.translatable("gui.fruitfulfun.minigame.spectators", count),
				width / 2,
				y,
				0xFFFFFFFF);
		int listY = y + 11;
		if (rows > 0) {
			graphics.fill(x - 1, listY - 1, x + listWidth + 1, listY + rows * 9 + 1, 0x80000000);
			for (int i = 0; i < count; i++) {
				int column = i / rows;
				int row = i % rows;
				graphics.text(
						font,
						spectators.get(i),
						x + column * TAB_LIST_COLUMN + 1,
						listY + row * 9,
						0xFFFFFFFF);
			}
		}
	}

	private boolean over() {
		if (!spectating) {
			return finished;
		}
		return versus() ? result != MinigameSession.RESULT_NONE : finished;
	}

	private Component finishOverlayMessage() {
		if (!spectating) {
			return finishMessage();
		}
		if (versus()) {
			return Component.translatable(
					"gui.fruitfulfun.minigame.spectate.finished",
					playerName,
					score,
					opponentName,
					opponentScore);
		}
		return Component.translatable("gui.fruitfulfun.minigame.finished", score);
	}

	private Component boardHeader(String name, int score, int moves) {
		return Component.translatable("gui.fruitfulfun.minigame.boardHeader", name, score, moves, moveLimit);
	}

	private int startBlockHeight() {
		return goalPanel.overlayHeight();
	}

	private int startOverlayTop() {
		int total = font.lineHeight * 2 + startBlockHeight() + START_BUTTON_HEIGHT + 34;
		return Math.max(4, (height - total) / 2);
	}

	private int startEntriesTop() {
		return startOverlayTop() + font.lineHeight * 2 + 4;
	}

	private int startButtonX() {
		return (width - START_BUTTON_WIDTH) / 2;
	}

	private int startButtonY() {
		return startEntriesTop() + startBlockHeight() + 10;
	}

	private void renderStartOverlay(GuiGraphicsExtractor graphics) {
		graphics.fill(0, 0, width, height, 0x80000000);
		int top = startOverlayTop();
		graphics.centeredText(font, title, width / 2, top, 0xFFFFFFFF);
		graphics.centeredText(
				font,
				Component.translatable("gui.fruitfulfun.minigame.goal.header"),
				width / 2,
				top + font.lineHeight + 4,
				0xFFFFD700);
		renderGoalEntries(graphics, startEntriesTop(), goalPanel.count());
	}

	private void renderFinishOverlay(GuiGraphicsExtractor graphics, Component message) {
		graphics.fill(0, 0, width, height, 0x80000000);
		Component close = Component.translatable("gui.fruitfulfun.minigame.close");
		if (goalPanel.isEmpty()) {
			graphics.centeredText(font, message, width / 2, height / 2 - 20, 0xFFFFFFFF);
			graphics.centeredText(font, close, width / 2, height / 2, 0xFFCCCCCC);
			return;
		}
		int rows = goalPanel.count();
		int available = height - 40;
		int limit = 0;
		int blockHeight = 0;
		while (limit < rows) {
			int rowHeight = goalPanel.rowHeight(limit, true);
			if (blockHeight + rowHeight > available) {
				break;
			}
			blockHeight += rowHeight;
			limit++;
		}
		int total = font.lineHeight * 3 + blockHeight + 24;
		int top = Math.max(4, (height - total) / 2);
		graphics.centeredText(font, message, width / 2, top, 0xFFFFFFFF);
		graphics.centeredText(font, goalsResult(), width / 2, top + font.lineHeight + 2, 0xFFFFD700);
		int entriesTop = top + font.lineHeight * 2 + 6;
		renderGoalEntries(graphics, entriesTop, limit);
		graphics.centeredText(font, close, width / 2, entriesTop + blockHeight + 6, 0xFFCCCCCC);
	}

	private void renderGoalEntries(GuiGraphicsExtractor graphics, int top, int limit) {
		int rowWidth = goalPanel.width();
		goalPanel.renderOverlay(graphics, (width - rowWidth) / 2, top, limit);
	}

	private Component goalsResult() {
		return Component.translatable(
				"gui.fruitfulfun.minigame.goals_result",
				goalPanel.doneCount(),
				goalPanel.count());
	}

	private void renderRewards(GuiGraphicsExtractor graphics) {
		int size = Math.min(rewards.size(), REWARD_DISPLAY_MAX);
		if (size <= 0) {
			return;
		}
		int gap = 2;
		int step = 16 + gap;
		int rows = (size + REWARD_PER_ROW - 1) / REWARD_PER_ROW;
		int bottom = height - 4;
		for (int i = 0; i < size; i++) {
			int row = i / REWARD_PER_ROW;
			int col = i % REWARD_PER_ROW;
			int countInRow = Math.min(REWARD_PER_ROW, size - row * REWARD_PER_ROW);
			int startX = (width - (countInRow * step - gap)) / 2;
			int px = startX + col * step;
			int py = bottom - (rows - row) * step;
			graphics.fill(px - 1, py - 1, px + 17, py + 17, 0x80000000);
			graphics.fakeItem(rewards.get(i), px, py);
			graphics.itemDecorations(font, rewards.get(i), px, py);
		}
		int labelY = bottom - rows * step - 11;
		graphics.centeredText(
				font,
				Component.translatable("gui.fruitfulfun.minigame.rewards"),
				width / 2,
				labelY,
				0xFFFFD700);
	}

	private void renderIntro(GuiGraphicsExtractor graphics) {
		long elapsed = Util.getMillis() - introStart;
		Component text;
		if (elapsed < 500) {
			text = Component.literal("3");
		} else if (elapsed < 1000) {
			text = Component.literal("2");
		} else if (elapsed < 1500) {
			text = Component.literal("1");
		} else {
			text = Component.translatable("gui.fruitfulfun.minigame.go");
		}
		graphics.pose().pushMatrix();
		graphics.pose().translate(width / 2f, height / 2f - 16f);
		graphics.pose().scale(4f);
		graphics.centeredText(font, text, 0, 0, 0xFFFFDD00);
		graphics.pose().popMatrix();
	}

	private void playStepSound() {
		playUi(SoundEvents.NOTE_BLOCK_HAT.value(), 1f + Math.min(path.size(), 12) * 0.05f, 0.5f);
	}

	private void playResultSound(int result) {
		switch (result) {
			case MinigameSession.RESULT_WIN -> playUi(SoundEvents.PLAYER_LEVELUP, 1f, 0.7f);
			case MinigameSession.RESULT_LOSE -> playUi(SoundEvents.NOTE_BLOCK_BASS.value(), 0.8f, 0.9f);
			case MinigameSession.RESULT_TIE -> playUi(SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 0.8f);
			default -> {
			}
		}
	}

	private static void playUi(SoundEvent sound, float pitch, float volume) {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
	}

	private Component finishMessage() {
		if (!versus()) {
			return Component.translatable("gui.fruitfulfun.minigame.finished", score);
		}
		return switch (result) {
			case MinigameSession.RESULT_WIN -> Component.translatable("gui.fruitfulfun.minigame.win", score);
			case MinigameSession.RESULT_LOSE -> Component.translatable("gui.fruitfulfun.minigame.lose", score);
			case MinigameSession.RESULT_TIE -> Component.translatable("gui.fruitfulfun.minigame.tie", score);
			default -> Component.translatable("gui.fruitfulfun.minigame.waiting", score);
		};
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}