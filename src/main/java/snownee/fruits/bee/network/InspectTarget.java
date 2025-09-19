package snownee.fruits.bee.network;


import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import snownee.fruits.Hooks;
import snownee.fruits.compat.supplementaries.SupplementariesCompat;

public interface InspectTarget {
	@Nullable
	static InspectTarget find(@Nullable Level level, @Nullable HitResult hitResult) {
		if (hitResult == null || level == null) {
			return null;
		}
		if (hitResult.getType() == HitResult.Type.ENTITY) {
			return EntityTarget.of(((EntityHitResult) hitResult).getEntity());
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			return BlockTarget.of(level, ((BlockHitResult) hitResult).getBlockPos());
		}
		return null;
	}

	@Nullable
	static InspectTarget fromNetwork(FriendlyByteBuf buf) {
		int i = buf.readVarInt();
		if (i == 0) {
			return new EntityTarget(buf.readResourceKey(Registries.DIMENSION), buf.readVarInt());
		} else if (i == 1) {
			return new BlockTarget(buf.readResourceKey(Registries.DIMENSION), buf.readBlockPos());
		}
		return null;
	}

	@Nullable Entity getEntity(Level level);

	void toNetwork(FriendlyByteBuf buf);

	boolean isFor(Level level);

	record EntityTarget(ResourceKey<Level> dimension, int id) implements InspectTarget {
		public static EntityTarget of(Entity entity) {
			return new EntityTarget(entity.level().dimension(), entity.getId());
		}

		@Override
		public @Nullable Entity getEntity(Level level) {
			return level.getEntity(id);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeVarInt(0);
			buf.writeResourceKey(dimension);
			buf.writeVarInt(id);
		}

		@Override
		public boolean isFor(Level level) {
			return level.dimension() == dimension;
		}
	}

	record BlockTarget(ResourceKey<Level> dimension, BlockPos pos) implements InspectTarget {
		public static BlockTarget of(Level level, BlockPos pos) {
			return new BlockTarget(level.dimension(), pos);
		}

		@Override
		public @Nullable Entity getEntity(Level level) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be == null) {
				return null;
			}
			if (Hooks.supplementaries) {
				return SupplementariesCompat.getTargetEntity(be);
			}
			return null;
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeVarInt(1);
			buf.writeResourceKey(dimension);
			buf.writeBlockPos(pos);
		}

		@Override
		public boolean isFor(Level level) {
			return level.dimension() == dimension && level.isLoaded(pos);
		}
	}
}
