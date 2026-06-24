package snownee.fruits.bee;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.kiwi.util.KUtil;
import snownee.lychee.util.action.PostAction;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.context.LycheeContextKey;
import snownee.lychee.util.json.JsonPointer;
import snownee.lychee.util.recipe.ILycheeRecipe;
import snownee.lychee.util.recipe.LycheeRecipe;
import snownee.lychee.util.recipe.LycheeRecipeCommonProperties;

public class HybridizingRecipe extends LycheeRecipe<LycheeContext> {
	public static final MapCodec<HybridizingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			LycheeRecipeCommonProperties.SIMPLE_MAP_CODEC.forGetter(HybridizingRecipe::commonProperties),
			Codec.STRING.sizeLimitedListOf(4).optionalFieldOf("pollens", List.of()).forGetter($ -> $.pollens),
			Codec.STRING.sizeLimitedListOf(4).optionalFieldOf("ending_step", List.of()).forGetter($ -> $.endingStep),
			Codec.BOOL.optionalFieldOf("reset", true).forGetter($ -> $.resetPollens)
	).apply(instance, HybridizingRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, HybridizingRecipe> STREAM_CODEC = StreamCodec.composite(
			LycheeRecipeCommonProperties.STREAM_CODEC,
			HybridizingRecipe::commonProperties,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(4)),
			$ -> $.pollens,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(4)),
			$ -> $.endingStep,
			ByteBufCodecs.BOOL,
			$ -> $.resetPollens,
			HybridizingRecipe::new
	);

	private final List<String> pollens;
	private final List<String> endingStep;
	private final boolean resetPollens;
	private final NonNullList<Ingredient> ingredients = NonNullList.create();

	public HybridizingRecipe(
			LycheeRecipeCommonProperties commonProperties,
			List<String> pollens,
			List<String> endingStep,
			boolean resetPollens) {
		super(commonProperties);
		this.pollens = pollens.stream().map(KUtil::trimRL).toList();
		this.endingStep = endingStep.stream().map(KUtil::trimRL).toList();
		this.resetPollens = resetPollens;
		for (String pollen : pollens) {
			Item item = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(pollen)).asItem();
			if (item != Items.AIR) {
				ingredients.add(Ingredient.of(item));
			}
		}
		for (String pollen : endingStep) {
			if (!pollens.contains(pollen)) {
				throw new IllegalArgumentException("Ending step must be in pollens");
			}
		}
	}

	@Override
	public boolean matches(LycheeContext ctx, Level worldIn) {
		BeeAttributes attributes = BeeAttributes.of(ctx.get(LycheeContextKey.LOOT_PARAMS).get(LootContextParams.THIS_ENTITY));
		return attributes.getPollens().size() >= pollens.size() && attributes.getPollens().containsAll(pollens);
	}

	@Override
	public RecipeSerializer<? extends ILycheeRecipe<LycheeContext>> getSerializer() {
		return BeeModule.SERIALIZER.get();
	}

	@Override
	public HybridizingRecipeType getType() {
		return BeeModule.RECIPE_TYPE.get();
	}

	public List<String> pollens() {
		return pollens;
	}

	public List<String> endingStep() {
		if (endingStep.isEmpty()) {
			return pollens;
		}
		return endingStep;
	}

	public boolean resetPollens() {
		return resetPollens;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@Override
	public IntList getItemIndexes(JsonPointer pointer) {
		return IntList.of();
	}

	public void addInvisibleInputs(Consumer<ItemStack> acceptor) {
		for (String pollen : pollens) {
			Block block = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(pollen));
			if (block instanceof FruitLeavesBlock leavesBlock) {
				acceptor.accept(new ItemStack(leavesBlock.type.value().sapling.get()));
			}
		}
	}

	public void addInvisibleOutputs(Consumer<ItemStack> acceptor) {
		allActions().filter(Predicate.not(PostAction::hidden))
				.flatMap($ -> $.getOutputItems().stream())
				.map(ItemStack::getItem)
				.distinct()
				.map($ -> {
					if (Block.byItem($) instanceof FruitLeavesBlock block) {
						return new ItemStack(block.type.value().sapling.get());
					}
					return null;
				})
				.filter(Objects::nonNull)
				.forEach(acceptor);
	}
}
