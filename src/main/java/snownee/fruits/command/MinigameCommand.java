package snownee.fruits.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import snownee.fruits.minigame.MinigameManager;

public class MinigameCommand {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("minigame")
				.then(Commands.literal("solo")
						.executes($ -> solo($.getSource())))
				.then(Commands.literal("invite")
						.then(Commands.argument("player", EntityArgument.player())
								.executes($ -> invite($.getSource(), EntityArgument.getPlayer($, "player")))))
				.then(Commands.literal("accept")
						.executes($ -> accept($.getSource())))
				.then(Commands.literal("decline")
						.executes($ -> decline($.getSource())))
				.then(Commands.literal("spectate")
						.then(Commands.argument("player", EntityArgument.player())
								.executes($ -> spectate($.getSource(), EntityArgument.getPlayer($, "player")))))
				.then(Commands.literal("duration")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.executes($ -> showDuration($.getSource()))
						.then(Commands.argument("seconds", IntegerArgumentType.integer(10, 3600))
								.executes($ -> setDuration($.getSource(), IntegerArgumentType.getInteger($, "seconds")))));
	}

	private static int solo(CommandSourceStack source) throws CommandSyntaxException {
		MinigameManager.startSolo(source.getPlayerOrException());
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.minigame.started"), false);
		return 0;
	}

	private static int invite(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
		MinigameManager.invite(source.getPlayerOrException(), target);
		return 0;
	}

	private static int accept(CommandSourceStack source) throws CommandSyntaxException {
		MinigameManager.accept(source.getPlayerOrException());
		return 0;
	}

	private static int decline(CommandSourceStack source) throws CommandSyntaxException {
		MinigameManager.decline(source.getPlayerOrException());
		return 0;
	}

	private static int spectate(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
		MinigameManager.spectate(source.getPlayerOrException(), target);
		return 0;
	}

	private static int showDuration(CommandSourceStack source) {
		int duration = MinigameManager.duration();
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.minigame.duration.get", duration), false);
		return duration;
	}

	private static int setDuration(CommandSourceStack source, int seconds) {
		MinigameManager.setDuration(seconds);
		source.sendSuccess(() -> Component.translatable("command.fruitfulfun.minigame.duration.set", seconds), true);
		return seconds;
	}
}