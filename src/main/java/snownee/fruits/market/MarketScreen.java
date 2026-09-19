package snownee.fruits.market;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.market.network.CSetOrderPacket;

public class MarketScreen extends AbstractContainerScreen<MarketMenu> {
	private static final int CATALOG_COLUMNS = 9;
	private static final int CATALOG_ROWS = 4;
	private static final int CATALOG_CELL = 18;
	private static final int CATALOG_PAD = 8;
	private static final int CATALOG_WIDTH = CATALOG_COLUMNS * CATALOG_CELL + CATALOG_PAD * 2;
	private static final int CATALOG_HEIGHT = CATALOG_ROWS * CATALOG_CELL + CATALOG_PAD * 2 + 12;
	private static final int CONTAINER_ROWS = 3;
	private static final int CONTAINER_WIDTH = 176;
	private static final Identifier CONTAINER_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
	private static final int QUANTITY_WIDTH = 150;
	private static final int QUANTITY_HEIGHT = 100;
	private static final int SLIDER_WIDTH = 110;
	private static final int SLIDER_HEIGHT = 8;
	private static final int BUTTON_WIDTH = 64;

	private BlockPos pos = BlockPos.ZERO;
	private final NonNullList<ItemStack> orders = NonNullList.withSize(MarketMenu.MARKET_SLOTS, ItemStack.EMPTY);
	private List<ItemStack> catalog = List.of();
	private long money;

	private boolean orderMode;
	private boolean catalogOpen;
	private int catalogSlot = -1;
	private int catalogScroll;
	private @Nullable Button orderModeButton;

	private boolean quantityOpen;
	private int quantitySlot = -1;
	private ItemStack quantityItem = ItemStack.EMPTY;
	private int lastQuantity = 1;
	private boolean sliderDragging;
	private @Nullable EditBox quantityEdit;
	private @Nullable Button confirmButton;
	private @Nullable Button cancelButton;

	public MarketScreen(MarketMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, MarketMenu.IMAGE_WIDTH, MarketMenu.IMAGE_HEIGHT);
	}

	public BlockPos pos() {
		return pos;
	}

	public void setCatalog(BlockPos pos, List<ItemStack> catalog, long money, List<ItemStack> orders) {
		this.pos = pos;
		this.catalog = List.copyOf(catalog);
		setSync(money, orders);
	}

	public void setSync(long money, List<ItemStack> orders) {
		this.money = money;
		for (int i = 0; i < MarketMenu.MARKET_SLOTS; i++) {
			this.orders.set(i, i < orders.size() ? orders.get(i) : ItemStack.EMPTY);
		}
	}

	@Override
	protected void init() {
		super.init();
		orderModeButton = addRenderableWidget(Button.builder(orderModeMessage(), $ -> {
			orderMode = !orderMode;
			updateOrderModeButton();
		}).bounds(leftPos, Math.max(2, topPos - 22), 60, 20).build());
		EditBox editBox = new EditBox(font, 0, 0, QUANTITY_WIDTH - 40, 18, Component.empty());
		editBox.setMaxLength(5);
		quantityEdit = editBox;
		confirmButton = Button.builder(Component.translatable("gui.fruitfulfun.market.confirm"), $ -> confirmQuantity())
				.bounds(0, 0, BUTTON_WIDTH, 20).build();
		cancelButton = Button.builder(Component.translatable("gui.fruitfulfun.market.cancel"), $ -> closeQuantity())
				.bounds(0, 0, BUTTON_WIDTH, 20).build();
	}

	private static Component orderModeMessage() {
		return Component.translatable("gui.fruitfulfun.market.order_mode.off");
	}

	private Component orderModeMessage(boolean on) {
		return Component.translatable(on ? "gui.fruitfulfun.market.order_mode.on" : "gui.fruitfulfun.market.order_mode.off");
	}

	private void updateOrderModeButton() {
		if (orderModeButton != null) {
			orderModeButton.setMessage(orderModeMessage(orderMode));
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		int topHeight = CONTAINER_ROWS * 18 + 17;
		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				CONTAINER_BACKGROUND,
				leftPos,
				topPos,
				0,
				0,
				CONTAINER_WIDTH,
				topHeight,
				256,
				256);
		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				CONTAINER_BACKGROUND,
				leftPos,
				topPos + topHeight,
				0,
				126,
				CONTAINER_WIDTH,
				96,
				256,
				256);
		slotBackground(graphics, leftPos + MarketMenu.CURRENCY_X, topPos + MarketMenu.CURRENCY_Y);
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		if (catalogOpen || quantityOpen) {
			return;
		}
		if (hoveredSlot != null && hoveredSlot.index == MarketMenu.CURRENCY_SLOT) {
			graphics.setComponentTooltipForNextFrame(font, currencyTooltip(), mouseX, mouseY);
			return;
		}
		if (hoveredSlot != null && hoveredSlot.index < MarketMenu.MARKET_SLOTS) {
			ItemStack stack = hoveredSlot.getItem();
			if (stack.isEmpty()) {
				ItemStack order = orders.get(hoveredSlot.index);
				if (!order.isEmpty()) {
					graphics.setComponentTooltipForNextFrame(font, tooltipFor(order), mouseX, mouseY);
				}
			} else {
				graphics.setComponentTooltipForNextFrame(font, tooltipFor(stack), mouseX, mouseY);
			}
			return;
		}
		super.extractTooltip(graphics, mouseX, mouseY);
	}

	private HolderLookup.Provider registries() {
		return minecraft.level.registryAccess();
	}

	private List<Component> tooltipFor(ItemStack stack) {
		List<Component> lines = new ArrayList<>(getTooltipFromContainerItem(stack));
		long unit = MarketCurrency.unitPrice(stack, registries());
		if (unit > 0) {
			lines.add(Component.translatable("gui.fruitfulfun.market.unit_price", MarketCurrency.format(unit)));
			if (stack.getCount() > 1) {
				lines.add(Component.translatable(
						"gui.fruitfulfun.market.total_price",
						MarketCurrency.format(unit * stack.getCount())));
			}
		}
		return lines;
	}

	private List<Component> currencyTooltip() {
		List<Component> lines = new ArrayList<>();
		lines.add(Component.translatable("gui.fruitfulfun.market.currency_info"));
		FFCommonConfig.currencyValues.entrySet().stream()
				.sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
						.thenComparing(Map.Entry::getKey))
				.forEach(entry -> {
					Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(entry.getKey()));
					if (item != null) {
						lines.add(Component.translatable(
								"gui.fruitfulfun.market.currency_entry",
								new ItemStack(item).getHoverName(),
								MarketCurrency.formatCompact(entry.getValue())));
					}
				});
		return lines;
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractLabels(graphics, mouseX, mouseY);
		String moneyText = MarketCurrency.format(money);
		graphics.text(
				font,
				moneyText,
				CONTAINER_WIDTH - 8 - font.width(moneyText),
				titleLabelY,
				0xFF404040,
				false);
	}

	private void centeredText(GuiGraphicsExtractor graphics, Component text, int centerX, int y, int color) {
		graphics.text(font, text, centerX - font.width(text) / 2, y, color, false);
	}

	@Override
	protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
		super.extractSlot(graphics, slot, mouseX, mouseY);
		if (slot.index < MarketMenu.MARKET_SLOTS && slot.getItem().isEmpty()) {
			ItemStack order = orders.get(slot.index);
			if (!order.isEmpty()) {
				graphics.fakeItem(order, slot.x, slot.y);
				graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80808080);
				graphics.itemDecorations(font, order, slot.x, slot.y);
			}
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		if (catalogOpen) {
			renderCatalog(graphics, mouseX, mouseY);
		}
		if (quantityOpen) {
			renderQuantity(graphics, mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean flag) {
		if (quantityOpen) {
			return handleQuantityClick(event);
		}
		if (catalogOpen) {
			return handleCatalogClick(event);
		}
		if (orderMode && !isOverOrderModeButton(event)) {
			if (hoveredSlot != null && hoveredSlot.index < MarketMenu.MARKET_SLOTS) {
				if (event.button() == 0) {
					openCatalog(hoveredSlot.index);
					return true;
				}
				if (event.button() == 1) {
					CSetOrderPacket.send(hoveredSlot.index, ItemStack.EMPTY);
					return true;
				}
			}
			return true;
		}
		return super.mouseClicked(event, flag);
	}

	private boolean isOverOrderModeButton(MouseButtonEvent event) {
		return orderModeButton != null && orderModeButton.isMouseOver(event.x(), event.y());
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (quantityOpen) {
			if (sliderDragging) {
				updateSlider(event.x());
			}
			return true;
		}
		if (catalogOpen) {
			return true;
		}
		if (orderMode) {
			return true;
		}
		return super.mouseDragged(event, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (quantityOpen && sliderDragging) {
			sliderDragging = false;
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (catalogOpen && !quantityOpen) {
			catalogScroll = Mth.clamp(
					catalogScroll - (int) Math.signum(scrollY) * CATALOG_COLUMNS,
					0,
					maxCatalogScroll());
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (quantityOpen) {
			if (event.isEscape()) {
				closeQuantity();
				return true;
			}
			if (event.isConfirmation()) {
				confirmQuantity();
				return true;
			}
			Objects.requireNonNull(quantityEdit).keyPressed(event);
			return true;
		}
		if (catalogOpen) {
			if (event.isEscape()) {
				catalogOpen = false;
				return true;
			}
			return true;
		}
		if (orderMode && isItemMoveKey(event)) {
			return true;
		}
		return super.keyPressed(event);
	}

	private boolean isItemMoveKey(KeyEvent event) {
		if (minecraft == null) {
			return false;
		}
		if (minecraft.options.keyDrop.matches(event)
				|| minecraft.options.keySwapOffhand.matches(event)
				|| minecraft.options.keyPickItem.matches(event)) {
			return true;
		}
		for (KeyMapping slot : minecraft.options.keyHotbarSlots) {
			if (slot.matches(event)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		if (quantityOpen) {
			return Objects.requireNonNull(quantityEdit).charTyped(event);
		}
		if (catalogOpen) {
			return true;
		}
		return super.charTyped(event);
	}

	private void openCatalog(int slot) {
		catalogSlot = slot;
		catalogOpen = true;
		quantityOpen = false;
		catalogScroll = 0;
	}

	private boolean handleCatalogClick(MouseButtonEvent event) {
		if (!insideCatalog(event.x(), event.y())) {
			catalogOpen = false;
			return true;
		}
		int count = visibleCatalogCount();
		for (int i = 0; i < count; i++) {
			int x = catalogGridLeft() + (i % CATALOG_COLUMNS) * CATALOG_CELL;
			int y = catalogGridTop() + (i / CATALOG_COLUMNS) * CATALOG_CELL;
			if (event.x() >= x && event.x() < x + 16 && event.y() >= y && event.y() < y + 16) {
				openQuantity(catalog.get(catalogScroll + i));
				return true;
			}
		}
		return true;
	}

	private boolean insideCatalog(double mouseX, double mouseY) {
		return mouseX >= catalogLeft() && mouseX < catalogLeft() + CATALOG_WIDTH
				&& mouseY >= catalogTop() && mouseY < catalogTop() + CATALOG_HEIGHT;
	}

	private boolean insideQuantity(double mouseX, double mouseY) {
		return mouseX >= quantityLeft() && mouseX < quantityLeft() + QUANTITY_WIDTH
				&& mouseY >= quantityTop() && mouseY < quantityTop() + QUANTITY_HEIGHT;
	}

	private void openQuantity(ItemStack item) {
		quantityItem = item.copyWithCount(1);
		quantitySlot = catalogSlot;
		ItemStack existing = orders.get(catalogSlot);
		int initial = existing.isEmpty() ? Mth.clamp(lastQuantity, 1, quantityMax()) : Mth.clamp(existing.getCount(), 1, quantityMax());
		EditBox editBox = Objects.requireNonNull(quantityEdit);
		editBox.setValue(Integer.toString(initial));
		editBox.setFocused(true);
		editBox.setCursorPosition(editBox.getValue().length());
		quantityOpen = true;
		sliderDragging = false;
	}

	private void closeQuantity() {
		quantityOpen = false;
		sliderDragging = false;
		EditBox editBox = quantityEdit;
		if (editBox != null) {
			editBox.setFocused(false);
		}
	}

	private void confirmQuantity() {
		if (!quantityOpen || quantityItem.isEmpty()) {
			return;
		}
		int quantity = parseQuantity();
		lastQuantity = quantity;
		CSetOrderPacket.send(quantitySlot, quantityItem.copyWithCount(quantity));
		closeQuantity();
		catalogOpen = false;
	}

	private int quantityMax() {
		return Math.max(1, quantityItem.getMaxStackSize());
	}

	private int parseQuantity() {
		EditBox editBox = Objects.requireNonNull(quantityEdit);
		try {
			return Mth.clamp(Integer.parseInt(editBox.getValue().trim()), 1, quantityMax());
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	private boolean handleQuantityClick(MouseButtonEvent event) {
		if (!insideQuantity(event.x(), event.y())) {
			closeQuantity();
			return true;
		}
		int trackX = quantityLeft() + 20;
		int trackY = quantityTop() + 54;
		if (event.x() >= trackX && event.x() <= trackX + SLIDER_WIDTH && event.y() >= trackY - 3 &&
				event.y() <= trackY + SLIDER_HEIGHT + 3) {
			sliderDragging = true;
			updateSlider(event.x());
			return true;
		}
		Button confirm = Objects.requireNonNull(confirmButton);
		Button cancel = Objects.requireNonNull(cancelButton);
		if (confirm.mouseClicked(event, false) || cancel.mouseClicked(event, false)) {
			return true;
		}
		Objects.requireNonNull(quantityEdit).mouseClicked(event, false);
		return true;
	}

	private void updateSlider(double mouseX) {
		int trackX = quantityLeft() + 20;
		int max = quantityMax();
		EditBox editBox = Objects.requireNonNull(quantityEdit);
		if (max <= 1) {
			editBox.setValue("1");
			return;
		}
		double t = Mth.clamp((mouseX - trackX) / (double) (SLIDER_WIDTH - 8), 0, 1);
		int qty = Mth.clamp((int) Math.round(1 + t * (max - 1)), 1, max);
		editBox.setValue(Integer.toString(qty));
		editBox.setCursorPosition(editBox.getValue().length());
	}

	private void renderCatalog(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.fill(0, 0, width, height, 0x80000000);
		int left = catalogLeft();
		int top = catalogTop();
		graphics.fill(left - 1, top - 1, left + CATALOG_WIDTH + 1, top + CATALOG_HEIGHT + 1, 0xFF000000);
		graphics.fill(left, top, left + CATALOG_WIDTH, top + CATALOG_HEIGHT, 0xFFC6C6C6);
		centeredText(
				graphics,
				Component.translatable("gui.fruitfulfun.market.catalog"),
				left + CATALOG_WIDTH / 2,
				top + 5,
				0xFF404040);
		catalogScroll = Mth.clamp(catalogScroll, 0, maxCatalogScroll());
		int count = visibleCatalogCount();
		for (int i = 0; i < count; i++) {
			int x = catalogGridLeft() + (i % CATALOG_COLUMNS) * CATALOG_CELL;
			int y = catalogGridTop() + (i / CATALOG_COLUMNS) * CATALOG_CELL;
			ItemStack stack = catalog.get(catalogScroll + i);
			slotBackground(graphics, x, y);
			graphics.fakeItem(stack, x, y);
			graphics.itemDecorations(font, stack, x, y);
			if (!quantityOpen && mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
				graphics.fill(x, y, x + 16, y + 16, 0x60FFFFFF);
				graphics.setComponentTooltipForNextFrame(font, tooltipFor(stack), mouseX, mouseY);
			}
		}
		if (catalog.isEmpty()) {
			centeredText(
					graphics,
					Component.translatable("gui.fruitfulfun.market.empty"),
					left + CATALOG_WIDTH / 2,
					catalogGridTop() + 26,
					0xFF808080);
		}
		int max = maxCatalogScroll();
		if (max > 0) {
			int barX = left + CATALOG_WIDTH - 5;
			int barTop = catalogGridTop();
			int barHeight = CATALOG_ROWS * CATALOG_CELL;
			graphics.fill(barX, barTop, barX + 3, barTop + barHeight, 0xFF8B8B8B);
			int handleHeight = Math.max(10, barHeight * (CATALOG_ROWS * CATALOG_COLUMNS) / Math.max(1, catalog.size()));
			int handleY = barTop + (barHeight - handleHeight) * catalogScroll / max;
			graphics.fill(barX - 1, handleY, barX + 4, handleY + handleHeight, 0xFF555555);
		}
	}

	private void renderQuantity(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.fill(0, 0, width, height, 0x80000000);
		int left = quantityLeft();
		int top = quantityTop();
		graphics.fill(left - 1, top - 1, left + QUANTITY_WIDTH + 1, top + QUANTITY_HEIGHT + 1, 0xFF000000);
		graphics.fill(left, top, left + QUANTITY_WIDTH, top + QUANTITY_HEIGHT, 0xFFC6C6C6);
		centeredText(graphics, quantityItem.getHoverName(), left + QUANTITY_WIDTH / 2, top + 6, 0xFF404040);
		EditBox editBox = Objects.requireNonNull(quantityEdit);
		editBox.setRectangle(QUANTITY_WIDTH - 40, 18, left + 20, top + 20);
		editBox.extractRenderState(graphics, mouseX, mouseY, 0);
		long unit = MarketCurrency.unitPrice(quantityItem, registries());
		centeredText(
				graphics,
				Component.translatable(
						"gui.fruitfulfun.market.total_price",
						MarketCurrency.format(unit * parseQuantity())),
				left + QUANTITY_WIDTH / 2,
				top + 40,
				0xFF404040);
		drawSlider(graphics, left + 20, top + 54);
		Button confirm = Objects.requireNonNull(confirmButton);
		Button cancel = Objects.requireNonNull(cancelButton);
		confirm.setRectangle(BUTTON_WIDTH, 20, left + 8, top + QUANTITY_HEIGHT - 26);
		cancel.setRectangle(BUTTON_WIDTH, 20, left + QUANTITY_WIDTH - BUTTON_WIDTH - 8, top + QUANTITY_HEIGHT - 26);
		confirm.extractRenderState(graphics, mouseX, mouseY, 0);
		cancel.extractRenderState(graphics, mouseX, mouseY, 0);
	}

	private void drawSlider(GuiGraphicsExtractor graphics, int x, int y) {
		int max = quantityMax();
		int qty = parseQuantityValue();
		double t = max <= 1 ? 0 : (qty - 1) / (double) (max - 1);
		int filled = (int) Math.round(t * (SLIDER_WIDTH - 8));
		graphics.fill(x, y, x + SLIDER_WIDTH, y + SLIDER_HEIGHT, 0xFF555555);
		graphics.fill(x, y, x + filled + 4, y + SLIDER_HEIGHT, 0xFF4C9A4C);
		graphics.fill(x + filled, y - 2, x + filled + 8, y + SLIDER_HEIGHT + 2, 0xFFCCCCCC);
		graphics.fill(x + filled, y - 2, x + filled + 8, y - 1, 0xFF333333);
		graphics.fill(x + filled, y + SLIDER_HEIGHT + 1, x + filled + 8, y + SLIDER_HEIGHT + 2, 0xFF333333);
	}

	private int parseQuantityValue() {
		EditBox editBox = quantityEdit;
		if (editBox == null) {
			return 1;
		}
		try {
			return Mth.clamp(Integer.parseInt(editBox.getValue().trim()), 1, quantityMax());
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	private static void slotBackground(GuiGraphicsExtractor graphics, int x, int y) {
		graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF373737);
		graphics.fill(x, y, x + 16, y + 16, 0xFF8B8B8B);
	}

	private int catalogLeft() {
		return (width - CATALOG_WIDTH) / 2;
	}

	private int catalogTop() {
		return (height - CATALOG_HEIGHT) / 2;
	}

	private int catalogGridLeft() {
		return catalogLeft() + CATALOG_PAD;
	}

	private int catalogGridTop() {
		return catalogTop() + CATALOG_PAD + 10;
	}

	private int maxCatalogScroll() {
		return Math.max(0, catalog.size() - CATALOG_COLUMNS * CATALOG_ROWS);
	}

	private int visibleCatalogCount() {
		return Math.min(Math.max(0, catalog.size() - catalogScroll), CATALOG_COLUMNS * CATALOG_ROWS);
	}

	private int quantityLeft() {
		return (width - QUANTITY_WIDTH) / 2;
	}

	private int quantityTop() {
		return (height - QUANTITY_HEIGHT) / 2;
	}
}
