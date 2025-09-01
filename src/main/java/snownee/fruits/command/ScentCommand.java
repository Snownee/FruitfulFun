package snownee.fruits.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.FFRegistries;
import snownee.fruits.gadget.ScentType;

public class ScentCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("scent")
				.requires($ -> $.hasPermission(2))
				.executes($ -> show($.getSource()))
				.then(Commands.literal("clear")
						.executes($ -> clear(1, $.getSource()))
						.then(Commands.argument("range", IntegerArgumentType.integer(1))
								.executes($ -> clear(IntegerArgumentType.getInteger($, "range"), $.getSource()))));
	}

	private static int clear(int range, CommandSourceStack source) {
		return 0;
	}

	private static int show(CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		LevelChunk chunk = level.getChunkAt(BlockPos.containing(source.getPosition()));
		long gameTime = level.getGameTime();
		int count = 0;
		for (ScentType type : FFRegistries.SCENT_TYPE) {
			long time = type.getTime(chunk);
			if (time <= 0) {
				continue;
			}
			count++;
			source.sendSystemMessage(Component.literal("%s - %st".formatted(
					FFRegistries.SCENT_TYPE.getKey(type),
					Math.max(0, time - gameTime))));
		}
		if (count == 0) {
			source.sendSystemMessage(Component.literal("No scent"));
		}
		return count;
	}
}
