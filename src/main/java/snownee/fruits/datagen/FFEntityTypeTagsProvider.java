package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.gadget.GadgetModule;

public class FFEntityTypeTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider {
	public FFEntityTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		getOrCreateRawBuilder(BeeModule.CANNOT_HAUNT)
				.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ARMOR_STAND))
				.addOptionalElement(Identifier.parse("supplementaries:hat_stand"))
				.addOptionalElement(Identifier.parse("dummmmmmy:target_dummy"));
		valueLookupBuilder(GadgetModule.VCD_MOVABLE);
		valueLookupBuilder(EntityTypeTags.DISMOUNTS_UNDERWATER).add(EntityType.BEE);
		valueLookupBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE).addOptional(GadgetModule.SUMMONED_BEE.get());
	}
}
