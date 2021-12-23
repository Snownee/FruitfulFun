package snownee.fruits.datagen;

import static net.minecraft.tags.ItemTags.createOptional;
import static net.minecraft.world.item.Items.*;
import static snownee.fruits.CoreModule.*;
import static snownee.fruits.cherry.CherryModule.*;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import snownee.fruits.FruitsMod;
import snownee.kiwi.data.provider.TagsProviderHelper;

public class CommonItemTagsProvider extends ItemTagsProvider {

	public CommonItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(pGenerator, pBlockTagsProvider, FruitsMod.MODID, existingFileHelper);
	}

	static final Tag.Named<Item> CITRUS_LOGS = createOptional(new ResourceLocation(FruitsMod.MODID, "citrus_logs"));
	static final Tag.Named<Item> CHERRY_LOGS = createOptional(new ResourceLocation(FruitsMod.MODID, "cherry_logs"));
	static final Tag.Named<Item> FRUITS = createOptional(new ResourceLocation("forge:fruits"));

	@Override
	protected void addTags() {
		copy(BlockTags.PLANKS, ItemTags.PLANKS);
		copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
		copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		copy(BlockTags.LEAVES, ItemTags.LEAVES);
		copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);

		copy(CommonBlockTagsProvider.CITRUS_LOGS, CITRUS_LOGS);
		copy(CommonBlockTagsProvider.CHERRY_LOGS, CHERRY_LOGS);

		TagsProviderHelper.addOptional(tag(FRUITS), CHERRY, REDLOVE).add(APPLE, MELON_SLICE, SWEET_BERRIES, CHORUS_FRUIT, GLOW_BERRIES).add(CITRON, GRAPEFRUIT, LEMON, LIME, MANDARIN, ORANGE, POMELO);
		tag(ItemTags.FOX_FOOD).addTag(FRUITS);
	}

}
