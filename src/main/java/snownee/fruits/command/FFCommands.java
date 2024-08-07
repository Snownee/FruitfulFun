package snownee.fruits.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import snownee.fruits.FruitfulFun;

public class FFCommands {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal(FruitfulFun.ID)
				.then(DebugAllelesCommand.register());
	}
}
