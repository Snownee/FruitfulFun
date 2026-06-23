package snownee.fruits.bee.genetics;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.lychee.context.ActionContext;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.context.LycheeContextKey;
import snownee.lychee.util.contextual.ContextualCondition;
import snownee.lychee.util.contextual.ContextualConditionType;

public record BeeHasTrait(Trait trait) implements ContextualCondition {
	@Override
	public ContextualConditionType<?> type() {
		return BeeModule.BEE_HAS_TRAIT.get();
	}

	@Override
	public int test(LycheeContext lycheeContext, ActionContext actionContext, int i) {
		Entity entity = lycheeContext.get(LycheeContextKey.LOOT_PARAMS).get(LootContextParams.THIS_ENTITY);
		if (entity instanceof Bee) {
			BeeAttributes attributes = BeeAttributes.of(entity);
			if (attributes.hasTrait(trait)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public MutableComponent getDescription(boolean inverted) {
		String key = getDescriptionId(inverted);
		return Component.translatable(key, trait.getDisplayName().withStyle(ChatFormatting.WHITE));
	}

	public static class Type implements ContextualConditionType<BeeHasTrait> {
		public static final MapCodec<BeeHasTrait> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Trait.CODEC.fieldOf("trait").forGetter(BeeHasTrait::trait)
		).apply(instance, BeeHasTrait::new));

		@Override
		public MapCodec<BeeHasTrait> codec() {
			return CODEC;
		}
	}
}
