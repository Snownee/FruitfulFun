package snownee.fruits.vacuum.client;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.phys.Vec3;

public interface ItemProjectileColor {
	int getColor(ItemStack itemStack, BlockAndTintGetter level, Vec3 pos);

	record Constant(int color) implements ItemProjectileColor {
		@Override
		public int getColor(ItemStack itemStack, BlockAndTintGetter level, Vec3 pos) {
			return color;
		}
	}

	record FallingBlock(net.minecraft.world.level.block.FallingBlock block) implements ItemProjectileColor {
		@Override
		public int getColor(ItemStack itemStack, BlockAndTintGetter level, Vec3 pos) {
			return block.getDustColor(block.defaultBlockState(), level, BlockPos.ZERO);
		}
	}

	Map<DyeColor, Constant> DYE_COLORS = Maps.newEnumMap(DyeColor.class);

	@Nullable
	static ItemProjectileColor ofDyeColor(@Nullable DyeColor color) {
		if (color == null) {
			return null;
		}
		return DYE_COLORS.computeIfAbsent(color, c -> new Constant(c.getFireworkColor()));
	}
}
