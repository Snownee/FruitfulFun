package snownee.fruits.command;

import java.util.Objects;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.GeneticData;

public class DebugAllelesCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("debug_alleles")
				.then(Commands.argument("seed", LongArgumentType.longArg())
						.executes(context -> {
							GeneticData geneticData = new GeneticData();
							long seed = LongArgumentType.getLong(context, "seed");
							if (seed == 0) {
								seed = System.currentTimeMillis();
							}
							geneticData.initAlleles(seed);
							context.getSource().sendSystemMessage(Component.literal(String.join(
									", ",
									Allele.sortedByCode().stream().map($ -> String.valueOf($.codename)).toList())));
							ServerLevel world = Objects.requireNonNull(context.getSource().getServer().getLevel(Level.OVERWORLD));
							seed = world.getSeed();
							geneticData = world.getDataStorage().computeIfAbsent(
									GeneticData::load,
									GeneticData::new,
									"fruitfulfun_genetics");
							geneticData.initAlleles(seed);
							return 0;
						}));
	}
}
