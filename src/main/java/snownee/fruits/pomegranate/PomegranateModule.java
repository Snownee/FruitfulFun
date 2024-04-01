package snownee.fruits.pomegranate;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import snownee.fruits.CoreModule;
import snownee.fruits.block.grower.FruitTreeGrower;
import snownee.fruits.pomegranate.block.HangingFruitBlock;
import snownee.fruits.pomegranate.block.HangingFruitLeavesBlock;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.lychee.LycheeRegistries;

@KiwiModule("pomegranate")
public class PomegranateModule extends AbstractModule {
	public static final KiwiGO<FFExplodeAction.Type> EXPLODE = go(FFExplodeAction.Type::new, () -> LycheeRegistries.POST_ACTION.registry());
	@KiwiModule.Category(value = Categories.INGREDIENTS, after = "wheat")
	@KiwiModule.RenderLayer(KiwiModule.RenderLayer.Layer.CUTOUT)
	public static final KiwiGO<HangingFruitBlock> POMEGRANATE = go(() -> new HangingFruitBlock(blockProp()
			.instabreak()
			.sound(SoundType.CROP)
			.dynamicShape()
			.offsetType(BlockBehaviour.OffsetType.XZ)
			.pushReaction(PushReaction.DESTROY)));
	@KiwiModule.Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final KiwiGO<HangingFruitLeavesBlock> POMEGRANATE_LEAVES = go(() -> new HangingFruitLeavesBlock(
			PomegranateFruitTypes.POMEGRANATE,
			blockProp(Blocks.JUNGLE_LEAVES)));
	@KiwiModule.Category(value = Categories.NATURAL_BLOCKS, after = "cherry_sapling")
	@KiwiModule.RenderLayer(KiwiModule.RenderLayer.Layer.CUTOUT)
	public static final KiwiGO<SaplingBlock> POMEGRANATE_SAPLING = go(() -> new SaplingBlock(
			new FruitTreeGrower(PomegranateFruitTypes.POMEGRANATE.getOrCreate()),
			blockProp(Blocks.JUNGLE_SAPLING)));
	@KiwiModule.RenderLayer(KiwiModule.RenderLayer.Layer.CUTOUT)
	@KiwiModule.NoItem
	public static final KiwiGO<Block> POTTED_POMEGRANATE = go(() -> new FlowerPotBlock(
			POMEGRANATE_SAPLING.getOrCreate(),
			blockProp(Blocks.POTTED_JUNGLE_SAPLING)));

	@Override
	protected void preInit() {
		CoreModule.createPoiTypes(this);
	}
}
