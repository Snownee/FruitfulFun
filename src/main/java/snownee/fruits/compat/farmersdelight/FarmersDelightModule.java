package snownee.fruits.compat.farmersdelight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.loader.event.InitEvent;

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

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Identifier.parse("farmersdelight:cabinet"));
			if (type != null) {
				type.addValidBlock(CITRUS_CABINET.get());
				type.addValidBlock(REDLOVE_CABINET.get());
			}
		});
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
