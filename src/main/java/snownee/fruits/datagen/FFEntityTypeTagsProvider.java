package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import snownee.fruits.bee.BeeModule;

public class FFEntityTypeTagsProvider extends FabricTagProvider.EntityTypeTagProvider {
	public FFEntityTypeTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		getOrCreateTagBuilder(BeeModule.CANNOT_HAUNT)
				.add(EntityType.ARMOR_STAND)
				.addOptional(new ResourceLocation("supplementaries:hat_stand"))
				.addOptional(new ResourceLocation("dummmmmmy:target_dummy"));
	}
}
