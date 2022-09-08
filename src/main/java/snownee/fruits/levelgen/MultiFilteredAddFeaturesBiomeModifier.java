package snownee.fruits.levelgen;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import snownee.fruits.CoreModule;

public record MultiFilteredAddFeaturesBiomeModifier(List<HolderSet<Biome>> requires, List<HolderSet<Biome>> excludes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements BiomeModifier {
	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
		if (phase == Phase.ADD && requires.stream().allMatch($ -> $.contains(biome)) && !excludes.stream().anyMatch($ -> $.contains(biome))) {
			BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
			features.forEach(holder -> generationSettings.addFeature(step, holder));
		}
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return CoreModule.ADD_FEATURES.get();
	}
}
