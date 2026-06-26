package snownee.fruits.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import snownee.fruits.CoreFruitType;
import snownee.fruits.FFRegistries;
import snownee.fruits.FFTreeGrowers;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;

public class FFDynamicRegistryProvider extends FabricDynamicRegistryProvider {
	public FFDynamicRegistryProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, FabricDynamicRegistryProvider.Entries entries) {
		entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
		entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
		addBannerPatterns(entries);
	}

	public static void addBannerPatterns(FabricDynamicRegistryProvider.Entries entries) {
		for (String path : List.of("heart", "snowflake")) {
			Identifier id = FruitfulFun.id(path);
			entries.add(
					ResourceKey.create(Registries.BANNER_PATTERN, id),
					new BannerPattern(id, "block.minecraft.banner." + id.toShortLanguageKey()));
		}
	}

	public static void configureConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
		for (Holder.Reference<FruitType> holder : FFRegistries.FRUIT_TYPE.listElements().toList()) {
			FruitType type = holder.value();
			Identifier id = holder.key().identifier();
			type.makeFeatures(
					id,
					false,
					(location, config) -> context.register(FFTreeGrowers.createKey(location), cf(Feature.TREE, config)));
			List<WeightedPlacedFeature> features = Lists.newArrayList();
			type.makeFeatures(
					id, true, (location, config) -> {
						ResourceKey<ConfiguredFeature<?, ?>> key = FFTreeGrowers.createKey(location.withSuffix("_wg"));
						ConfiguredFeature<TreeConfiguration, ?> cf = cf(Feature.TREE, config);
						Holder<ConfiguredFeature<?, ?>> featureHolder = context.register(key, cf);
						features.add(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(featureHolder), 0.333f));
					});
			if (type.tier == 0) {
				ResourceKey<ConfiguredFeature<?, ?>> key = FFTreeGrowers.createKey(id.withSuffix("_random"));
				ConfiguredFeature<?, ?> cf = cf(
						Feature.RANDOM_SELECTOR,
						new RandomFeatureConfiguration(features, features.removeFirst().feature));
				context.register(key, cf);
			}
		}
	}

	public static void configurePlacedFeatures(BootstrapContext<PlacedFeature> context) {
		for (Holder.Reference<FruitType> holder : FFRegistries.FRUIT_TYPE.listElements().toList()) {
			if (!(holder.value() instanceof CoreFruitType type)) {
				continue;
			}
			if (type.tier != 0) {
				continue;
			}
			Identifier id = holder.key().identifier();
			PlacedFeature placedFeature = makePlacedFeature(
					context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(FFTreeGrowers.createKey(id.withSuffix("_random"))),
					type.sapling.get());
			context.register(ResourceKey.create(Registries.PLACED_FEATURE, id), placedFeature);
		}
	}

	public static PlacedFeature makePlacedFeature(Holder<ConfiguredFeature<?, ?>> configuredFeature, Block sapling) {
		return new PlacedFeature(configuredFeature, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(64), sapling));
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>> ConfiguredFeature<FC, ?> cf(F feature, FC config) {
		return new ConfiguredFeature<>(feature, config);
	}

	@Override
	public String getName() {
		return "FFDynamicRegistryProvider";
	}
}
