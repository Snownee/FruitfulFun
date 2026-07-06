package snownee.fruits.pomegranate;

import java.util.Optional;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import snownee.fruits.CoreModule;
import snownee.fruits.FFFruitTypes;
import snownee.fruits.FFTreeGrowers;
import snownee.fruits.FruitfulFun;
import snownee.fruits.pomegranate.block.HangingFruitBlock;
import snownee.fruits.pomegranate.block.HangingFruitLeavesBlock;
import snownee.fruits.pomegranate.item.EnchantedPomegranateItem;
import snownee.fruits.pomegranate.item.PomegranateItem;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule(value = "pomegranate", dependencies = "@fruit_types")
public class PomegranateModule extends AbstractModule {
	@KiwiModule.Category(value = Categories.NATURAL_BLOCKS, after = "cherry_leaves")
	public static final BlockObject<HangingFruitLeavesBlock> POMEGRANATE_LEAVES = block(
			$ -> new HangingFruitLeavesBlock(
					FFFruitTypes.POMEGRANATE.holderOrThrow(),
					0.01F,
					Optional.empty(),
					0xFF6b852c,
					$), () -> Blocks.JUNGLE_LEAVES);
	@KiwiModule.NoItem
	public static final BlockObject<HangingFruitBlock> POMEGRANATE = block($ -> new HangingFruitBlock($.instabreak()
			.sound(SoundType.CROP)
			.dynamicShape()
			.offsetType(BlockBehaviour.OffsetType.XZ)
			.pushReaction(PushReaction.DESTROY)));
	@KiwiModule.Category(value = {Categories.NATURAL_BLOCKS, Categories.COMBAT}, after = "cherry_leaves")
	@KiwiModule.Name("pomegranate")
	public static final ItemObject<PomegranateItem> POMEGRANATE_ITEM = item($ -> new PomegranateItem(POMEGRANATE.getOrCreate(), $));
	@KiwiModule.Category(value = Categories.COMBAT)
	public static final ItemObject<EnchantedPomegranateItem> ENCHANTED_POMEGRANATE = item($ -> new EnchantedPomegranateItem($.rarity(Rarity.UNCOMMON)));
	@KiwiModule.Category(value = Categories.NATURAL_BLOCKS, after = "cherry_sapling")
	public static final BlockObject<SaplingBlock> POMEGRANATE_SAPLING = block(
			$ -> new SaplingBlock(FFTreeGrowers.POMEGRANATE, $),
			() -> Blocks.JUNGLE_SAPLING);
	@KiwiModule.NoItem
	public static final BlockObject<Block> POTTED_POMEGRANATE = block(
			$ -> new FlowerPotBlock(POMEGRANATE_SAPLING.getOrCreate(), $),
			() -> Blocks.POTTED_JUNGLE_SAPLING);
	public static final KiwiGO<SoundEvent> POMEGRANATE_EXPLODE = go(() -> SoundEvent.createVariableRangeEvent(FruitfulFun.id(
			"entity.pomegranate.explode")));

	@Override
	protected void addEntries() {
		CoreModule.createPoiTypes(this);
	}
}
