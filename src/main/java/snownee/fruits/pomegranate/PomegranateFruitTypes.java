package snownee.fruits.pomegranate;

import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreFruitType;
import snownee.fruits.FruitType;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("pomegranate_fruits")
public class PomegranateFruitTypes extends AbstractModule {
	public static final KiwiGO<FruitType> POMEGRANATE = go(
			() -> new CoreFruitType(2, () -> Blocks.JUNGLE_LOG, PomegranateModule.POMEGRANATE_LEAVES, PomegranateModule.POMEGRANATE_SAPLING,
					PomegranateModule.POMEGRANATE.getOrCreate()::asItem));
}
