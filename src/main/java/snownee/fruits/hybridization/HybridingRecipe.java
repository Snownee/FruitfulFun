package snownee.fruits.hybridization;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import snownee.fruits.FruitType;
import snownee.kiwi.crafting.Recipe;

public class HybridingRecipe extends Recipe<HybridingContext> {

	protected final Either<FruitType, Block> result;
	public final ImmutableSet<Either<FruitType, Block>> ingredients;

	public HybridingRecipe(ResourceLocation id, Either<FruitType, Block> result, ImmutableSet<Either<FruitType, Block>> ingredients) {
		super(id);
		this.result = result;
		this.ingredients = ingredients;
	}

	@Override
	public boolean matches(HybridingContext inv, World worldIn) {
		return ingredients.stream().allMatch(inv.ingredients::contains);
	}

	public Either<FruitType, Block> getResult(Collection<Either<FruitType, Block>> types) {
		return result;
	}

	public Block getResultAsBlock(Collection<Either<FruitType, Block>> types) {
		return getResult(types).map(t -> t.leaves, b -> b);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Hybridization.SERIALIZER;
	}

	@Override
	public IRecipeType<?> getType() {
		return Hybridization.RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<HybridingRecipe> {

		@Override
		public HybridingRecipe read(ResourceLocation recipeId, JsonObject json) {
			Either<FruitType, Block> result = readIngredient(JSONUtils.getJsonObject(json, "result"));
			ImmutableSet.Builder<Either<FruitType, Block>> builder = ImmutableSet.builder();
			JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
			if (ingredients.size() < 2 || ingredients.size() > 4) {
				throw new JsonSyntaxException("Size of ingredients has to be in [2, 4]");
			}
			ingredients.forEach(e -> builder.add(readIngredient(e.getAsJsonObject())));
			return new HybridingRecipe(recipeId, result, builder.build());
		}

		protected static Either<FruitType, Block> readIngredient(JsonObject object) {
			if (object.has("block")) {
				Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getString(object, "block")));
				return Either.right(block);
			} else if (object.has("fruit")) {
				FruitType type = FruitType.parse(JSONUtils.getString(object, "fruit"));
				return Either.left(type);
			}
			throw new JsonSyntaxException("Expect key 'block' or 'fruit'");
		}

		@Override
		public HybridingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			Either<FruitType, Block> result;
			if (buffer.readBoolean()) {
				result = Either.left(FruitType.parse(buffer.readString(32767)));
			} else {
				result = Either.right(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
			}
			ImmutableSet.Builder<Either<FruitType, Block>> builder = ImmutableSet.builder();
			int size = buffer.readByte(); // blocks
			for (int i = 0; i < size; i++) {
				Block block = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
				builder.add(Either.right(block));
			}
			size = buffer.readByte(); // types
			for (int i = 0; i < size; i++) {
				builder.add(Either.left(FruitType.parse(buffer.readString(32767))));
			}
			return new HybridingRecipe(recipeId, result, builder.build());
		}

		@Override
		public void write(PacketBuffer buffer, HybridingRecipe recipe) {
			recipe.result.ifLeft(type -> {
				buffer.writeBoolean(true);
				buffer.writeString(type.name());
			}).ifRight(block -> {
				buffer.writeBoolean(false);
				buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
			});
			List<FruitType> types = Lists.newArrayList();
			List<Block> blocks = Lists.newArrayList();
			recipe.ingredients.forEach(e -> e.map(types::add, blocks::add));
			buffer.writeByte(blocks.size());
			for (Block block : blocks) {
				buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
			}
			buffer.writeByte(types.size());
			for (FruitType type : types) {
				buffer.writeString(type.name());
			}
		}

	}
}
