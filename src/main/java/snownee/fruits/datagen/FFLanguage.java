package snownee.fruits.datagen;

import static snownee.kiwi.AbstractModule.itemTag;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class FFLanguage extends KiwiLanguageProvider {
	public FFLanguage(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(packOutput, registryLookup);
	}

	@Override
	protected void preGenerate(HolderLookup.Provider lookup, TreeMap<String, String> translationEntries) {
		generateGameObjectsEntries(lookup, translationEntries);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
		builder.add(FFItemTags.CITRUS_LOGS, "Citrus Logs");
		builder.add(FFItemTags.REDLOVE_LOGS, "Redlove Logs");
		builder.add(FFItemTags.TULIPS, "Tulips");
		builder.add(FFItemTags.GADGET_TOKEN, "Gadget Token");
		builder.add("gui.fruitfulfun.battle_table", "Battle Table");
		builder.add("gui.fruitfulfun.battle_table.left", "Left: %s");
		builder.add("gui.fruitfulfun.battle_table.right", "Right: %s");
		builder.add("gui.fruitfulfun.battle_table.sit_left", "Sit Left");
		builder.add("gui.fruitfulfun.battle_table.sit_right", "Sit Right");
		builder.add("gui.fruitfulfun.battle_table.spectate", "Spectate");
		builder.add("gui.fruitfulfun.battle_table.start", "Start");
		builder.add("gui.fruitfulfun.battle_table.waiting_start", "Waiting for left player");
		builder.add("gui.fruitfulfun.battle_table.leave", "Leave");
		builder.add("gui.fruitfulfun.battle_table.spectators", "Spectators: %s");
		builder.add("gui.fruitfulfun.battle_table.already_joined", "You are already at another battle table.");
		builder.add("gui.fruitfulfun.market", "Market");
		builder.add("gui.fruitfulfun.market.catalog", "Order");
		builder.add("gui.fruitfulfun.market.unit_price", "Price: %s");
		builder.add("gui.fruitfulfun.market.total_price", "Total: %s");
		builder.add("gui.fruitfulfun.market.order_mode.off", "Order: OFF");
		builder.add("gui.fruitfulfun.market.order_mode.on", "Order: ON");
		builder.add("gui.fruitfulfun.market.confirm", "Confirm");
		builder.add("gui.fruitfulfun.market.cancel", "Cancel");
		builder.add("gui.fruitfulfun.market.empty", "Nothing discovered yet");
		builder.add("command.fruitfulfun.market.sales.total", "Total sold in the last %s days: %s");
		builder.add("command.fruitfulfun.market.sales.item", "%s: %s");
		builder.add("command.fruitfulfun.market.sales.day", "%s days ago:");
		builder.add("command.fruitfulfun.market.sales.empty", "No sales records");
		builder.add("command.fruitfulfun.market.sales.disabled", "Market sales statistics are disabled");
		builder.add(itemTag("c:flowers/lavender"), "Lavender");
		builder.add(itemTag("c:chocolatebar"), "Chocolate Bar");
		List<String> crops = List.of(
				"tangerine",
				"grapefruit",
				"lemon",
				"pomelo",
				"apple",
				"cherry",
				"citron",
				"orange",
				"lime",
				"pomegranate",
				"citrus");
		for (String crop : crops) {
			builder.add(itemTag("c:crops/" + crop), StringUtils.capitalize(crop));
		}
	}
}
