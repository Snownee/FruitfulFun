package snownee.fruits.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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
import snownee.fruits.FruitType;

public class FFDynamicRegistryProvider extends FabricDynamicRegistryProvider {
	public FFDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, FabricDynamicRegistryProvider.Entries entries) {
		addConfiguredFeatures(entries);
		addPlacedFeatures(entries);
	}

	public static void addConfiguredFeatures(FabricDynamicRegistryProvider.Entries entries) {
		for (Holder.Reference<FruitType> holder : FFRegistries.FRUIT_TYPE.holders().toList()) {
			FruitType type = holder.value();
			ResourceLocation id = holder.key().location();
			type.makeFeatures(
					id, false, (location, config) -> entries.add(FeatureUtils.createKey(location.toString()), cf(Feature.TREE, config)));
			List<WeightedPlacedFeature> features = Lists.newArrayList();
			type.makeFeatures(id, true, (location, config) -> {
				ResourceKey<ConfiguredFeature<?, ?>> key = FeatureUtils.createKey(location.withSuffix("_wg").toString());
				ConfiguredFeature<TreeConfiguration, ?> cf = cf(Feature.TREE, config);
				entries.add(key, cf);
				features.add(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(entries.ref(key)), 0.333f));
			});
			if (type.tier == 0) {
				ResourceKey<ConfiguredFeature<?, ?>> key = FeatureUtils.createKey(id.withSuffix("_random").toString());
				ConfiguredFeature<?, ?> cf = cf(
						Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(features, features.remove(0).feature));
				entries.add(key, cf);
			}
		}
	}

	public static void addPlacedFeatures(FabricDynamicRegistryProvider.Entries entries) {
		for (Holder.Reference<FruitType> holder : FFRegistries.FRUIT_TYPE.holders().toList()) {
			if (!(holder.value() instanceof CoreFruitType type)) {
				continue;
			}
			if (type.tier != 0) {
				continue;
			}
			ResourceLocation id = holder.key().location();
			PlacedFeature placedFeature = makePlacedFeature(
					entries.ref(FeatureUtils.createKey(id.withSuffix("_random").toString())), type.sapling.get());
			entries.add(PlacementUtils.createKey(id.toString()), placedFeature);
		}
	}

	public static PlacedFeature makePlacedFeature(Holder<ConfiguredFeature<?, ?>> configuredFeature, Block sapling) {
		return new PlacedFeature(configuredFeature, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(64), sapling));
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>> ConfiguredFeature<FC, ?> cf(F feature, FC config) {
		return new ConfiguredFeature<>(feature, config);
	}

	@Override
	public @NotNull String getName() {
		return "FFDynamicRegistryProvider";
	}
}
