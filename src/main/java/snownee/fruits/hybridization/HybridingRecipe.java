package snownee.fruits.hybridization;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import snownee.fruits.Fruits;
import snownee.kiwi.crafting.Recipe;

public class HybridingRecipe extends Recipe<HybridingContext> {

    protected final Fruits.Type result;
    protected final ImmutableSet<Either<Fruits.Type, Block>> ingredients;

    public HybridingRecipe(ResourceLocation id, Fruits.Type result, ImmutableSet<Either<Fruits.Type, Block>> ingredients) {
        super(id);
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(HybridingContext inv, World worldIn) {
        // TODO Auto-generated method stub
        return false;
    }

    public Fruits.Type getResult(Set<Either<Fruits.Type, Block>> types) {
        return result;
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
            Fruits.Type result = Fruits.Type.parse(JSONUtils.getString(json, "result"));
            ImmutableSet.Builder<Either<Fruits.Type, Block>> builder = ImmutableSet.builder();
            JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
            ingredients.forEach(e -> {
                if (e.getAsJsonObject().has("block")) {
                    Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getString(e, "block")));
                    builder.add(Either.right(block));
                } else {
                    Fruits.Type type = Fruits.Type.parse(JSONUtils.getString(e, "fruit"));
                    builder.add(Either.left(type));
                }
            });
            return new HybridingRecipe(recipeId, result, builder.build());
        }

        @Override
        public HybridingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Fruits.Type result = Fruits.Type.parse(buffer.readString(32767));
            ImmutableSet.Builder<Either<Fruits.Type, Block>> builder = ImmutableSet.builder();
            int size = buffer.readByte(); // blocks
            for (int i = 0; i < size; i++) {
                Block block = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
                builder.add(Either.right(block));
            }
            size = buffer.readByte(); // types
            for (int i = 0; i < size; i++) {
                builder.add(Either.left(Fruits.Type.parse(buffer.readString(32767))));
            }
            return new HybridingRecipe(recipeId, result, builder.build());
        }

        @Override
        public void write(PacketBuffer buffer, HybridingRecipe recipe) {
            buffer.writeString(recipe.result.name());
            List<Fruits.Type> types = Lists.newArrayList();
            List<Block> blocks = Lists.newArrayList();
            recipe.ingredients.forEach(e -> e.map(types::add, blocks::add));
            buffer.writeByte(blocks.size());
            for (Block block : blocks) {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
            }
            buffer.writeByte(types.size());
            for (Fruits.Type type : types) {
                buffer.writeString(type.name());
            }
        }

    }
}
