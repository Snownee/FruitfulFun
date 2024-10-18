package snownee.fruits.ritual;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import snownee.fruits.FruitfulFun;
import snownee.fruits.util.CommonProxy;
import snownee.fruits.util.CustomIngredient;
import snownee.fruits.util.CustomIngredientSerializer;

public class BeehiveIngredient implements CustomIngredient {
	public static final CustomIngredientSerializer<BeehiveIngredient> SERIALIZER = new Serializer();
	private final boolean requireBees;
	private final List<ItemStack> matchingStacks = List.of(Items.BEEHIVE.getDefaultInstance(), Items.BEE_NEST.getDefaultInstance());

	public BeehiveIngredient(boolean requireBees) {
		this.requireBees = requireBees;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (!CommonProxy.isBeehive(stack)) {
			return false;
		}
		if (requireBees) {
			CompoundTag tag = stack.getTag();
			if (tag == null) {
				return false;
			}
			CompoundTag blockEntityData = BlockItem.getBlockEntityData(stack);
			if (blockEntityData == null) {
				return false;
			}
			ListTag list = blockEntityData.getList(BeehiveBlockEntity.BEES, Tag.TAG_COMPOUND);
			if (list.isEmpty()) {
				return false;
			}
		}
		return true;
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
		public static final BeehiveIngredient TRUE = new BeehiveIngredient(true);
		public static final BeehiveIngredient FALSE = new BeehiveIngredient(false);

		@Override
		public ResourceLocation getIdentifier() {
			return FruitfulFun.id("beehive");
		}

		@Override
		public BeehiveIngredient read(JsonObject json) {
			return GsonHelper.getAsBoolean(json, "require_bees", false) ? TRUE : FALSE;
		}

		@Override
		public void write(JsonObject json, BeehiveIngredient ingredient) {
			if (ingredient.requireBees) {
				json.addProperty("require_bees", true);
			}
		}

		@Override
		public BeehiveIngredient read(FriendlyByteBuf buf) {
			return buf.readBoolean() ? TRUE : FALSE;
		}

		@Override
		public void write(FriendlyByteBuf buf, BeehiveIngredient ingredient) {
			buf.writeBoolean(ingredient.requireBees);
		}
	}
}
