package snownee.fruits.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.common.collect.BiMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

@Mixin(value = CraftingHelper.class, remap = false)
public interface CraftingHelperAccess {
	@Accessor
	static BiMap<ResourceLocation, IIngredientSerializer<?>> getIngredients() {
		throw new AssertionError();
	}
}
