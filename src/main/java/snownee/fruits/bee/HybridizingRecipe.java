package snownee.fruits.bee;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.recipe.BlockKeyRecipe;
import snownee.lychee.core.recipe.ILycheeRecipe;
import snownee.lychee.core.recipe.LycheeRecipe;
import snownee.lychee.core.recipe.type.LycheeRecipeType;
import snownee.lychee.util.json.JsonPointer;

public class HybridizingRecipe extends LycheeRecipe<LycheeContext> implements BlockKeyRecipe<HybridizingRecipe> {

	public Collection<String> pollens = List.of();
	public Collection<String> endingStep = List.of();
	public final NonNullList<Ingredient> ingredients = NonNullList.create();

	public HybridizingRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(LycheeContext ctx, Level worldIn) {
		BeeAttributes attributes = BeeAttributes.of(ctx.getParam(LootContextParams.THIS_ENTITY));
		return attributes.getPollens().size() >= pollens.size() && attributes.getPollens().containsAll(pollens);
	}

	@Override
	public LycheeRecipe.Serializer<?> getSerializer() {
		return BeeModule.SERIALIZER.get();
	}

	@Override
	public LycheeRecipeType<?, ?> getType() {
		return BeeModule.RECIPE_TYPE.get();
	}

	@Override
	public int compareTo(@NotNull HybridizingRecipe o) {
		return 0;
	}

	@Override
	public BlockPredicate getBlock() {
		return BlockPredicate.ANY; // not applicable
	}

	public Collection<String> endingStep() {
		if (endingStep.isEmpty()) {
			return pollens;
		}
		return endingStep;
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	public void refreshIngredients() {
		ingredients.clear();
		for (String pollen : pollens) {
			Item item = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(pollen)).asItem();
			if (item != Items.AIR) {
				ingredients.add(Ingredient.of(item));
			}
		}
	}

	@Override
	public IntList getItemIndexes(JsonPointer pointer) {
		return IntList.of();
	}

	public void addInvisibleInputs(Consumer<ItemStack> acceptor) {
		for (String pollen : pollens) {
			Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(pollen));
			if (block instanceof FruitLeavesBlock leavesBlock) {
				acceptor.accept(new ItemStack(leavesBlock.type.get().sapling.get()));
			}
		}
	}

	public void addInvisibleOutputs(Consumer<ItemStack> acceptor) {
		ILycheeRecipe.filterHidden(getAllActions())
				.flatMap($ -> $.getItemOutputs().stream())
				.map(ItemStack::getItem)
				.distinct()
				.map($ -> {
					if (Block.byItem($) instanceof FruitLeavesBlock block) {
						return new ItemStack(block.type.get().sapling.get());
					}
					return null;
				})
				.filter(Objects::nonNull)
				.forEach(acceptor);
	}

	public static class Serializer extends LycheeRecipe.Serializer<HybridizingRecipe> {
		public Serializer() {
			super(HybridizingRecipe::new);
		}

		@Override
		public void fromJson(HybridizingRecipe recipe, JsonObject jsonObject) {
			JsonArray ingredients = GsonHelper.getAsJsonArray(jsonObject, "pollens");
			Preconditions.checkArgument(!ingredients.isEmpty() && ingredients.size() <= 4, "Size of pollens has to be in [1, 4]");
			recipe.pollens = Sets.newLinkedHashSetWithExpectedSize(ingredients.size());
			for (JsonElement element : ingredients) {
				String s = element.getAsString();
				Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(s));
				Preconditions.checkArgument(block != Blocks.AIR, "Unknown block: " + s);
				recipe.pollens.add(s);
			}
			Preconditions.checkArgument(recipe.pollens.size() == Sets.newHashSet(recipe.pollens).size(), "Pollens must be unique");
			JsonArray endingStep = GsonHelper.getAsJsonArray(jsonObject, "ending_step", null);
			if (endingStep != null) {
				Preconditions.checkArgument(!endingStep.isEmpty() && endingStep.size() <= 4, "Size of ending_step has to be in [1, 4]");
				recipe.endingStep = Sets.newLinkedHashSetWithExpectedSize(endingStep.size());
				for (JsonElement element : endingStep) {
					String s = element.getAsString();
					Preconditions.checkArgument(recipe.pollens.contains(s), "Ending step must be in pollens");
					recipe.endingStep.add(s);
				}
			}
			recipe.refreshIngredients();
		}

		@Override
		public void fromNetwork(HybridizingRecipe recipe, FriendlyByteBuf buf) {
			recipe.pollens = List.copyOf(buf.readList(FriendlyByteBuf::readUtf));
			recipe.endingStep = List.copyOf(buf.readList(FriendlyByteBuf::readUtf));
			recipe.refreshIngredients();
		}

		@Override
		public void toNetwork0(FriendlyByteBuf buf, HybridizingRecipe recipe) {
			buf.writeCollection(recipe.pollens, FriendlyByteBuf::writeUtf);
			buf.writeCollection(recipe.endingStep, FriendlyByteBuf::writeUtf);
		}
	}

}
