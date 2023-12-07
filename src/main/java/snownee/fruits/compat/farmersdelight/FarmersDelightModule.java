package snownee.fruits.compat.farmersdelight;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.FruitsMod;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;

@KiwiModule(value = "farmersdelight", dependencies = "farmersdelight")
@KiwiModule.Optional
public class FarmersDelightModule extends AbstractModule {

	@Category("decorations")
	public static final KiwiGO<Block> CITRUS_CABINET = go(FarmersDelightModule::createCabinet);
	@Category("decorations")
	public static final KiwiGO<Block> CHERRY_CABINET = go(FarmersDelightModule::createCabinet);

	public static Block createCabinet() {
		try {
			return (Block) Class.forName("vectorwing.farmersdelight.common.block.CabinetBlock")
					.getConstructor(Block.Properties.class)
					.newInstance(blockProp(Blocks.BARREL));
		} catch (Exception e) {
			FruitsMod.LOGGER.error("Failed to load FarmersDelight cabinet", e);
			return null;
		}
	}

}
