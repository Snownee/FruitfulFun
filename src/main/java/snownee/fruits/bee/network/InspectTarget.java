package snownee.fruits.bee.network;


import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
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
			Entity entity = ((EntityHitResult) hitResult).getEntity();
			if (entity instanceof Bee bee) {
				return EntityTarget.of(bee);
			}
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
			BlockEntity be = level.getBlockEntity(pos);
			if (be != null && Hooks.supplementaries) {
				Entity entity = SupplementariesCompat.getTargetEntity(be);
				if (entity instanceof Bee) {
					return BlockTarget.of(level, pos);
				}
			}
		}
		return null;
	}

	static InspectTarget fromNetwork(FriendlyByteBuf buf) {
		int i = buf.readVarInt();
		if (i == 0) {
			return new EntityTarget(buf.readResourceKey(Registries.DIMENSION), buf.readVarInt());
		} else if (i == 1) {
			return new BlockTarget(buf.readResourceKey(Registries.DIMENSION), buf.readBlockPos());
		}
		return null;
	}

	Entity getEntity(Level level);

	void toNetwork(FriendlyByteBuf buf);

	record EntityTarget(ResourceKey<Level> dimension, int id) implements InspectTarget {
		public static EntityTarget of(Entity entity) {
			return new EntityTarget(entity.level().dimension(), entity.getId());
		}

		@Override
		public Entity getEntity(Level level) {
			if (level.dimension() != dimension) {
				return null;
			}
			return level.getEntity(id);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeVarInt(0);
			buf.writeResourceKey(dimension);
			buf.writeVarInt(id);
		}
	}

	record BlockTarget(ResourceKey<Level> dimension, BlockPos pos) implements InspectTarget {
		public static BlockTarget of(Level level, BlockPos pos) {
			return new BlockTarget(level.dimension(), pos);
		}

		@Override
		public Entity getEntity(Level level) {
			if (level.dimension() != dimension) {
				return null;
			}
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
	}
}
