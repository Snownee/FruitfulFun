package snownee.fruits.command;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.fruits.FFRegistries;
import snownee.fruits.gadget.scent.ScentType;

public class ScentCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("scent")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.executes($ -> show($.getSource()))
				.then(Commands.literal("clear")
						.executes($ -> clear(0, $.getSource()))
						.then(Commands.argument("range", IntegerArgumentType.integer(0, 99))
								.executes($ -> clear(IntegerArgumentType.getInteger($, "range"), $.getSource()))));
	}

	private static int clear(int range, CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		long lastGameTime = level.getGameTime() - 1;
		BlockPos pos = BlockPos.containing(source.getPosition());
		MutableInt count = new MutableInt();
		ChunkPos.rangeClosed(ChunkPos.containing(pos), range).forEach(chunkPos -> {
			LevelChunk chunk = level.getChunk(chunkPos.x(), chunkPos.z());
			for (ScentType scentType : FFRegistries.SCENT_TYPE) {
				if (scentType.isActiveAt(chunk)) {
					scentType.setTime(chunk, lastGameTime);
					count.increment();
				}
			}
		});
		source.sendSystemMessage(Component.literal("Affected %d %s".formatted(
				count.intValue(),
				count.intValue() == 1 ? "chunk" : "chunks")));
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
