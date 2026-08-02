package snownee.fruits.compat.lychee;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.fruits.ritual.RitualModule;
import snownee.kiwi.loader.Platform;

public class LycheeCompat {
	public static void init() {
		boolean isClient = Platform.isPhysicalClient();
		if (Hooks.bee) {
			RecipeSynchronization.synchronizeRecipeSerializer(BeeModule.RECIPE_SERIALIZER.getOrCreate());
		}
		if (Hooks.ritual) {
			RecipeSynchronization.synchronizeRecipeSerializer(RitualModule.RECIPE_SERIALIZER.getOrCreate());
		}
		if (Platform.isPhysicalClient()) {
			LycheeCompatClient.init();
		}
	}

	public static List<Input> getInputs(HybridizingRecipe recipe) {
		List<Input> inputs = Lists.newArrayListWithExpectedSize(recipe.pollens().size());
		for (String pollen : recipe.pollens()) {
			Block block = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(pollen));
			Item item = block.asItem();
			if (item == Items.AIR) {
				inputs.add(new Input(block));
			} else {
				inputs.add(new Input(Ingredient.of(item)));
			}
		}
		return inputs;
	}

	public static class Input {
		@Nullable
		public final Ingredient itemIngredient;
		@Nullable
		public final Block block;

		public Input(Ingredient itemIngredient) {
			this.itemIngredient = Objects.requireNonNull(itemIngredient);
			this.block = null;
		}

		public Input(Block block) {
			this.itemIngredient = null;
			this.block = Objects.requireNonNull(block);
		}

		public boolean isItem() {
			return itemIngredient != null;
		}
	}
}
