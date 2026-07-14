package snownee.fruits.bee.genetics;

import com.mojang.serialization.MapCodec;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.fruits.FFRegistries;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.BeeVariant;
import snownee.lychee.context.ActionContext;
import snownee.lychee.util.RegistryEntryDisplay;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.context.LycheeContextKey;
import snownee.lychee.util.contextual.ContextualCondition;
import snownee.lychee.util.contextual.ContextualConditionType;

public record BeeIsVariant(ResourceKey<BeeVariant> variant) implements ContextualCondition {
	@Override
	public ContextualConditionType<?> type() {
		return BeeModule.BEE_IS_VARIANT.get();
	}

	@Override
	public int test(LycheeContext lycheeContext, ActionContext actionContext, int i) {
		Entity entity = lycheeContext.get(LycheeContextKey.LOOT_PARAMS).get(LootContextParams.THIS_ENTITY);
		if (entity instanceof Bee) {
			BeeAttributes attributes = BeeAttributes.of(entity);
			if (attributes.variant().is(variant)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public MutableComponent getDescription(boolean inverted) {
		return Component.translatable(
				getDescriptionId(inverted),
				RegistryEntryDisplay.of(variant, FFRegistries.BEE_VARIANT_KEY).withStyle(ChatFormatting.WHITE));
	}

	public static class Type implements ContextualConditionType<BeeIsVariant> {
		public static final MapCodec<BeeIsVariant> CODEC = ResourceKey.codec(FFRegistries.BEE_VARIANT_KEY).fieldOf("variant").xmap(
				BeeIsVariant::new,
				BeeIsVariant::variant);

		@Override
		public MapCodec<BeeIsVariant> codec() {
			return CODEC;
		}
	}
}
