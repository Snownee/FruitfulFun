package snownee.fruits.util;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import snownee.fruits.mixin.forge.CraftingHelperAccess;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class CustomIngredientImpl extends AbstractIngredient {
	public static IIngredientSerializer<?> getWrappedSerializer(ResourceLocation identifier) {
		Objects.requireNonNull(identifier, "Identifier may not be null.");
		return Objects.requireNonNull(CraftingHelperAccess.getIngredients().get(identifier));
	}

	private final CustomIngredient customIngredient;

	public CustomIngredientImpl(CustomIngredient customIngredient) {
		this.customIngredient = customIngredient;
	}

	@Override
	public boolean test(@Nullable ItemStack itemStack) {
		return itemStack != null && customIngredient.test(itemStack);
	}

	@Override
	public ItemStack[] getItems() {
		if (itemStacks == null) {
			itemStacks = customIngredient.getMatchingStacks().toArray(ItemStack[]::new);
		}
		return itemStacks;
	}

	@Override
	public boolean isEmpty() {
		return itemStacks != null && itemStacks.length == 0;
	}

	@Override
	public boolean isSimple() {
		return !customIngredient.requiresTesting();
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return getWrappedSerializer(this.customIngredient.getSerializer().getIdentifier());
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", customIngredient.getSerializer().getIdentifier().toString());
		customIngredient.getSerializer().write(json, coerceIngredient());
		return json;
	}

	private <T> T coerceIngredient() {
		//noinspection unchecked
		return (T) customIngredient;
	}

	public record Serializer(CustomIngredientSerializer<?> serializer) implements IIngredientSerializer<CustomIngredientImpl> {

		@Override
		public CustomIngredientImpl parse(FriendlyByteBuf buffer) {
			return new CustomIngredientImpl(serializer.read(buffer));
		}

		@Override
		public CustomIngredientImpl parse(JsonObject json) {
			return new CustomIngredientImpl(serializer.read(json));
		}

		@Override
		public void write(FriendlyByteBuf buffer, CustomIngredientImpl ingredient) {
			serializer.write(buffer, ingredient.coerceIngredient());
		}
	}
}
