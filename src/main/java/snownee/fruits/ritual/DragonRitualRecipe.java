package snownee.fruits.ritual;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.kiwi.recipe.SizedIngredient;
import snownee.lychee.LootContextKeys;
import snownee.lychee.context.LootParamsContext;
import snownee.lychee.util.codec.LycheeCodecs;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.context.LycheeContextKey;
import snownee.lychee.util.input.ItemStackHolderCollection;
import snownee.lychee.util.recipe.LycheeRecipe;
import snownee.lychee.util.recipe.LycheeRecipeCommonProperties;
import snownee.lychee.util.recipe.LycheeRecipeType;

public class DragonRitualRecipe extends LycheeRecipe<LycheeContext> {
	public static final MapCodec<DragonRitualRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			LycheeRecipeCommonProperties.SIMPLE_MAP_CODEC.forGetter(LycheeRecipe::commonProperties),
			LycheeCodecs.SIZED_INGREDIENT.fieldOf("item_in").forGetter(r -> r.input)
	).apply(instance, DragonRitualRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DragonRitualRecipe> STREAM_CODEC = StreamCodec.composite(
			LycheeRecipeCommonProperties.STREAM_CODEC,
			LycheeRecipe::commonProperties,
			SizedIngredient.STREAM_CODEC,
			r -> r.input,
			DragonRitualRecipe::new);
	private static final LycheeContextKey.Required<Integer> DRAGON_HEADS = LycheeContextKey.req("dragon_heads");

	protected SizedIngredient input;

	public DragonRitualRecipe(LycheeRecipeCommonProperties commonProperties, SizedIngredient input) {
		super(commonProperties);
		this.input = input;
	}

	@Override
	public boolean matches(LycheeContext ctx, Level level) {
		return this.input.test(ctx.getItem(0));
	}

	@Override
	public RecipeSerializer<DragonRitualRecipe> getSerializer() {
		return RitualModule.SERIALIZER.get();
	}

	@Override
	public LycheeRecipeType<?> getType() {
		return RitualModule.RECIPE_TYPE.get();
	}

	@Override
	public List<SizedIngredient> sizedIngredients() {
		return List.of(input);
	}

	public static boolean on(ItemEntity entity, BlockPos pos, int heads, BlockState state) {
		LycheeContext ctx = new LycheeContext();
		ctx.put(LycheeContextKey.LEVEL, entity.level());
		ctx.put(LycheeContextKey.ITEM, ItemStackHolderCollection.InWorld.of(entity));
		ctx.put(DRAGON_HEADS, heads);
		LootParamsContext lootParams = ctx.initLootParams(RitualModule.RECIPE_TYPE.get());
		lootParams.set(LootContextParams.ORIGIN, entity.position());
		lootParams.set(LootContextParams.THIS_ENTITY, entity);
		lootParams.set(LootContextParams.BLOCK_STATE, state);
		lootParams.set(LootContextKeys.BLOCK_POS, pos);
		lootParams.validate();
		Optional<RecipeHolder<DragonRitualRecipe>> recipeHolder = RitualModule.RECIPE_TYPE.get().findFirst(ctx, entity.level());
		if (recipeHolder.isPresent()) {
			ctx.put(recipeHolder.get());
			DragonRitualRecipe recipe = recipeHolder.get().value();
			int times = recipe.getRandomRepeats(entity.getItem().getCount() / recipe.input.count(), ctx);
			recipe.applyPostActions(ctx, times);
			ctx.get(LycheeContextKey.ITEM).postApply(true, times);
		}
		return recipeHolder.isPresent();
	}

}
