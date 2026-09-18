package snownee.fruits.minigame;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.minigame.goal.MinigameGoal;
import snownee.fruits.minigame.network.PlayerSync;

public final class GoalPanel {
	public static final int ICON = 16;
	private static final int ICON_MIN = 12;
	private static final int ROW_HEIGHT = 36;
	private static final int LINE = 10;
	private static final int RULE_LINE = 10;
	private static final int REWARD_ICONS = 3;
	private static final int MIN_WIDTH = 56;
	private static final int MAX_WIDTH = 156;
	private static final int MARGIN = 8;
	private static final long HIGHLIGHT_MS = 600;
	private static final int COLOR_TEXT = 0xFFFFFFFF;
	private static final int COLOR_DONE = 0xFF66FF66;
	private static final int COLOR_PROGRESS = 0xFFCCCCCC;
	private static final int COLOR_RULE = 0xFFFFC04D;
	private static final int COLOR_HEADER = 0xFFFFD700;
	private static final int COLOR_MORE = 0xFFAAAAAA;
	private static final Component HEADER = Component.translatable("gui.fruitfulfun.minigame.goal.header");

	private record Segment(String text, Style style, int start) {
		public int end() {
			return start + text.length();
		}
	}

	private record Entry(
			ItemStack icon,
			Component description,
			List<FormattedCharSequence> lines,
			List<FormattedCharSequence> doneLines,
			List<ItemStack> rewards,
			int target,
			List<List<FormattedCharSequence>> ruleLines,
			int contentHeight,
			int height) {
	}

	private final Font font;
	private List<MinigameGoal.Display> displays = List.of();
	private List<Entry> entries = List.of();
	private int[] progress = new int[0];
	private String[] progressTexts = new String[0];
	private int[] progressWidths = new int[0];
	private boolean[] done = new boolean[0];
	private long[] doneAt = new long[0];
	private int panelWidth;
	private int cachedScreenWidth = -1;
	private boolean dirty = true;

	public GoalPanel(Font font) {
		this.font = font;
	}

	public boolean isEmpty() {
		return displays.isEmpty();
	}

	public int count() {
		return entries.size();
	}

	public int doneCount() {
		int count = 0;
		for (boolean value : done) {
			if (value) {
				count++;
			}
		}
		return count;
	}

	public void setDisplays(List<MinigameGoal.Display> displays) {
		this.displays = displays;
		this.progress = new int[displays.size()];
		this.progressTexts = new String[displays.size()];
		this.progressWidths = new int[displays.size()];
		this.done = new boolean[displays.size()];
		this.doneAt = new long[displays.size()];
		this.dirty = true;
	}

	public boolean applyProgress(List<PlayerSync.Entry> entries) {
		boolean completed = false;
		for (PlayerSync.Entry entry : entries) {
			int index = entry.index();
			progress[index] = entry.current();
			if (!done[index] && entry.current() >= displays.get(index).target()) {
				done[index] = true;
				doneAt[index] = Util.getMillis();
				completed = true;
			}
		}
		return completed;
	}

	private void updateProgressTexts() {
		for (int i = 0; i < displays.size(); i++) {
			String text = progress[i] + "/" + displays.get(i).target();
			if (!text.equals(progressTexts[i])) {
				progressTexts[i] = text;
				progressWidths[i] = font.width(text);
			}
		}
	}

	public void layout(int screenWidth) {
		updateProgressTexts();
		if (!dirty && screenWidth == cachedScreenWidth) {
			return;
		}
		cachedScreenWidth = screenWidth;
		dirty = false;
		panelWidth = measureWidth(screenWidth);
		List<Entry> built = new ArrayList<>(displays.size());
		for (MinigameGoal.Display display : displays) {
			List<FormattedCharSequence> lines = split(display.description(), panelWidth - ICON - 4, COLOR_TEXT);
			List<List<FormattedCharSequence>> ruleLines = new ArrayList<>(display.ruleTexts().size());
			for (Component ruleText : display.ruleTexts()) {
				ruleLines.add(split(ruleText, panelWidth, COLOR_RULE));
			}
			int contentHeight = ROW_HEIGHT + (lines.size() - 1) * LINE;
			int rules = 0;
			for (List<FormattedCharSequence> ruleLine : ruleLines) {
				rules += ruleLine.size();
			}
			built.add(new Entry(
					display.icon(),
					display.description(),
					lines,
					split(display.description(), panelWidth - ICON - 4, COLOR_DONE),
					display.rewards().subList(0, Math.min(display.rewards().size(), REWARD_ICONS)),
					display.target(),
					ruleLines,
					contentHeight,
					contentHeight + rules * RULE_LINE));
		}
		entries = List.copyOf(built);
	}

	private List<FormattedCharSequence> split(Component text, int width, int color) {
		Component colored = text.copy().withStyle(style -> style.withColor(color));
		String plain = colored.getString();
		if (plain.isEmpty()) {
			return List.of(FormattedCharSequence.EMPTY);
		}
		List<int[]> ranges = wrapRanges(plain, width);
		List<Segment> segments = segments(colored);
		List<FormattedCharSequence> lines = new ArrayList<>(ranges.size());
		for (int[] range : ranges) {
			MutableComponent line = Component.empty();
			for (Segment segment : segments) {
				int from = Math.max(range[0], segment.start());
				int to = Math.min(range[1], segment.end());
				if (from < to) {
					line.append(Component.literal(segment.text().substring(from - segment.start(), to - segment.start()))
							.withStyle(segment.style()));
				}
			}
			lines.add(line.getVisualOrderText());
		}
		return lines;
	}

	private static List<Segment> segments(Component text) {
		List<Segment> segments = new ArrayList<>();
		int[] offset = {0};
		text.visit((style, content) -> {
			segments.add(new Segment(content, style, offset[0]));
			offset[0] += content.length();
			return Optional.empty();
		}, Style.EMPTY);
		return segments;
	}

	private List<int[]> wrapRanges(String plain, int maxWidth) {
		BreakIterator iterator = BreakIterator.getLineInstance();
		iterator.setText(plain);
		List<int[]> ranges = new ArrayList<>();
		int lineStart = 0;
		int lastFit = 0;
		for (int boundary = iterator.first(); boundary != BreakIterator.DONE; boundary = iterator.next()) {
			if (boundary <= lineStart || font.width(plain.substring(lineStart, boundary)) <= maxWidth) {
				lastFit = Math.max(lastFit, boundary);
				continue;
			}
			int end = lastFit > lineStart ? lastFit : charFit(plain, lineStart, maxWidth);
			ranges.add(new int[]{lineStart, end});
			lineStart = end;
			while (lineStart < plain.length() && plain.charAt(lineStart) == ' ') {
				lineStart++;
			}
			lastFit = lineStart;
		}
		if (lineStart < plain.length()) {
			ranges.add(new int[]{lineStart, plain.length()});
		}
		return ranges;
	}

	private int charFit(String plain, int start, int maxWidth) {
		int end = start;
		while (end < plain.length() && font.width(plain.substring(start, end + 1)) <= maxWidth) {
			end++;
		}
		return end > start ? end : start + 1;
	}

	private int measureWidth(int screenWidth) {
		if (displays.isEmpty()) {
			return 0;
		}
		int content = 0;
		for (int i = 0; i < displays.size(); i++) {
			MinigameGoal.Display display = displays.get(i);
			content = Math.max(content, ICON + 4 + font.width(display.description()));
			int rewards = Math.min(display.rewards().size(), REWARD_ICONS) * (ICON + 2);
			content = Math.max(content, ICON + 4 + rewards + progressWidths[i]);
			for (Component ruleText : display.ruleTexts()) {
				content = Math.max(content, font.width(ruleText));
			}
		}
		int max = Math.min(MAX_WIDTH, screenWidth / 3);
		return Math.max(Math.min(MIN_WIDTH, max), Math.min(content + 8, max));
	}

	public int reservedWidth() {
		return displays.isEmpty() ? 0 : panelWidth + MARGIN * 2;
	}

	public int width() {
		return panelWidth;
	}

	public int rowHeight(int index, boolean showRules) {
		Entry entry = entries.get(index);
		return showRules ? entry.height() : entry.contentHeight();
	}

	public int height(int limit, boolean showRules) {
		int total = 0;
		for (int i = 0; i < limit; i++) {
			total += rowHeight(i, showRules);
		}
		return total;
	}

	public int overlayHeight() {
		return height(entries.size(), true);
	}

	public void renderPanel(GuiGraphicsExtractor graphics, int top, int available, int mouseX, int mouseY) {
		int count = entries.size();
		if (count == 0) {
			return;
		}
		boolean showRules = height(count, true) <= available;
		int shown = count;
		int step = 0;
		boolean compact = false;
		if (!showRules && height(count, false) > available) {
			shown = Math.min(count, Math.max(1, available / ICON_MIN));
			step = Math.min(ROW_HEIGHT, available / shown);
			compact = step < ROW_HEIGHT;
			showRules = false;
		}
		int iconSize = compact ? ICON_MIN : ICON;
		graphics.text(font, HEADER, MARGIN, top - font.lineHeight - 2, COLOR_HEADER);
		int end = renderEntries(graphics, MARGIN, top, panelWidth, shown, iconSize, step, compact, showRules, mouseX, mouseY);
		if (shown < count) {
			graphics.text(font, Component.literal("+" + (count - shown)), MARGIN, end + 2, COLOR_MORE);
		}
	}

	public void renderOverlay(GuiGraphicsExtractor graphics, int x, int top, int limit, int mouseX, int mouseY) {
		renderEntries(graphics, x, top, panelWidth, limit, ICON, 0, false, true, mouseX, mouseY);
	}

	private int renderEntries(
			GuiGraphicsExtractor graphics,
			int x,
			int top,
			int rowWidth,
			int limit,
			int iconSize,
			int compactStep,
			boolean compact,
			boolean showRules,
			int mouseX,
			int mouseY) {
		long now = Util.getMillis();
		int y = top;
		for (int i = 0; i < limit; i++) {
			Entry entry = entries.get(i);
			int step = compact ? compactStep : (showRules ? entry.height() : entry.contentHeight());
			boolean isDone = done[i];
			int color = isDone ? COLOR_DONE : COLOR_TEXT;
			if (isDone) {
				boolean highlight = now - doneAt[i] < HIGHLIGHT_MS;
				graphics.fill(x - 3, y - 2, x + rowWidth + 3, y + step - 3, highlight ? 0x80FFEE55 : 0x4022AA22);
			}
			drawItemIcon(graphics, entry.icon(), x, y + (entry.contentHeight() - iconSize) / 2, iconSize);
			int textX = x + iconSize + 4;
			if (compact) {
				String text = join(entry, i);
				graphics.text(
						font,
						font.plainSubstrByWidth(text, x + rowWidth - textX),
						textX,
						y + (step - font.lineHeight) / 2,
						color);
			} else {
				int lineY = y + 4;
				for (FormattedCharSequence line : isDone ? entry.doneLines() : entry.lines()) {
					drawHoverableText(graphics, line, textX, lineY);
					lineY += LINE;
				}
				int secondY = y + 16 + (entry.contentHeight() - ROW_HEIGHT);
				int rewardX = textX;
				for (ItemStack reward : entry.rewards()) {
					drawRewardIcon(graphics, reward, rewardX, secondY);
					if (mouseX >= rewardX - 1 && mouseX < rewardX + ICON + 1
							&& mouseY >= secondY - 1 && mouseY < secondY + ICON + 1) {
						graphics.setTooltipForNextFrame(font, reward, mouseX, mouseY);
					}
					rewardX += ICON + 2;
				}
				String text = progressTexts[i];
				graphics.text(
						font,
						text,
						x + rowWidth - progressWidths[i],
						secondY + (ICON - font.lineHeight) / 2,
						isDone ? COLOR_DONE : COLOR_PROGRESS);
				if (isDone) {
					drawCheck(graphics, x + rowWidth + 4, y + 4, COLOR_DONE);
				}
				if (showRules) {
					int ruleY = secondY + 20;
					for (List<FormattedCharSequence> ruleLine : entry.ruleLines()) {
						for (FormattedCharSequence line : ruleLine) {
							drawHoverableText(graphics, line, x, ruleY);
							ruleY += RULE_LINE;
						}
					}
				}
			}
			y += step;
		}
		return y;
	}

	private String join(Entry entry, int index) {
		return entry.description().getString() + " " + progressTexts[index];
	}

	private void drawHoverableText(GuiGraphicsExtractor graphics, FormattedCharSequence text, int x, int y) {
		graphics.textRenderer().accept(x, y, text);
	}

	private static void drawItemIcon(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y, int size) {
		float scale = size / 16f;
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(scale);
		graphics.fakeItem(stack, 0, 0);
		graphics.pose().popMatrix();
	}

	private void drawRewardIcon(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
		graphics.fill(x - 1, y - 1, x + ICON + 1, y + ICON + 1, 0x80000000);
		graphics.fakeItem(stack, x, y);
		graphics.itemDecorations(font, stack, x, y);
	}

	private static void drawCheck(GuiGraphicsExtractor graphics, int x, int y, int color) {
		graphics.fill(x, y + 2, x + 2, y + 4, color);
		graphics.fill(x + 1, y + 3, x + 3, y + 5, color);
		graphics.fill(x + 2, y + 4, x + 4, y + 6, color);
		graphics.fill(x + 3, y + 3, x + 5, y + 5, color);
		graphics.fill(x + 4, y + 2, x + 6, y + 4, color);
		graphics.fill(x + 5, y + 1, x + 7, y + 3, color);
	}
}
