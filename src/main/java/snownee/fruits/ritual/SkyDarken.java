package snownee.fruits.ritual;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import snownee.lychee.LycheeLootContextParams;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.contextual.ContextualCondition;
import snownee.lychee.core.contextual.ContextualConditionType;
import snownee.lychee.core.def.IntBoundsHelper;
import snownee.lychee.core.recipe.ILycheeRecipe;

public record SkyDarken(MinMaxBounds.Ints value, boolean requireSkyLight, boolean canSeeSky) implements ContextualCondition {
	@Override
	public ContextualConditionType<? extends ContextualCondition> getType() {
		return RitualModule.SKY_DARKEN.getOrCreate();
	}

	@Override
	public int test(ILycheeRecipe<?> recipe, LycheeContext context, int i) {
		BlockPos pos = context.getParamOrNull(LycheeLootContextParams.BLOCK_POS);
		if (pos == null) {
			pos = BlockPos.containing(context.getParam(LootContextParams.ORIGIN));
		}
		return test(context.getLevel(), pos) ? i : 0;
	}

	@Override
	public MutableComponent getDescription(boolean inverted) {
		return Component.translatable(makeDescriptionId(inverted));
	}

	private boolean test(Level level, BlockPos pos) {
		if (requireSkyLight && !level.dimensionType().hasSkyLight()) {
			return false;
		}
		if (canSeeSky && !level.canSeeSky(pos)) {
			return false;
		}
		return value.matches(level.getSkyDarken());
	}

	public static class Type extends ContextualConditionType<SkyDarken> {

		@Override
		public SkyDarken fromJson(JsonObject o) {
			return new SkyDarken(
					MinMaxBounds.Ints.fromJson(o.get("value")),
					GsonHelper.getAsBoolean(o, "require_sky_light", false),
					GsonHelper.getAsBoolean(o, "can_see_sky", false));
		}

		@Override
		public void toJson(SkyDarken skyDarken, JsonObject o) {
			o.add("value", skyDarken.value.serializeToJson());
			if (skyDarken.requireSkyLight) {
				o.addProperty("require_sky_light", true);
			}
			if (skyDarken.canSeeSky) {
				o.addProperty("can_see_sky", true);
			}
		}

		@Override
		public SkyDarken fromNetwork(FriendlyByteBuf buf) {
			return new SkyDarken(IntBoundsHelper.fromNetwork(buf), buf.readBoolean(), buf.readBoolean());
		}

		@Override
		public void toNetwork(SkyDarken skyDarken, FriendlyByteBuf buf) {
			IntBoundsHelper.toNetwork(skyDarken.value, buf);
			buf.writeBoolean(skyDarken.requireSkyLight);
			buf.writeBoolean(skyDarken.canSeeSky);
		}
	}
}
