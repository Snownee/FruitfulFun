package snownee.fruits.cherry;

import static snownee.fruits.cherry.CherryModule.*;

import snownee.fruits.FruitType;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule(value = "cherry_fruits", dependencies = "@cherry")
public class CherryFruitTypes extends AbstractModule {

	public static final KiwiGO<FruitType> CHERRY = go(() -> new FruitType(0, CHERRY_LOG, CHERRY_LEAVES, CHERRY_SAPLING, CherryModule.CHERRY, CHERRY_CARPET));

	public static final KiwiGO<FruitType> REDLOVE = go(() -> new FruitType(2, CHERRY_LOG, REDLOVE_LEAVES, REDLOVE_SAPLING, CherryModule.REDLOVE, REDLOVE_CARPET));

}
