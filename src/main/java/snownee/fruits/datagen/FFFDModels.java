package snownee.fruits.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import snownee.fruits.compat.farmersdelight.FarmersDelightModule;

public class FFFDModels extends FabricModelProvider {
	public FFFDModels(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		generators.registerSimpleItemModel(
				FarmersDelightModule.CITRUS_CABINET.get(),
				ModelLocationUtils.getModelLocation(FarmersDelightModule.CITRUS_CABINET.get()));
		generators.registerSimpleItemModel(
				FarmersDelightModule.REDLOVE_CABINET.get(),
				ModelLocationUtils.getModelLocation(FarmersDelightModule.REDLOVE_CABINET.get()));
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {}
}
