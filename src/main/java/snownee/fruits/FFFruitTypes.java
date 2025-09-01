package snownee.fruits;

import static snownee.fruits.cherry.CherryModule.CHERRY_LEAVES;
import static snownee.fruits.cherry.CherryModule.CHERRY_SAPLING;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LEAVES;
import static snownee.fruits.cherry.CherryModule.REDLOVE_LOG;
import static snownee.fruits.cherry.CherryModule.REDLOVE_SAPLING;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.cherry.CherryFruitType;
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.pomegranate.PomegranateModule;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("fruit_types")
public class FFFruitTypes extends AbstractModule {
	public static final KiwiGO<FruitType> TANGERINE = go(() -> new CoreFruitType(
			0,
			CoreModule.CITRUS_LOG,
			CoreModule.TANGERINE_LEAVES,
			CoreModule.TANGERINE_SAPLING,
			CoreModule.TANGERINE));
	public static final KiwiGO<FruitType> LIME = go(() -> new CoreFruitType(
			0,
			CoreModule.CITRUS_LOG,
			CoreModule.LIME_LEAVES,
			CoreModule.LIME_SAPLING,
			CoreModule.LIME));
	public static final KiwiGO<FruitType> CITRON = go(() -> new CoreFruitType(
			0,
			CoreModule.CITRUS_LOG,
			CoreModule.CITRON_LEAVES,
			CoreModule.CITRON_SAPLING,
			CoreModule.CITRON));
	public static final KiwiGO<FruitType> POMELO = go(() -> new CoreFruitType(
			1,
			CoreModule.CITRUS_LOG,
			CoreModule.POMELO_LEAVES,
			CoreModule.POMELO_SAPLING,
			CoreModule.POMELO));
	public static final KiwiGO<FruitType> ORANGE = go(() -> new CoreFruitType(
			1,
			CoreModule.CITRUS_LOG,
			CoreModule.ORANGE_LEAVES,
			CoreModule.ORANGE_SAPLING,
			CoreModule.ORANGE));
	public static final KiwiGO<FruitType> LEMON = go(() -> new CoreFruitType(
			1,
			CoreModule.CITRUS_LOG,
			CoreModule.LEMON_LEAVES,
			CoreModule.LEMON_SAPLING,
			CoreModule.LEMON));
	public static final KiwiGO<FruitType> GRAPEFRUIT = go(() -> new CoreFruitType(
			2,
			CoreModule.CITRUS_LOG,
			CoreModule.GRAPEFRUIT_LEAVES,
			CoreModule.GRAPEFRUIT_SAPLING,
			CoreModule.GRAPEFRUIT));
	public static final KiwiGO<FruitType> APPLE = go(() -> new CoreFruitType(
			1,
			() -> Blocks.OAK_LOG,
			CoreModule.APPLE_LEAVES,
			CoreModule.APPLE_SAPLING,
			() -> Items.APPLE).allogamous());

	public static final KiwiGO<FruitType> CHERRY = go(() -> new CherryFruitType(
			1,
			() -> Blocks.CHERRY_LOG,
			CHERRY_LEAVES,
			CHERRY_SAPLING,
			CherryModule.CHERRY).allogamous());
	public static final KiwiGO<FruitType> REDLOVE = go(() -> new CherryFruitType(
			2,
			REDLOVE_LOG,
			REDLOVE_LEAVES,
			REDLOVE_SAPLING,
			CherryModule.REDLOVE).allogamous());

	public static final KiwiGO<FruitType> POMEGRANATE = go(() -> new CoreFruitType(
			2,
			() -> Blocks.JUNGLE_LOG,
			PomegranateModule.POMEGRANATE_LEAVES,
			PomegranateModule.POMEGRANATE_SAPLING,
			PomegranateModule.POMEGRANATE_ITEM::getOrCreate));

	static {
		FFRegistries.init();
	}
}
