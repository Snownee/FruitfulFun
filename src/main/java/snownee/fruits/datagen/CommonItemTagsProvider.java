package snownee.fruits.datagen;

import static net.minecraft.world.item.Items.*;
import static snownee.fruits.CoreModule.*;
import static snownee.fruits.cherry.CherryModule.*;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitsMod;
import snownee.kiwi.datagen.provider.TagsProviderHelper;

public class CommonItemTagsProvider extends ItemTagsProvider {

	private final TagsProviderHelper<Item> helper;

	public CommonItemTagsProvider(PackOutput packOutput, CompletableFuture<Provider> provider, BlockTagsProvider pBlockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(packOutput, provider, pBlockTagsProvider, FruitsMod.ID, existingFileHelper);
		helper = new TagsProviderHelper<>(this);
	}

	static final TagKey<Item> CITRUS_LOGS = ItemTags.create(new ResourceLocation(FruitsMod.ID, "citrus_logs"));
	static final TagKey<Item> CHERRY_LOGS = ItemTags.create(new ResourceLocation(FruitsMod.ID, "cherry_logs"));
	static final TagKey<Item> FRUITS = ItemTags.create(new ResourceLocation("forge:fruits"));

	@Override
	protected void addTags(Provider provider) {
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
		copy(CoreModule.ALL_LEAVES, ItemTags.LEAVES);
		copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);

		copy(CommonBlockTagsProvider.CITRUS_LOGS, CITRUS_LOGS);
		copy(CommonBlockTagsProvider.CHERRY_LOGS, CHERRY_LOGS);

		tag(FRUITS).add(APPLE, MELON_SLICE, SWEET_BERRIES, CHORUS_FRUIT, GLOW_BERRIES);
		helper.add(FRUITS, CITRON, GRAPEFRUIT, LEMON, LIME, MANDARIN, ORANGE, POMELO);
		helper.optional(FRUITS, CHERRY, REDLOVE);
		tag(ItemTags.FOX_FOOD).addTag(FRUITS);
	}

}
