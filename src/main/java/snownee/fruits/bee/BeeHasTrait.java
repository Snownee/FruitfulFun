package snownee.fruits.bee;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.fruits.bee.genetics.Trait;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.contextual.ContextualCondition;
import snownee.lychee.core.contextual.ContextualConditionType;
import snownee.lychee.core.recipe.ILycheeRecipe;

public record BeeHasTrait(Trait trait) implements ContextualCondition {
	@Override
	public ContextualConditionType<? extends ContextualCondition> getType() {
		return BeeModule.BEE_HAS_TRAIT.get();
	}

	@Override
	public int test(ILycheeRecipe<?> recipe, LycheeContext ctx, int times) {
		Entity entity = ctx.getParam(LootContextParams.THIS_ENTITY);
		if (entity instanceof Bee) {
			BeeAttributes attributes = BeeAttributes.of(entity);
			if (attributes.hasTrait(trait)) {
				return times;
			}
		}
		return 0;
	}

	public MutableComponent getDescription(boolean inverted) {
		String key = this.makeDescriptionId(inverted);
		return Component.translatable(key, trait.getDisplayName().withStyle(ChatFormatting.WHITE));
	}

	public static class Type extends ContextualConditionType<BeeHasTrait> {
		@Override
		public BeeHasTrait fromJson(JsonObject jsonObject) {
			String s = GsonHelper.getAsString(jsonObject, "trait");
			Trait trait = Trait.REGISTRY.get(s);
			Preconditions.checkNotNull(trait, "Unknown trait: %s", s);
			return new BeeHasTrait(trait);
		}

		@Override
		public void toJson(BeeHasTrait condition, JsonObject jsonObject) {
			jsonObject.addProperty("trait", condition.trait.name());
		}

		@Override
		public BeeHasTrait fromNetwork(FriendlyByteBuf buf) {
			return new BeeHasTrait(Trait.REGISTRY.get(buf.readUtf()));
		}

		@Override
		public void toNetwork(BeeHasTrait condition, FriendlyByteBuf buf) {
			buf.writeUtf(condition.trait.name());
		}
	}
}
