package snownee.fruits.vacuum.client;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.GravelBlock;
import net.minecraft.world.level.block.SandBlock;
import snownee.fruits.util.ClientProxy;

public class ItemProjectileColors {
	private static final Map<Item, ItemProjectileColor> COLORS = Maps.newIdentityHashMap();
	private static final DyeColor[] DYE_COLOR_ORDER;

	static {
		DyeColor[] values = DyeColor.values();
		List<DyeColor> list = Lists.newArrayListWithExpectedSize(values.length);
		list.add(DyeColor.LIGHT_GRAY);
		list.add(DyeColor.LIGHT_BLUE);
		for (DyeColor color : values) {
			if (color != DyeColor.LIGHT_GRAY && color != DyeColor.LIGHT_BLUE) {
				list.add(color);
			}
		}
		DYE_COLOR_ORDER = list.toArray(DyeColor[]::new);
	}

	public static void register(Item item, ItemProjectileColor color) {
		COLORS.put(item, color);
	}

	public static ItemProjectileColor get(ItemStack itemStack) {
		ItemProjectileColor color = COLORS.get(itemStack.getItem());
		if (color == null) {
			guessColor(itemStack);
		}
		return color;
	}

	private static void guessColor(ItemStack itemStack) {
		Item item = itemStack.getItem();
		ItemProjectileColor color = ClientProxy.getItemProjectileColor(itemStack);
		if (color != null && !itemStack.hasTag()) {
			register(item, color);
			return;
		}
		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof FallingBlock block) {
			if (block instanceof SandBlock || block instanceof GravelBlock || block instanceof ConcretePowderBlock) {
				register(item, new ItemProjectileColor.FallingBlock(block));
				return;
			}
		}
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
		String palette = null;
		for (DyeColor dyeColor : DYE_COLOR_ORDER) {
			if (id.getPath().contains(dyeColor.getName())) {
				palette = id.getPath().replace(dyeColor.getName(), "%s");
				break;
			}
		}
		if (palette == null) {
			return;
		}
		List<Pair<Item, DyeColor>> list = Lists.newArrayListWithExpectedSize(DYE_COLOR_ORDER.length);
		for (DyeColor dyeColor : DYE_COLOR_ORDER) {
			id = id.withPath(String.format(palette, dyeColor.getName()));
			item = BuiltInRegistries.ITEM.get(id);
			if (item == Items.AIR) {
				return;
			}
			list.add(Pair.of(item, dyeColor));
		}
		for (Pair<Item, DyeColor> pair : list) {
			register(pair.getFirst(), ItemProjectileColor.ofDyeColor(pair.getSecond()));
		}
	}

	public static void invalidate() {
		COLORS.clear();
	}
}
