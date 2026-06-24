package snownee.fruits.bee;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.Hooks;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.bee.network.CInspectTargetPacket;
import snownee.fruits.bee.network.InspectAction;
import snownee.fruits.bee.network.InspectTarget;
import snownee.fruits.bee.network.SInspectBeeReplyPacket;
import snownee.fruits.compat.jade.JadeCompat;
import snownee.fruits.duck.FFPlayer;

public class InspectorClientHandler {
	public static final int ANALYZE_TICKS = 12;
	@Nullable
	public static InspectTarget inspectingTarget;
	private static int hoverTicks;
	private static boolean jadeHint = true;
	private static boolean holdAlt;
	private static long holdAltStart;
	private static int pageNow;

	public static void tick(Minecraft mc) {
		if (mc.level == null || mc.player == null) {
			return;
		}
		if (!mc.player.isUsingItem() || !BeeModule.INSPECTOR.is(mc.player.getUseItem())) {
			reset();
			return;
		}
		long millis = Util.getMillis();
		boolean alt = Screen.hasAltDown();
		if (!holdAlt && alt) {
			holdAltStart = millis;
		} else if (holdAlt && !alt) {
			if (millis - holdAltStart < 500) {
				pageNow += Screen.hasControlDown() ? -1 : 1;
				pageNow = Math.floorMod(pageNow, 3);
			}
		}
		holdAlt = alt;
		InspectTarget target = InspectTarget.find(mc.level, mc.hitResult);
		InspectAction action = InspectAction.get(target, mc.level);
		if (target == null || action == null) {
			reset();
			return;
		}
		if (!Objects.equals(target, inspectingTarget)) {
			hoverTicks = 0;
			if (action.recommendJade && !Hooks.jade) {
				mc.player.sendOverlayMessage(Component.translatable("tip.fruitfulfun.analyzing"));
				if (jadeHint && !mc.player.getOffhandItem().is(Items.WRITABLE_BOOK)) {
					jadeHint = false;
					mc.player.sendSystemMessage(Component.translatable("tip.fruitfulfun.recommendJade"));
				}
			}
		}
		inspectingTarget = target;
		if (++hoverTicks == ANALYZE_TICKS) {
			CInspectTargetPacket.send(inspectingTarget);
			if (Hooks.jade) {
				JadeCompat.ensureVisibility(target.getClass() == InspectTarget.EntityTarget.class);
			}
		}
	}

	public static void reset() {
		inspectingTarget = null;
		hoverTicks = 0;
	}

	public static void writeToBook(
			LocalPlayer player,
			List<Trait> traits,
			List<String> pollens,
			List<SInspectBeeReplyPacket.GeneRecord> genes) {
		ItemStack stack = player.getOffhandItem();
		if (!stack.is(Items.WRITABLE_BOOK)) {
			return;
		}
		WritableBookContent bookContent = stack.getOrDefault(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY);
		if (bookContent.pages().size() >= 100 - 3) {
			player.sendOverlayMessage(Component.translatable("tip.fruitfulfun.bookIsFull"));
			return;
		}
		player.openItemGui(stack, InteractionHand.OFF_HAND);
		if (!(Minecraft.getInstance().screen instanceof BookEditScreen screen)) {
			return;
		}
		List<String> pages = screen.pages;
		if (!pages.isEmpty() && pages.getLast().isBlank()) {
			pages.removeLast();
		}
		screen.currentPage = pages.size();
		List<String> lines = Lists.newArrayList();
//		// Time: %s/%s %s:%s:%s
//		Calendar calendar = Calendar.getInstance();
//		int month = calendar.get(Calendar.MONTH) + 1;
//		int day = calendar.get(Calendar.DAY_OF_MONTH);
//		int hour = calendar.get(Calendar.HOUR_OF_DAY);
//		int minute = calendar.get(Calendar.MINUTE);
//		int second = calendar.get(Calendar.SECOND);
//		lines.add(I18n.get("text.fruitfulfun.time", month, day, hour, minute, second));

		lines.add(I18n.get("text.fruitfulfun.pollen"));
		for (String pollen : pollens) {
			Block block = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(pollen));
			if (block == Blocks.AIR) {
				lines.add("- " + StringUtils.capitalize(pollen.replace('_', ' ')));
			} else {
				lines.add("- " + block.getName().getString());
			}
		}
		if (pollens.isEmpty()) {
			lines.add(I18n.get("text.fruitfulfun.pollen.none"));
		}
		pages.add(String.join("\n", lines));
		lines.clear();

		lines.add(I18n.get("text.fruitfulfun.trait"));
		for (Trait trait : traits) {
			String name = trait.getDisplayName().getString();
			String desc = trait.getDescription().getString();
			lines.add(I18n.get("text.fruitfulfun.trait.pair", name, desc));
		}
		if (traits.isEmpty()) {
			lines.add(I18n.get("text.fruitfulfun.trait.none"));
		}
		pages.add(String.join("\n", lines));
		lines.clear();

		lines.add(I18n.get("text.fruitfulfun.gene"));
		boolean hasDesc = false;
		FFPlayer ffPlayer = FFPlayer.of(player);
		for (SInspectBeeReplyPacket.GeneRecord gene : genes) {
			String desc = ffPlayer.fruits$getGeneDesc(gene.code());
			if (!desc.isEmpty()) {
				hasDesc = true;
				break;
			}
		}
		for (SInspectBeeReplyPacket.GeneRecord gene : genes) {
			String code = ffPlayer.fruits$getGeneName(gene.code());
			if (hasDesc) {
				String desc = ffPlayer.fruits$getGeneDesc(gene.code());
				if (desc.isEmpty()) {
					lines.add(I18n.get("text.fruitfulfun.gene.unnamed", code));
				} else {
					lines.add(desc);
				}
			}
			String gene1 = code + gene.high();
			String gene2 = code + gene.low();
			lines.add(I18n.get("text.fruitfulfun.gene.pair", gene1, gene2));
		}
		pages.add(String.join("\n", lines));
		lines.clear();
	}

	public static boolean canUse() {
		Level level = Minecraft.getInstance().level;
		InspectTarget target = InspectTarget.find(level, Minecraft.getInstance().hitResult);
		return target != null && target.getEntity(level) instanceof Bee;
	}

	public static int getPageNow() {
		return pageNow;
	}

	public static void setPageNow(int pageNow) {
		InspectorClientHandler.pageNow = Math.floorMod(pageNow, 3);
	}

	public static boolean isAnalyzing() {
		return inspectingTarget != null && hoverTicks < ANALYZE_TICKS;
	}

	public static int getHoverTicks() {
		return hoverTicks;
	}
}
