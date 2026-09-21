package snownee.fruits.command;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.market.MarketSales;
import snownee.fruits.market.MarketSalesData;

public class MarketCommand {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("market")
				.then(Commands.literal("sales")
						.executes($ -> totals($.getSource()))
						.then(Commands.literal("detail")
								.then(Commands.argument("days", IntegerArgumentType.integer(1, 365))
										.executes($ -> detail($.getSource(), IntegerArgumentType.getInteger($, "days"))))));
	}

	private static int totals(CommandSourceStack source) {
		if (!FFCommonConfig.marketStats) {
			source.sendFailure(Component.translatable("command.fruitfulfun.market.sales.disabled"));
			return 0;
		}
		ServerLevel level = source.getLevel();
		long day = MarketSales.currentDay(level);
		int days = FFCommonConfig.marketStatsDays;
		MarketSalesData data = MarketSales.data(level);
		data.prune(day, days);
		Map<ResourceKey<Item>, Long> totals = data.totals(day, days);
		long total = totals.values().stream().mapToLong(Long::longValue).sum();
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.market.sales.total", days, total), false);
		if (totals.isEmpty()) {
			source.sendSuccess(() -> Component.translatable("command.fruitfulfun.market.sales.empty"), false);
			return 0;
		}
		sorted(totals).forEach(entry -> source.sendSuccess(
				() -> Component.translatable(
						"command.fruitfulfun.market.sales.item",
						itemName(entry.getKey()),
						entry.getValue()),
				false));
		return (int) Math.min(total, Integer.MAX_VALUE);
	}

	private static int detail(CommandSourceStack source, int days) {
		if (!FFCommonConfig.marketStats) {
			source.sendFailure(Component.translatable("command.fruitfulfun.market.sales.disabled"));
			return 0;
		}
		ServerLevel level = source.getLevel();
		long day = MarketSales.currentDay(level);
		int window = FFCommonConfig.marketStatsDays;
		int count = Mth.clamp(days, 1, window);
		MarketSalesData data = MarketSales.data(level);
		data.prune(day, window);
		long total = data.totals(day, count).values().stream().mapToLong(Long::longValue).sum();
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.market.sales.total", count, total), false);
		int shown = 0;
		for (long d = day; d > day - count; d--) {
			Map<ResourceKey<Item>, Long> items = data.itemsOf(d);
			if (items.isEmpty()) {
				continue;
			}
			long ago = day - d;
			source.sendSuccess(() -> Component.translatable("command.fruitfulfun.market.sales.day", ago), false);
			sorted(items).forEach(entry -> source.sendSuccess(
					() -> Component.translatable(
							"command.fruitfulfun.market.sales.item",
							itemName(entry.getKey()),
							entry.getValue()),
					false));
			shown++;
		}
		if (shown == 0) {
			source.sendSuccess(() -> Component.translatable("command.fruitfulfun.market.sales.empty"), false);
		}
		return shown;
	}

	private static List<Map.Entry<ResourceKey<Item>, Long>> sorted(Map<ResourceKey<Item>, Long> map) {
		return map.entrySet().stream()
				.sorted(Comparator.<Map.Entry<ResourceKey<Item>, Long>>comparingLong(Map.Entry::getValue).reversed())
				.toList();
	}

	private static Component itemName(ResourceKey<Item> key) {
		Item item = BuiltInRegistries.ITEM.getValue(key);
		return item == null ? Component.literal(key.identifier().toString()) : new ItemStack(item).getHoverName();
	}
}
