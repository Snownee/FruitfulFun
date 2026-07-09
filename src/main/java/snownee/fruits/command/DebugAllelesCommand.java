package snownee.fruits.command;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.Allele;
import snownee.fruits.bee.genetics.GeneData;
import snownee.fruits.bee.genetics.GeneticSavedData;
import snownee.fruits.bee.genetics.MutationRate;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.compat.jade.InspectorProvider;

public class DebugAllelesCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("debug_alleles")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.argument("seed", LongArgumentType.longArg())
						.executes(context -> {
							GeneticSavedData data = new GeneticSavedData();
							long seed = LongArgumentType.getLong(context, "seed");
							if (seed == 0) {
								seed = System.currentTimeMillis();
							}
							data.initAlleles(seed);
							context.getSource().sendSystemMessage(Component.literal(String.join(
									", ",
									Allele.sortedByCode().stream().map($ -> $.codename).toList())));
							ServerLevel level = Objects.requireNonNull(context.getSource().getServer().getLevel(Level.OVERWORLD));
							seed = level.getSeed();
							data = level.getDataStorage().computeIfAbsent(GeneticSavedData.TYPE);
							data.initAlleles(seed);
							return 0;
						}))
				.then(Commands.literal("breed")
						.executes(context -> {
							breed(context);
							return 0;
						}))
				.then(Commands.literal("randomize")
						.executes(context -> {
							randomize(context);
							return 0;
						}));
	}

	private static void randomize(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		ServerLevel level = source.getLevel();
		Vec3 pos = source.getPosition();
		List<Bee> bees = level.getEntities(EntityType.BEE, AABB.ofSize(pos, 10, 10, 10), BeeModule::canBreed);
		if (bees.isEmpty()) {
			source.sendSystemMessage(Component.literal("No bees nearby"));
			return;
		}
		for (Bee bee : bees) {
			BeeAttributes.of(bee).randomize(bee);
		}
		if (Hooks.jade) {
			InspectorProvider.POTENTIAL_CACHE.invalidateAll();
		}
		source.sendSystemMessage(Component.literal("Randomized " + bees.size() + " bees"));
	}

	private static void breed(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		ServerLevel level = source.getLevel();
		Vec3 pos = source.getPosition();
		List<Bee> bees = level.getEntities(EntityType.BEE, AABB.ofSize(pos, 10, 10, 10), BeeModule::canBreed);
		if (bees.size() < 2) {
			source.sendSystemMessage(Component.literal("Not enough bees nearby"));
			return;
		}
		GeneData bee1 = BeeAttributes.of(bees.get(0)).genes();
		Set<Trait> traits1 = bee1.traits();
		GeneData bee2 = BeeAttributes.of(bees.get(1)).genes();
		Set<Trait> traits2 = bee2.traits();
		Object2IntOpenHashMap<String> traitCounts = new Object2IntOpenHashMap<>();
		for (int i = 0; i < 300; i++) {
			GeneData offspring = new GeneData();
			offspring.breedFrom(
					bee1,
					MutationRate.neverMutate(),
					bee2,
					MutationRate.neverMutate(),
					level.getRandom());
			offspring.updateTraits();
			for (Trait trait : offspring.traits()) {
				traitCounts.addTo(trait.name(), 1);
			}
		}
		source.sendSystemMessage(Component.literal("Traits from bee 1: " + traits1.stream().map(Trait::name).toList()));
		source.sendSystemMessage(Component.literal("Traits from bee 2: " + traits2.stream().map(Trait::name).toList()));
		source.sendSystemMessage(Component.literal("Trait counts in 100 offspring: " + traitCounts));
	}
}
