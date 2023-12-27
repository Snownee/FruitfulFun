package snownee.fruits.compat.farmersdelight;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.FruitfulFun;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;

@KiwiModule(value = "farmersdelight", dependencies = "farmersdelight")
@KiwiModule.Optional
public class FarmersDelightModule extends AbstractModule {

	@Category(Categories.FUNCTIONAL_BLOCKS)
	public static final KiwiGO<Block> CITRUS_CABINET = go(FarmersDelightModule::createCabinet);
	@Category(Categories.FUNCTIONAL_BLOCKS)
	public static final KiwiGO<Block> REDLOVE_CABINET = go(FarmersDelightModule::createCabinet);

	public static Block createCabinet() {
		try {
			return (Block) Class.forName("vectorwing.farmersdelight.common.block.CabinetBlock")
					.getConstructor(Block.Properties.class)
					.newInstance(blockProp(Blocks.BARREL));
		} catch (Exception e) {
			FruitfulFun.LOGGER.error("Failed to load FarmersDelight cabinet", e);
			return null;
		}
	}

}
