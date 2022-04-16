package snownee.fruits;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("core_fruits")
public class CoreFruitTypes extends AbstractModule {

	public static final KiwiGO<FruitType> MANDARIN = go(() -> new FruitType(0, CoreModule.CITRUS_LOG, CoreModule.MANDARIN_LEAVES, CoreModule.MANDARIN_SAPLING, CoreModule.MANDARIN));
	public static final KiwiGO<FruitType> LIME = go(() -> new FruitType(0, CoreModule.CITRUS_LOG, CoreModule.LIME_LEAVES, CoreModule.LIME_SAPLING, CoreModule.LIME));
	public static final KiwiGO<FruitType> CITRON = go(() -> new FruitType(0, CoreModule.CITRUS_LOG, CoreModule.CITRON_LEAVES, CoreModule.CITRON_SAPLING, CoreModule.CITRON));
	public static final KiwiGO<FruitType> POMELO = go(() -> new FruitType(1, CoreModule.CITRUS_LOG, CoreModule.POMELO_LEAVES, CoreModule.POMELO_SAPLING, CoreModule.POMELO));
	public static final KiwiGO<FruitType> ORANGE = go(() -> new FruitType(1, CoreModule.CITRUS_LOG, CoreModule.ORANGE_LEAVES, CoreModule.ORANGE_SAPLING, CoreModule.ORANGE));
	public static final KiwiGO<FruitType> LEMON = go(() -> new FruitType(1, CoreModule.CITRUS_LOG, CoreModule.LEMON_LEAVES, CoreModule.LEMON_SAPLING, CoreModule.LEMON));
	public static final KiwiGO<FruitType> GRAPEFRUIT = go(() -> new FruitType(2, CoreModule.CITRUS_LOG, CoreModule.GRAPEFRUIT_LEAVES, CoreModule.GRAPEFRUIT_SAPLING, CoreModule.GRAPEFRUIT));
	public static final KiwiGO<FruitType> APPLE = go(() -> new FruitType(1, () -> Blocks.OAK_LOG, CoreModule.APPLE_LEAVES, CoreModule.APPLE_SAPLING, () -> Items.APPLE));

}
