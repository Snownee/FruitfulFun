package snownee.fruits.datagen;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.cherry.CherryModule;
import snownee.kiwi.recipe.ModuleLoadedCondition;

public class FFRecipeProvider extends FabricRecipeProvider {
	public FFRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> exporter) {
		Consumer<FinishedRecipe> cherryExporter = withConditions(exporter, ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "cherry")));
		oneToOneConversionRecipe(cherryExporter, Items.PINK_DYE, CherryModule.PEACH_PINK_PETALS.get().asItem(), "pink_dye");
		woodenBoat(exporter, Items.OAK_BOAT, CoreModule.CITRUS_PLANKS.get());
		woodenBoat(cherryExporter, Items.CHERRY_BOAT, CherryModule.REDLOVE_PLANKS.get());
		wreath(cherryExporter, CherryModule.CHERRY_WREATH.get(), CherryModule.CHERRY_LEAVES.get());
		wreath(cherryExporter, CherryModule.REDLOVE_WREATH.get(), CherryModule.REDLOVE_LEAVES.get());

		Consumer<FinishedRecipe> beeExporter = withConditions(exporter, ModuleLoadedCondition.provider(new ResourceLocation(FruitfulFun.ID, "bee")));
		oneToOneConversionRecipe(beeExporter, Items.GLASS_BOTTLE, BeeModule.MUTAGEN.get(), null);
	}

	public static void wreath(Consumer<FinishedRecipe> exporter, ItemLike wreath, ItemLike leaves) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, wreath)
				.pattern(" # ")
				.pattern("# #")
				.pattern(" # ")
				.define('#', leaves)
				.unlockedBy("has_item", has(leaves))
				.save(exporter);
	}
}
