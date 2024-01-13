package snownee.fruits.mixin.forge;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

@Mixin(ItemColors.class)
public interface ItemColorsAccess {
	@Accessor(remap = false)
	Map<Holder.Reference<Item>, ItemColor> getItemColors();
}
