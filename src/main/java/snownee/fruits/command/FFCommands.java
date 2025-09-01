package snownee.fruits.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.loader.Platform;

public class FFCommands {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(FruitfulFun.ID);
		if (!Platform.isProduction()) {
			root.then(DebugAllelesCommand.register());
		}
		return root.then(ScentCommand.register());
	}
}
