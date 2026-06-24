package snownee.fruits.compat.farmersdelight;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;

@KiwiModule(value = "farmersdelight", dependencies = "farmersdelight")
@KiwiModule.Optional
public class FarmersDelightModule extends AbstractModule {
	@Category(
			value = {Categories.FUNCTIONAL_BLOCKS, "farmersdelight:main", "farmersdelight:farmersdelight"},
			after = "farmersdelight:cherry_cabinet")
	public static final BlockObject<Block> CITRUS_CABINET = cabinet();
	public static final BlockObject<Block> REDLOVE_CABINET = cabinet();

	public FarmersDelightModule() {
		Hooks.farmersdelight = true;
	}

	public static BlockObject<Block> cabinet() {
		return block(
				$ -> {
					try {
						return (Block) Class.forName("vectorwing.farmersdelight.common.block.CabinetBlock")
								.getConstructor(BlockBehaviour.Properties.class)
								.newInstance($);
					} catch (Exception e) {
						throw new IllegalStateException("Failed to load FarmersDelight cabinets", e);
					}
				},
				() -> Blocks.BARREL);
	}
}
