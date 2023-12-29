package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitType;

public class FFDynamicRegistryProvider extends FabricDynamicRegistryProvider {
	public FFDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, Entries entries) {
		addConfiguredFeatures(entries);
	}

	public static void addConfiguredFeatures(FabricDynamicRegistryProvider.Entries entries) {
		for (Holder.Reference<FruitType> holder : FFRegistries.FRUIT_TYPE.holders().toList()) {
			FruitType type = holder.value();
			ResourceLocation id = holder.key().location();
			type.makeFeatures(id, false, (location, config) -> entries.add(FeatureUtils.createKey(location.toString()), cf(Feature.TREE, config)));
			type.makeFeatures(id, true, (location, config) -> entries.add(FeatureUtils.createKey(location.withSuffix("_wg").toString()), cf(Feature.TREE, config)));
		}
	}

	@Override
	public @NotNull String getName() {
		return "FFDynamicRegistryProvider";
	}

	private static <FC extends FeatureConfiguration, F extends Feature<FC>> ConfiguredFeature<FC, ?> cf(F feature, FC config) {
		return new ConfiguredFeature<>(feature, config);
	}
}
