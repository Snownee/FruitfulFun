package snownee.fruits.minigame;

import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import snownee.fruits.minigame.network.CBattleTableActionPacket;
import snownee.fruits.minigame.network.SBattleTableSyncPacket;

public final class BattleTableScreen extends Screen {
	private final BlockPos pos;
	private String left = "";
	private String right = "";
	private List<String> spectators = List.of();
	private boolean leftAvailable;
	private boolean rightAvailable;
	private boolean canStart;
	private boolean canSpectate;
	private boolean waitingForLeft;
	private boolean closed;
	private @Nullable Button leftButton;
	private @Nullable Button rightButton;
	private @Nullable Button spectateButton;
	private @Nullable Button startButton;
	private @Nullable Button leaveButton;

	public BattleTableScreen(SBattleTableSyncPacket packet) {
		super(Component.translatable("gui.fruitfulfun.battle_table"));
		pos = packet.pos();
		update(packet);
	}

	public static void open(SBattleTableSyncPacket packet) {
		Minecraft minecraft = Minecraft.getInstance();
		if (packet.close()) {
			if (minecraft.screen instanceof BattleTableScreen) {
				minecraft.setScreen(null);
			}
		} else if (minecraft.screen instanceof BattleTableScreen screen) {
			screen.update(packet);
		} else {
			minecraft.setScreen(new BattleTableScreen(packet));
		}
	}

	public void update(SBattleTableSyncPacket packet) {
		left = packet.left();
		right = packet.right();
		spectators = packet.spectators();
		leftAvailable = packet.leftAvailable();
		rightAvailable = packet.rightAvailable();
		canStart = packet.canStart();
		canSpectate = packet.canSpectate();
		waitingForLeft = packet.waitingForLeft();
		updateWidgets();
	}

	@Override
	protected void init() {
		int center = width / 2;
		leftButton = addRenderableWidget(Button.builder(
						Component.translatable("gui.fruitfulfun.battle_table.sit_left"),
						_ -> CBattleTableActionPacket.send(pos, CBattleTableActionPacket.LEFT))
				.bounds(center - 128, 70, 120, 20)
				.build());
		rightButton = addRenderableWidget(Button.builder(
						Component.translatable("gui.fruitfulfun.battle_table.sit_right"),
						_ -> CBattleTableActionPacket.send(pos, CBattleTableActionPacket.RIGHT))
				.bounds(center + 8, 70, 120, 20)
				.build());
		spectateButton = addRenderableWidget(Button.builder(
						Component.translatable("gui.fruitfulfun.battle_table.spectate"),
						_ -> CBattleTableActionPacket.send(pos, CBattleTableActionPacket.SPECTATE))
				.bounds(center - 60, 100, 120, 20)
				.build());
		startButton = addRenderableWidget(Button.builder(
						Component.translatable("gui.fruitfulfun.battle_table.start"),
						_ -> CBattleTableActionPacket.send(pos, CBattleTableActionPacket.START))
				.bounds(center - 60, 130, 120, 20)
				.build());
		leaveButton = addRenderableWidget(Button.builder(
						Component.translatable("gui.fruitfulfun.battle_table.leave"),
						_ -> closeAndLeave())
				.bounds(center - 60, 160, 120, 20)
				.build());
		updateWidgets();
	}

	private void updateWidgets() {
		if (leftButton == null || rightButton == null || spectateButton == null || startButton == null || leaveButton == null) {
			return;
		}
		leftButton.visible = leftAvailable;
		rightButton.visible = rightAvailable;
		spectateButton.visible = canSpectate;
		startButton.active = canStart;
		if (waitingForLeft) {
			startButton.setTooltip(Tooltip.create(Component.translatable("gui.fruitfulfun.battle_table.waiting_start")));
		}
	}

	@Override
	public void onClose() {
		closeAndLeave();
	}

	private void closeAndLeave() {
		if (!closed) {
			closed = true;
			CBattleTableActionPacket.send(pos, CBattleTableActionPacket.LEAVE);
		}
		minecraft.setScreen(null);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		int center = width / 2;
		graphics.centeredText(font, title, center, 25, 0xFFFFFFFF);
		graphics.centeredText(
				font,
				Component.translatable("gui.fruitfulfun.battle_table.left", emptyName(left)),
				center - 70,
				50,
				0xFF66FF66);
		graphics.centeredText(
				font,
				Component.translatable("gui.fruitfulfun.battle_table.right", emptyName(right)),
				center + 70,
				50,
				0xFFFF8888);
		graphics.centeredText(
				font,
				Component.translatable("gui.fruitfulfun.battle_table.spectators", spectators.size()),
				center,
				205,
				0xFFFFFFFF);
		int y = 225;
		for (String spectator : spectators) {
			graphics.centeredText(font, spectator, center, y, 0xFFCCCCCC);
			y += 12;
		}
	}

	private static String emptyName(String name) {
		return name.isEmpty() ? "-" : name;
	}
}
