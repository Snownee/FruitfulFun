package snownee.fruits.bee;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import snownee.fruits.FruitType;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.bee.network.CInspectBeePacket;
import snownee.fruits.bee.network.InspectTarget;
import snownee.fruits.bee.network.SInspectBeeReplyPacket;

public class InspectorClientHandler {
	@Nullable
	public static InspectTarget inspectingBee;
	private static int hoverTicks;

	public static void tick(Minecraft mc) {
		if (inspectingBee == null || mc.level == null) {
			return;
		}
		InspectTarget target = InspectTarget.find(mc.level, mc.hitResult);
		if (target == null || !Objects.equals(target, inspectingBee) || !(target.getEntity(mc.level) instanceof Bee bee) || bee.isDeadOrDying()) {
			reset();
			return;
		}
		if (++hoverTicks == 10) {
			CInspectBeePacket.I.sendToServer(inspectingBee::toNetwork);
			reset();
		}
	}

	public static void startInspecting(InspectTarget target) {
		reset();
		inspectingBee = target;
	}

	public static void reset() {
		inspectingBee = null;
		hoverTicks = 0;
	}

	public static void writeToBook(LocalPlayer player, List<Trait> traits, List<String> pollens, List<SInspectBeeReplyPacket.GeneRecord> genes) {
		ItemStack stack = player.getOffhandItem();
		if (!stack.is(Items.WRITABLE_BOOK)) {
			return;
		}
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("pages") && tag.getList("pages", Tag.TAG_STRING).size() >= 100 - 3) {
			player.displayClientMessage(Component.translatable("tip.fruitfulfun.bookIsFull"), true);
			return;
		}
		player.openItemGui(stack, InteractionHand.OFF_HAND);
		if (!(Minecraft.getInstance().screen instanceof BookEditScreen screen)) {
			return;
		}
		List<String> pages = screen.pages;
		if (!pages.isEmpty() && pages.get(pages.size() - 1).isBlank()) {
			pages.remove(pages.size() - 1);
		}
		screen.currentPage = pages.size();
		screen.isModified = true;
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
			Item item = FruitType.getFruitOrDefault(pollen);
			if (item == Items.AIR) {
				lines.add("- " + StringUtils.capitalize(pollen.replace('_', ' ')));
			} else {
				lines.add("- " + item.getDescription().getString());
			}
		}
		if (pollens.isEmpty()) {
			lines.add(I18n.get("text.fruitfulfun.pollen.none"));
		}
		pages.add(Joiner.on('\n').join(lines));
		lines.clear();

		lines.add(I18n.get("text.fruitfulfun.trait"));
		for (Trait trait : traits) {
			String name = I18n.get("text.fruitfulfun.trait." + trait.name());
			String desc = I18n.get("text.fruitfulfun.trait." + trait.name() + ".desc");
			lines.add(I18n.get("text.fruitfulfun.trait.pair", name, desc));
		}
		if (traits.isEmpty()) {
			lines.add(I18n.get("text.fruitfulfun.trait.none"));
		}
		pages.add(Joiner.on('\n').join(lines));
		lines.clear();

		lines.add(I18n.get("text.fruitfulfun.gene"));
		for (SInspectBeeReplyPacket.GeneRecord gene : genes) {
			String gene1 = "" + gene.code() + gene.high();
			String gene2 = "" + gene.code() + gene.low();
			lines.add(I18n.get("text.fruitfulfun.gene.pair", gene1, gene2));
		}
		pages.add(Joiner.on('\n').join(lines));
		lines.clear();
	}

	public static boolean startUsing() {
		Level level = Minecraft.getInstance().level;
		InspectTarget target = InspectTarget.find(level, Minecraft.getInstance().hitResult);
		if (target != null && target.getEntity(level) instanceof Bee) {
			InspectorClientHandler.startInspecting(target);
			return true;
		}
		return false;
	}
}
