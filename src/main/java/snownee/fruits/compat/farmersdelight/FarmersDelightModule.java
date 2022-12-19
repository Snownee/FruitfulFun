package snownee.fruits.compat.farmersdelight;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import vectorwing.farmersdelight.common.block.CabinetBlock;

@KiwiModule(value = "farmersdelight", dependencies = "farmersdelight")
@KiwiModule.Optional
public class FarmersDelightModule extends AbstractModule {

	@Category("decorations")
	public static final KiwiGO<Block> CITRUS_CABINET = go(() -> new CabinetBlock(blockProp(Blocks.BARREL)));
	@Category("decorations")
	public static final KiwiGO<Block> CHERRY_CABINET = go(() -> new CabinetBlock(blockProp(Blocks.BARREL)));

}
