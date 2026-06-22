package snownee.fruits;

import java.util.Optional;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class FFTreeGrowers {
	public static final TreeGrower TANGERINE = core("tangerine");
	public static final TreeGrower LIME = core("lime");
	public static final TreeGrower CITRON = core("citron");
	public static final TreeGrower POMELO = core("pomelo");
	public static final TreeGrower ORANGE = core("orange");
	public static final TreeGrower LEMON = core("lemon");
	public static final TreeGrower GRAPEFRUIT = core("grapefruit");
	public static final TreeGrower APPLE = core("apple");
	public static final TreeGrower CHERRY = cherry("cherry");
	public static final TreeGrower REDLOVE = cherry("redlove");
	public static final TreeGrower POMEGRANATE = core("pomegranate");

	public static TreeGrower core(String id) {
		var feature = createKey(id);
		var fancyFeature = createKey(id + "_fancy");
		var beesFeature = createKey(id + "_bees");
		return new TreeGrower(
				FruitfulFun.id(id).toString(),
				0.2F,
				Optional.empty(),
				Optional.empty(),
				Optional.of(feature),
				Optional.of(fancyFeature),
				Optional.of(beesFeature),
				Optional.empty());
	}

	public static TreeGrower cherry(String id) {
		var feature = createKey(id);
		var beesFeature = createKey(id + "_bees");
		return new TreeGrower(FruitfulFun.id(id).toString(), Optional.empty(), Optional.of(feature), Optional.of(beesFeature));
	}

	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, FruitfulFun.id(name));
	}
}
