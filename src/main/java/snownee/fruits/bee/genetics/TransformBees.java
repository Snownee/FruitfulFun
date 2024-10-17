package snownee.fruits.bee.genetics;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import snownee.fruits.bee.BeeModule;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.Reference;
import snownee.lychee.core.post.PostAction;
import snownee.lychee.core.post.PostActionType;
import snownee.lychee.core.recipe.ILycheeRecipe;

public class TransformBees extends PostAction {
	public final Reference target;
	public final ImmutableList<Trait> addTraits;
	public final ImmutableList<Trait> removeTraits;

	public TransformBees(Reference target, ImmutableList<Trait> addTraits, ImmutableList<Trait> removeTraits) {
		this.target = target;
		this.addTraits = addTraits;
		this.removeTraits = removeTraits;
	}

	@Override
	public PostActionType<?> getType() {
		return BeeModule.TRANSFORM_BEES.get();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

	@Override
	protected void apply(ILycheeRecipe<?> recipe, LycheeContext ctx, int times) {
		IntList indexes = recipe.getItemIndexes(this.target);
		for (int index : indexes) {
			ItemStack stack = ctx.getItem(index);
			CompoundTag blockEntityData = BlockItem.getBlockEntityData(stack);
			if (blockEntityData == null) {
				continue;
			}
			ListTag list = blockEntityData.getList(BeehiveBlockEntity.BEES, Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag entityData = list.getCompound(i).getCompound(BeehiveBlockEntity.ENTITY_DATA);
				entityData.remove(Bee.TAG_HAS_NECTAR);
				entityData.remove(Bee.TAG_ANGER_TIME);
				entityData.remove("InLove");
				entityData.putBoolean("PersistenceRequired", true);
				CompoundTag attributesTag = entityData.getCompound("FruitfulFun");
				GeneData geneData = new GeneData();
				geneData.fromNBT(attributesTag.getCompound("Genes"));
				CompoundTag lociTag = new CompoundTag();
				for (Trait trait : addTraits) {
					geneData.addExtraTrait(trait);
				}
				for (Trait trait : removeTraits) {
					geneData.removeExtraTrait(trait);
				}
				geneData.toNBT(lociTag);
				if (!lociTag.isEmpty()) {
					attributesTag.put("Genes", lociTag);
				}
				entityData.put("FruitfulFun", attributesTag);
			}
		}
	}

	public static class Type extends PostActionType<TransformBees> {
		@Override
		public TransformBees fromJson(JsonObject jsonObject) {
			Reference target = Reference.fromJson(jsonObject, "target");
			Set<Trait> addTraits = Sets.newHashSet();
			Set<Trait> removeTraits = Sets.newHashSet();
			readTraits(jsonObject, "traits", addTraits, removeTraits);
			return new TransformBees(target, ImmutableList.copyOf(addTraits), ImmutableList.copyOf(removeTraits));
		}

		private static void readTraits(JsonObject jsonObject, String key, Set<Trait> addTraits, Set<Trait> removeTraits) {
			JsonArray array = GsonHelper.getAsJsonArray(jsonObject, key, null);
			if (array == null) {
				return;
			}
			for (JsonElement element : array) {
				String id = element.getAsString();
				boolean remove = id.startsWith("!");
				if (remove) {
					id = id.substring(1);
				}
				Trait trait = Trait.REGISTRY.get(id);
				Preconditions.checkNotNull(trait, "Unknown trait: %s", id);
				if (remove) {
					removeTraits.add(trait);
				} else {
					addTraits.add(trait);
				}
			}
		}

		@Override
		public void toJson(TransformBees transformBees, JsonObject jsonObject) {
			Reference.toJson(transformBees.target, jsonObject, "target");
			writeTraits(jsonObject, "traits", transformBees.addTraits, transformBees.removeTraits);
		}

		private static void writeTraits(JsonObject jsonObject, String key, List<Trait> addTraits, List<Trait> removeTraits) {
			JsonArray array = new JsonArray();
			for (Trait trait : addTraits) {
				array.add(trait.name());
			}
			for (Trait trait : removeTraits) {
				array.add("!" + trait.name());
			}
			jsonObject.add(key, array);
		}

		@Override
		public TransformBees fromNetwork(FriendlyByteBuf buf) {
			return new TransformBees(Reference.fromNetwork(buf), readTraits(buf), readTraits(buf));
		}

		private static ImmutableList<Trait> readTraits(FriendlyByteBuf buf) {
			List<Trait> traits = buf.readList($ -> Trait.REGISTRY.get($.readUtf()));
			traits.removeIf(Objects::isNull);
			return ImmutableList.copyOf(traits);
		}

		@Override
		public void toNetwork(TransformBees transformBees, FriendlyByteBuf buf) {
			Reference.toNetwork(transformBees.target, buf);
			writeTraits(buf, transformBees.addTraits);
			writeTraits(buf, transformBees.removeTraits);
		}

		private static void writeTraits(FriendlyByteBuf buf, List<Trait> traits) {
			buf.writeCollection(traits, ($, trait) -> $.writeUtf(trait.name()));
		}
	}
}
