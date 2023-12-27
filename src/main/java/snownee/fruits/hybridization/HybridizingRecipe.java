package snownee.fruits.hybridization;

import java.util.Collection;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.kiwi.recipe.Simple;

public class HybridizingRecipe extends Simple<HybridizingContext> {

	protected final Block result;
	public final ImmutableSet<Block> ingredients;

	public HybridizingRecipe(ResourceLocation id, Block result, ImmutableSet<Block> ingredients) {
		super(id);
		this.result = result;
		this.ingredients = ingredients;
	}

	@Override
	public boolean matches(HybridizingContext inv, Level worldIn) {
		return inv.ingredients.size() >= ingredients.size() && ingredients.stream().allMatch(inv.ingredients::contains);
	}

	public Block getResult(Collection<Block> types) {
		return result;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HybridizationModule.SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return HybridizationModule.RECIPE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<HybridizingRecipe> {

		@Override
		public HybridizingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Block result = readIngredient(json.get("result"));
			ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
			JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
			if (ingredients.size() < 2 || ingredients.size() > 4) {
				throw new JsonSyntaxException("Size of ingredients has to be in [2, 4]");
			}
			ingredients.forEach(e -> builder.add(readIngredient(e)));
			return new HybridizingRecipe(recipeId, result, builder.build());
		}

		protected static Block readIngredient(JsonElement element) {
			Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(element.getAsString()));
			Preconditions.checkArgument(block != Blocks.AIR);
			return block;
		}

		@Override
		public HybridizingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Block result = Objects.requireNonNull(buffer.readById(BuiltInRegistries.BLOCK));
			ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
			int size = buffer.readByte();
			for (int i = 0; i < size; i++) {
				builder.add(Objects.requireNonNull(buffer.readById(BuiltInRegistries.BLOCK)));
			}
			return new HybridizingRecipe(recipeId, result, builder.build());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, HybridizingRecipe recipe) {
			buffer.writeId(BuiltInRegistries.BLOCK, recipe.result);
			buffer.writeByte(recipe.ingredients.size());
			for (Block block : recipe.ingredients) {
				buffer.writeId(BuiltInRegistries.BLOCK, block);
			}
		}

	}

}
