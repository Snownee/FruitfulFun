package snownee.fruits.bee.genetics;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import snownee.fruits.FFRegistries;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.BeeVariant;
import snownee.lychee.context.ActionContext;
import snownee.lychee.util.Reference;
import snownee.lychee.util.action.PostAction;
import snownee.lychee.util.action.PostActionCommonProperties;
import snownee.lychee.util.action.PostActionType;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.context.LycheeContextKey;

public record TransformBees(
		PostActionCommonProperties commonProperties,
		Reference target,
		List<Trait> addTraits,
		List<Trait> removeTraits,
		Optional<ResourceKey<BeeVariant>> variant) implements PostAction {

	@Override
	public PostActionType<?> type() {
		return BeeModule.TRANSFORM_BEES.get();
	}

	@Override
	public void apply(LycheeContext ctx, ActionContext actionContext, int i) {
		IntList indexes = ctx.get(LycheeContextKey.RECIPE).getItemIndexes(this.target);
		for (int index : indexes) {
			ItemStack stack = ctx.getItem(index);
			Bees bees = stack.getOrDefault(DataComponents.BEES, Bees.EMPTY);
			if (bees.bees().isEmpty()) {
				continue;
			}

			List<BeehiveBlockEntity.Occupant> transformed = Lists.newArrayList();
			for (BeehiveBlockEntity.Occupant occupant : bees.bees()) {
				if (occupant.entityData().type() != EntityType.BEE) {
					transformed.add(occupant);
					continue;
				}
				CompoundTag entityData = occupant.entityData().copyTagWithoutId();
				// See Mob.java
				entityData.putBoolean("PersistenceRequired", true);
				CompoundTag attributesTag = entityData.getCompoundOrEmpty("FruitfulFun");
				if ((!addTraits.isEmpty() || !removeTraits.isEmpty()) && attributesTag.contains("Genes")) {
					GeneData geneData = attributesTag.read("Genes", GeneData.CODEC).orElseGet(GeneData::new);
					for (Trait trait : addTraits) {
						geneData.addExtraTrait(trait);
					}
					for (Trait trait : removeTraits) {
						geneData.removeExtraTrait(trait);
					}
					attributesTag.store("Genes", GeneData.CODEC, geneData);
				}
				variant.ifPresent(key -> attributesTag.putString("ForcedVariant", key.toString()));
				entityData.put("FruitfulFun", attributesTag);
				transformed.add(new BeehiveBlockEntity.Occupant(
						TypedEntityData.of(EntityType.BEE, entityData),
						occupant.ticksInHive(),
						occupant.minTicksInHive()));
			}
			stack.set(DataComponents.BEES, new Bees(transformed));
		}
	}

	public static class Type implements PostActionType<TransformBees> {
		public static final MapCodec<TransformBees> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				PostActionCommonProperties.MAP_CODEC.forGetter(TransformBees::commonProperties),
				Reference.CODEC.optionalFieldOf("target", Reference.DEFAULT).forGetter(TransformBees::target),
				ExtraCodecs.compactListCodec(Trait.CODEC).optionalFieldOf("add_trait", List.of()).forGetter(TransformBees::addTraits),
				ExtraCodecs.compactListCodec(Trait.CODEC)
						.optionalFieldOf("remove_trait", List.of())
						.forGetter(TransformBees::removeTraits),
				ResourceKey.codec(FFRegistries.BEE_VARIANT_KEY).optionalFieldOf("variant").forGetter(TransformBees::variant)
		).apply(instance, TransformBees::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, TransformBees> STREAM_CODEC = StreamCodec.composite(
				PostActionCommonProperties.STREAM_CODEC,
				TransformBees::commonProperties,
				Reference.STREAM_CODEC,
				TransformBees::target,
				Trait.STREAM_CODEC.apply(ByteBufCodecs.list()),
				TransformBees::addTraits,
				Trait.STREAM_CODEC.apply(ByteBufCodecs.list()),
				TransformBees::removeTraits,
				ResourceKey.streamCodec(FFRegistries.BEE_VARIANT_KEY).apply(ByteBufCodecs::optional),
				TransformBees::variant,
				TransformBees::new);

		@Override
		public MapCodec<TransformBees> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, TransformBees> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
