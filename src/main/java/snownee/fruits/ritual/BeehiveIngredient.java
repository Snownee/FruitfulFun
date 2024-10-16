package snownee.fruits.ritual;

import java.util.List;

import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.fruits.FruitfulFun;
import snownee.fruits.util.CommonProxy;

public class BeehiveIngredient implements CustomIngredient {
	public static final CustomIngredientSerializer<BeehiveIngredient> SERIALIZER = new Serializer();
	private final List<ItemStack> matchingStacks = List.of(Items.BEEHIVE.getDefaultInstance(), Items.BEE_NEST.getDefaultInstance());

	@Override
	public boolean test(ItemStack stack) {
		return CommonProxy.isBeehive(stack);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		return matchingStacks;
	}

	@Override
	public boolean requiresTesting() {
		return false;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer implements CustomIngredientSerializer<BeehiveIngredient> {
		public static final BeehiveIngredient INSTANCE = new BeehiveIngredient();

		@Override
		public ResourceLocation getIdentifier() {
			return FruitfulFun.id("beehive");
		}

		@Override
		public BeehiveIngredient read(JsonObject json) {
			return INSTANCE;
		}

		@Override
		public void write(JsonObject json, BeehiveIngredient ingredient) {
		}

		@Override
		public BeehiveIngredient read(FriendlyByteBuf buf) {
			return INSTANCE;
		}

		@Override
		public void write(FriendlyByteBuf buf, BeehiveIngredient ingredient) {
		}
	}
}
