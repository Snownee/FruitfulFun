package snownee.fruits.ritual;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.lychee.LycheeLootContextParams;
import snownee.lychee.core.input.ItemHolderCollection;
import snownee.lychee.core.recipe.LycheeRecipe;
import snownee.lychee.core.recipe.type.LycheeRecipeType;

public class DragonRitualRecipe extends LycheeRecipe<DragonRitualContext> {
	protected Ingredient input;

	public DragonRitualRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(DragonRitualContext ctx, Level worldIn) {
		return this.input.test(ctx.getItem(0));
	}

	@Override
	public LycheeRecipe.Serializer<?> getSerializer() {
		return RitualModule.SERIALIZER.get();
	}

	@Override
	public LycheeRecipeType<?, ?> getType() {
		return RitualModule.RECIPE_TYPE.get();
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, input);
	}

	public static class Serializer extends LycheeRecipe.Serializer<DragonRitualRecipe> {
		public Serializer() {
			super(DragonRitualRecipe::new);
		}

		@Override
		public void fromJson(DragonRitualRecipe recipe, JsonObject jsonObject) {
			recipe.input = Ingredient.fromJson(jsonObject.get("item_in"));
		}

		@Override
		public void fromNetwork(DragonRitualRecipe recipe, FriendlyByteBuf buf) {
			recipe.input = Ingredient.fromNetwork(buf);
		}

		@Override
		public void toNetwork0(FriendlyByteBuf buf, DragonRitualRecipe recipe) {
			recipe.input.toNetwork(buf);
		}
	}

	public static boolean on(ItemEntity entity, BlockPos pos, int heads) {
		DragonRitualContext.Builder builder = new DragonRitualContext.Builder(entity.level(), heads);
		builder.withParameter(LycheeLootContextParams.BLOCK_POS, pos);
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
