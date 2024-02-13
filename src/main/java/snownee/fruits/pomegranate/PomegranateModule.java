package snownee.fruits.pomegranate;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SaplingBlock;
import snownee.fruits.CoreModule;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.item.ModItem;
import snownee.lychee.LycheeRegistries;

@KiwiModule("pomegranate")
public class PomegranateModule extends AbstractModule {
	@KiwiModule.Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final KiwiGO<FFExplodeAction.Type> EXPLODE = go(FFExplodeAction.Type::new, () -> LycheeRegistries.POST_ACTION.registry());
	public static final KiwiGO<Item> POMEGRANATE = go(() -> new ModItem(itemProp()));
	@KiwiModule.Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final KiwiGO<FruitLeavesBlock> POMEGRANATE_LEAVES = go(() -> new FruitLeavesBlock(PomegranateFruitTypes.POMEGRANATE, blockProp(Blocks.BIRCH_LEAVES)));
	@KiwiModule.Category(value = Categories.NATURAL_BLOCKS, after = "cherry_sapling")
	@KiwiModule.RenderLayer(KiwiModule.RenderLayer.Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> POMEGRANATE_SAPLING = go(() -> new SaplingBlock(new FruitTreeGrower(PomegranateFruitTypes.POMEGRANATE.getOrCreate()), blockProp(Blocks.BIRCH_SAPLING)));
	@KiwiModule.RenderLayer(KiwiModule.RenderLayer.Layer.CUTOUT)
	@KiwiModule.NoItem
	public static final KiwiGO<Block> POTTED_POMEGRANATE = go(() -> new FlowerPotBlock(POMEGRANATE_SAPLING.getOrCreate(), blockProp(Blocks.POTTED_BIRCH_SAPLING)));

	@Override
	protected void preInit() {
		CoreModule.createPoiTypes(this);
	}
}
