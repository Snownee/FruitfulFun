package snownee.fruits.bee.genetics;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import snownee.fruits.bee.network.CSetGeneNamePacket;
import snownee.fruits.duck.FFPlayer;

public class EditGeneNameScreen extends Screen {
	private CycleButton<String> button;
	private EditBox nameField;
	private EditBox descField;
	private boolean changed;

	public EditGeneNameScreen() {
		super(Component.translatable("gui.fruitfulfun.editGeneName"));
	}

	@Override
	protected void init() {
		FFPlayer player = Objects.requireNonNull(FFPlayer.of(Minecraft.getInstance().player));
		int x = width / 2;
		int y = height / 2 - 10;
		addRenderableWidget(button =
				CycleButton.builder(Component::literal)
						.withValues(player.fruits$getGeneNames().keySet().stream().sorted().toList())
						.displayOnlyValue()
						.create(x - 135, y - 1, 20, 20, Component.translatable("gui.fruitfulfun.cycleGenes"), ($, code) -> {
							sendPacket();
							updateValues(code);
						}));
		addRenderableWidget(nameField =
				new EditBox(font, x - 105, y, 100, 18, Component.translatable("gui.fruitfulfun.geneName")));
		addRenderableWidget(descField =
				new EditBox(font, x + 5, y, 100, 18, Component.translatable("gui.fruitfulfun.geneDesc")));
		nameField.setMaxLength(8);
		descField.setMaxLength(12);
		updateValues(button.getValue());
		nameField.setResponder($ -> changed = true);
		descField.setResponder($ -> changed = true);
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $ -> onClose())
				.bounds(x - 100, this.height / 4 + 144, 200, 20)
				.build());
	}

	private void updateValues(String code) {
		FFPlayer player = Objects.requireNonNull(FFPlayer.of(Minecraft.getInstance().player));
		nameField.setValue(player.fruits$getGeneName(code));
		descField.setValue(player.fruits$getGeneDesc(code));
		changed = false;
	}

	@Override
	public void tick() {
		nameField.tick();
		descField.tick();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		this.renderBackground(guiGraphics);
		guiGraphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
		guiGraphics.drawString(font, nameField.getMessage(), nameField.getX(), nameField.getY() - 14, 0xFFFFFF);
		guiGraphics.drawString(font, descField.getMessage(), descField.getX(), descField.getY() - 14, 0xFFFFFF);
		super.render(guiGraphics, i, j, f);
	}

	@Override
	public void removed() {
		sendPacket();
	}

	private void sendPacket() {
		if (!changed) {
			return;
		}
		CSetGeneNamePacket.send(button.getValue(), nameField.getValue(), descField.getValue());
	}
}
