package snownee.fruits.mixin.lychee;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.Hooks;
import snownee.fruits.compat.lychee.DragonRitualCategory;
import snownee.fruits.compat.lychee.HybridizingRecipeCategory;
import snownee.fruits.food.FoodModule;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.compat.recipeviewer.RvPlugin;
import snownee.lychee.compat.recipeviewer.category.RvCategory;
import snownee.lychee.compat.recipeviewer.element.SideBlockIcon;
import snownee.lychee.util.context.LycheeContext;
import snownee.lychee.util.recipe.ILycheeRecipe;

@Mixin(RvPlugin.class)
public abstract class RvPluginMixin {
	@Shadow
	public abstract <R extends ILycheeRecipe<LycheeContext>, T extends RvCategory<R>> void register(T category, Consumer<T> configurer);

	@Inject(
			method = "init",
			at = @At(
					value = "INVOKE",
					target = "Lsnownee/lychee/compat/recipeviewer/RvPlugin;register(Lsnownee/lychee/compat/recipeviewer/category/RvCategory;Ljava/util/function/Consumer;)V",
					ordinal = 0))
	private void init(RecipeMap recipeMap, CallbackInfo ci) {
		if (Hooks.bee) {
			register(
					new HybridizingRecipeCategory(), it -> {
						it.width = 170;
						it.iconProvider = _ -> new SideBlockIcon(
								GuiGameElement.of(CoreModule.GRAPEFRUIT),
								Blocks.BEEHIVE::defaultBlockState);
					});
		}
		if (Hooks.ritual) {
			register(
					new DragonRitualCategory(), it -> {
						it.iconProvider = _ -> new SideBlockIcon(
								GuiGameElement.of(Items.DRAGON_HEAD),
								FoodModule.CHORUS_FRUIT_PIE::defaultBlockState);
						it.setSimpleWorkstationProvider(_ -> List.of(Items.DRAGON_HEAD, FoodModule.CHORUS_FRUIT_PIE.asItem()));
					});
		}
	}
}
