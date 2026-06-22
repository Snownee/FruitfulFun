package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import snownee.fruits.FFDamageTypes;

public class FFDamageTypeTagsProvider extends FabricTagsProvider<DamageType> {
	public FFDamageTypeTagsProvider(
			FabricPackOutput output,
			ResourceKey<? extends Registry<DamageType>> registryKey,
			CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registryKey, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		builder(DamageTypeTags.IS_EXPLOSION)
				.addOptional(FFDamageTypes.EXPLOSION)
				.addOptional(FFDamageTypes.PLAYER_EXPLOSION);
	}
}
