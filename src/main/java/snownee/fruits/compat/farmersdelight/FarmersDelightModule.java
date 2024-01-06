package snownee.fruits.compat.farmersdelight;

import net.minecraft.world.level.block.Block;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;

@KiwiModule(value = "farmersdelight", dependencies = "farmersdelight")
@KiwiModule.Optional
public class FarmersDelightModule extends AbstractModule {

	@Category(value = {Categories.FUNCTIONAL_BLOCKS, "farmersdelight:main"}, after = "farmersdelight:cherry_cabinet")
	public static final KiwiGO<Block> CITRUS_CABINET = go(FarmersDelightModule::createCabinet);
	public static final KiwiGO<Block> REDLOVE_CABINET = go(FarmersDelightModule::createCabinet);

	public FarmersDelightModule() {
		Hooks.farmersdelight = true;
	}

	public static Block createCabinet() {
		try {
			return (Block) Class.forName("com.nhoryzon.mc.farmersdelight.block.CabinetBlock")
					.getConstructor()
					.newInstance();
		} catch (Exception e) {
			FruitfulFun.LOGGER.error("Failed to load FarmersDelight cabinet", e);
			return null;
		}
	}

}
