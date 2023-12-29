package snownee.fruits.cherry;

import static snownee.fruits.cherry.CherryModule.CHERRY_LEAVES;
import static snownee.fruits.cherry.CherryModule.CHERRY_SAPLING;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LEAVES;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LOG;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SAPLING;

import net.minecraft.world.level.block.Blocks;
import snownee.fruits.FruitType;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule(value = "cherry_fruits", dependencies = "@cherry")
public class CherryFruitTypes extends AbstractModule {

	public static final KiwiGO<FruitType> CHERRY = go(() -> new CherryFruitType(1, () -> Blocks.CHERRY_LOG, CHERRY_LEAVES, CHERRY_SAPLING, CherryModule.CHERRY));
	public static final KiwiGO<FruitType> REDLOVE = go(() -> new CherryFruitType(2, REDLOVE_LOG, REDLOVE_LEAVES, REDLOVE_SAPLING, CherryModule.REDLOVE));

}
