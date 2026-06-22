package snownee.fruits.ritual;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.kiwi.recipe.SizedIngredient;
import snownee.lychee.LootContextKeys;
import snownee.lychee.util.codec.LycheeCodecs;
import snownee.lychee.util.context.LycheeContext;
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

	public static boolean on(ItemEntity entity, BlockPos pos, int heads) {
		DragonRitualContext.Builder builder = new DragonRitualContext.Builder(entity.level(), heads);
		builder.withParameter(LootContextKeys.BLOCK_POS, pos);
		builder.withParameter(LootContextParams.ORIGIN, entity.position());
		builder.withParameter(LootContextParams.THIS_ENTITY, entity);
		DragonRitualContext ctx = builder.create(RitualModule.RECIPE_TYPE.get().contextParamSet);
		ctx.itemHolders = ItemHolderCollection.InWorld.of(entity);
		Optional<DragonRitualRecipe> recipe = RitualModule.RECIPE_TYPE.get().findFirst(ctx, entity.level());
		if (recipe.isPresent()) {
			int times = recipe.get().getRandomRepeats(entity.getItem().getCount(), ctx);
			recipe.get().applyPostActions(ctx, times);
			ctx.itemHolders.postApply(ctx.runtime.doDefault, times);
		}
		return recipe.isPresent();
	}

}
