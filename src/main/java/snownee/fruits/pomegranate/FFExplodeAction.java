package snownee.fruits.pomegranate;

import java.util.Locale;

import com.google.gson.JsonObject;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFDamageTypes;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.post.Explode;
import snownee.lychee.core.post.PostActionType;
import snownee.lychee.core.recipe.ILycheeRecipe;
import snownee.lychee.util.CommonProxy;

public class FFExplodeAction extends Explode {
	public FFExplodeAction(Explosion.BlockInteraction blockInteraction, BlockPos offset, boolean fire, float radius, float step) {
		super(blockInteraction, offset, fire, radius, step);
	}

	@Override
	public PostActionType<?> getType() {
		return PomegranateModule.EXPLODE.get();
	}

	@Override
	protected void apply(ILycheeRecipe<?> recipe, LycheeContext ctx, int times) {
		Vec3 pos = ctx.getParam(LootContextParams.ORIGIN);
		pos = pos.add(this.offset.getX(), this.offset.getY(), this.offset.getZ());
		float r = Math.min(this.radius + this.step * (Mth.sqrt((float) times) - 1.0F), this.radius * 4.0F);
		Entity entity = ctx.getParamOrNull(LootContextParams.THIS_ENTITY);
		Entity causingEntity = null;
		if (entity instanceof TraceableEntity traceable) {
			causingEntity = traceable.getOwner();
		}
		DamageSource damageSource = FFDamageTypes.explosion(ctx.getLevel().damageSources(), entity, causingEntity);
		CommonProxy.explode(this, ctx.getServerLevel(), pos, entity, damageSource, null, r);
	}

	public static class Type extends PostActionType<FFExplodeAction> {

		@Override
		public FFExplodeAction fromJson(JsonObject o) {
			BlockPos offset = CommonProxy.parseOffset(o);
			boolean fire = GsonHelper.getAsBoolean(o, "fire", false);
			String s = GsonHelper.getAsString(o, "block_interaction", "destroy");
			Explosion.BlockInteraction blockInteraction = switch (s) {
				case "none", "keep" -> Explosion.BlockInteraction.KEEP;
				case "break", "destroy_with_decay" -> Explosion.BlockInteraction.DESTROY_WITH_DECAY;
				case "destroy" -> Explosion.BlockInteraction.DESTROY;
				default -> throw new IllegalArgumentException("Unexpected value: " + s);
			};
			float radius = GsonHelper.getAsFloat(o, "radius", 4.0F);
			float radiusStep = GsonHelper.getAsFloat(o, "radius_step", 0.5F);
			return new FFExplodeAction(blockInteraction, offset, fire, radius, radiusStep);
		}

		@Override
		public void toJson(FFExplodeAction action, JsonObject o) {
			BlockPos offset = action.offset;
			if (offset.getX() != 0) {
				o.addProperty("offsetX", offset.getX());
			}

			if (offset.getY() != 0) {
				o.addProperty("offsetY", offset.getY());
			}

			if (offset.getZ() != 0) {
				o.addProperty("offsetZ", offset.getX());
			}

			if (action.fire) {
				o.addProperty("fire", true);
			}

			if (action.blockInteraction != Explosion.BlockInteraction.DESTROY) {
				o.addProperty("block_interaction", action.blockInteraction.name().toLowerCase(Locale.ENGLISH));
			}

			if (action.radius != 4.0F) {
				o.addProperty("radius", action.radius);
			}

			if (action.step != 0.5F) {
				o.addProperty("radius_step", action.step);
			}

		}

		@Override
		public FFExplodeAction fromNetwork(FriendlyByteBuf buf) {
			Explosion.BlockInteraction blockInteraction = buf.readEnum(Explosion.BlockInteraction.class);
			BlockPos offset = buf.readBlockPos();
			boolean fire = buf.readBoolean();
			float radius = buf.readFloat();
			float step = buf.readFloat();
			return new FFExplodeAction(blockInteraction, offset, fire, radius, step);
		}

		@Override
		public void toNetwork(FFExplodeAction action, FriendlyByteBuf buf) {
			buf.writeEnum(action.blockInteraction);
			buf.writeBlockPos(action.offset);
			buf.writeBoolean(action.fire);
			buf.writeFloat(action.radius);
			buf.writeFloat(action.step);
		}
	}
}
