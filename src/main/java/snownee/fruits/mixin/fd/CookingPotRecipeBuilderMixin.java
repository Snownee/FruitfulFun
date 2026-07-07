package snownee.fruits.mixin.fd;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import snownee.fruits.FruitfulFun;
import vectorwing.farmersdelight.data.builder.CookingPotRecipeBuilder;

@Mixin(CookingPotRecipeBuilder.class)
public class CookingPotRecipeBuilderMixin {
	@WrapMethod(method = "build(Lnet/minecraft/data/recipes/RecipeOutput;Lnet/minecraft/resources/Identifier;)V")
	private void save(RecipeOutput output, Identifier id, Operation<Void> original) {
		original.call(output, FruitfulFun.id("fd/" + id.getPath()));
	}
}
