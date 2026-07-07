package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import snownee.fruits.FFDamageTypes;

public class FFDamageTypeTags extends FabricTagsProvider<DamageType> {
	public FFDamageTypeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.DAMAGE_TYPE, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		builder(DamageTypeTags.IS_EXPLOSION).addOptional(FFDamageTypes.EXPLOSION);
	}
}
