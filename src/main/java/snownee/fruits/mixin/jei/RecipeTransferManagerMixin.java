package snownee.fruits.mixin.jei;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.library.recipes.RecipeTransferManager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import snownee.fruits.gadget.GadgetModule;

@Mixin(RecipeTransferManager.class)
public class RecipeTransferManagerMixin {
	@WrapMethod(method = "getHandler", require = 0)
	private Optional<IRecipeTransferHandler<?, ?>> fruits$useVanillaBrewingStandMenuType(
			Class<? extends AbstractContainerMenu> containerClass,
			MenuType<AbstractContainerMenu> menuType,
			IRecipeType<?> recipeType,
			Operation<Optional<IRecipeTransferHandler<?, ?>>> original) {
		if ((Object) menuType == GadgetModule.BREWER_MENU.getOrCreate()) {
			return original.call(containerClass, MenuType.BREWING_STAND, recipeType);
		}
		return original.call(containerClass, menuType, recipeType);
	}
}
