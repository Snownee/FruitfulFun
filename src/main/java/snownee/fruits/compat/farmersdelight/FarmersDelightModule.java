package snownee.fruits.compat.farmersdelight;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;

@KiwiModule(value = "farmersdelight", dependencies = "farmersdelight")
@KiwiModule.Optional
public class FarmersDelightModule extends AbstractModule {

	@Nullable
	private static final String MODE;

	static {
		if (classExists("vectorwing.farmersdelight.common.block.CabinetBlock")) {
			MODE = "vectorwing";
		} else if (classExists("com.nhoryzon.mc.farmersdelight.block.CabinetBlock")) {
			MODE = "nhoryzon";
		} else {
			MODE = null;
		}
	}

	private static boolean classExists(String name) {
		try {
			Class.forName(name);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	@Category(
			value = {Categories.FUNCTIONAL_BLOCKS, "farmersdelight:main", "farmersdelight:farmersdelight"},
			after = "farmersdelight:cherry_cabinet")
	public static final KiwiGO<Block> CITRUS_CABINET = go(FarmersDelightModule::createCabinet);
	public static final KiwiGO<Block> REDLOVE_CABINET = go(FarmersDelightModule::createCabinet);

	public FarmersDelightModule() {
		Hooks.farmersdelight = true;
	}

	public static Block createCabinet() {
		try {
			if ("vectorwing".equals(MODE)) {
				return (Block) Class.forName("vectorwing.farmersdelight.common.block.CabinetBlock")
						.getConstructor(BlockBehaviour.Properties.class)
						.newInstance(BlockBehaviour.Properties.copy(Blocks.BARREL));
			} else if ("nhoryzon".equals(MODE)) {
				return (Block) Class.forName("com.nhoryzon.mc.farmersdelight.block.CabinetBlock")
						.getConstructor()
						.newInstance();
			} else {
				throw new IllegalStateException("Cabinets are registered unexpectedly");
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load FarmersDelight cabinets", e);
		}
	}

	public static String getMode() {
		return MODE;
	}
}
