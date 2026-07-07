package snownee.fruits.ritual;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Bees;
import snownee.fruits.FruitfulFun;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.recipe.CustomIngredient;
import snownee.kiwi.recipe.CustomIngredientSerializer;

public record BeehiveIngredient(boolean requireBees) implements CustomIngredient {
	public static final BeehiveIngredient TRUE = new BeehiveIngredient(true);
	public static final BeehiveIngredient FALSE = new BeehiveIngredient(false);
	public static final CustomIngredientSerializer<BeehiveIngredient> SERIALIZER = new Serializer();
	private static final Supplier<List<Holder<Item>>> ITEMS = Suppliers.memoize(() -> Stream.of(Items.BEEHIVE, Items.BEE_NEST)
			.map(BuiltInRegistries.ITEM::wrapAsHolder)
			.toList());

	@Override
	public boolean test(ItemStack stack) {
		if (!CommonProxy.isBeehive(stack)) {
			return false;
		}
		return !requireBees || !stack.getOrDefault(DataComponents.BEES, Bees.EMPTY).bees().isEmpty();
	}

	@Override
	public Stream<Holder<Item>> items() {
		return ITEMS.get().stream();
	}

	@Override
	public boolean requiresTesting() {
		return true;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public record Serializer() implements CustomIngredientSerializer<BeehiveIngredient> {
		public static final MapCodec<BeehiveIngredient> CODEC = Codec.BOOL.xmap($ -> $ ? TRUE : FALSE, BeehiveIngredient::requireBees)
				.fieldOf("require_bees");
		public static final StreamCodec<RegistryFriendlyByteBuf, BeehiveIngredient> STREAM_CODEC = StreamCodec.of(
				(buf, $) -> buf.writeBoolean($.requireBees),
				buf -> buf.readBoolean() ? TRUE : FALSE);

		@Override
		public Identifier getIdentifier() {
			return FruitfulFun.id("beehive");
		}

		@Override
		public MapCodec<BeehiveIngredient> getCodec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BeehiveIngredient> getStreamCodec() {
			return STREAM_CODEC;
		}
	}
}
