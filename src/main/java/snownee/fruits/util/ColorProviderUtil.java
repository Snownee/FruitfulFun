package snownee.fruits.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.mixin.forge.BlockColorsAccess;
import snownee.fruits.mixin.forge.ItemColorsAccess;

public class ColorProviderUtil {
	public static Supplier<BlockColor> delegate(Block block) {
		return new CachedSupplier<>(() -> {
			BlockColorsAccess blockColors = (BlockColorsAccess) Minecraft.getInstance().getBlockColors();
			return blockColors.getBlockColors().get(ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
		}, Dummy.INSTANCE);
	}

	public static Supplier<ItemColor> delegate(Item item) {
		return new CachedSupplier<>(() -> {
			ItemColorsAccess itemColors = (ItemColorsAccess) Minecraft.getInstance().getItemColors();
			return itemColors.getItemColors().get(ForgeRegistries.ITEMS.getDelegateOrThrow(item));
		}, Dummy.INSTANCE);
	}

	public static class Dummy implements ItemColor, BlockColor {
		public static final Dummy INSTANCE = new Dummy();

		@Override
		public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i) {
			return -1;
		}

		@Override
		public int getColor(ItemStack itemStack, int i) {
			return -1;
		}
	}
}
