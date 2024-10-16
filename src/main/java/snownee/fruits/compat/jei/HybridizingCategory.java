package snownee.fruits.compat.jei;

import java.util.List;
import java.util.Objects;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.fruits.compat.DummyBlockInput;
import snownee.fruits.compat.FFJEIREI;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.compat.jei.JEICompat;
import snownee.lychee.compat.jei.SideBlockIcon;
import snownee.lychee.compat.jei.category.BaseJEICategory;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.recipe.type.LycheeRecipeType;

public class HybridizingCategory extends BaseJEICategory<LycheeContext, HybridizingRecipe> {
	private final Bee bee;
	private final IDrawable line;

	public HybridizingCategory(LycheeRecipeType<LycheeContext, HybridizingRecipe> recipeType) {
		super(recipeType);
		bee = EntityType.BEE.create(Minecraft.getInstance().level);
		Objects.requireNonNull(bee);
		bee.setLevel(null);
		infoRect = new Rect2i(80, 38, 8, 8);

		line = JEICompat.GUI.drawableBuilder(FruitfulFun.id("textures/gui/jei.png"), 12, 0, 31, 11)
				.setTextureSize(64, 64)
				.build();
	}

	@Override
	public IDrawable createIcon(IGuiHelper guiHelper, List<HybridizingRecipe> recipes) {
		GuiGameElement.GuiRenderBuilder mainIcon = GuiGameElement.of(CoreModule.GRAPEFRUIT.get());
		return new JEICompat.ScreenElementWrapper(new SideBlockIcon(mainIcon, Blocks.BEEHIVE::defaultBlockState));
	}

	@Override
	public void draw(HybridizingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		drawInfoBadge(recipe, graphics, mouseX, mouseY);
		line.draw(graphics, 68, 24);
		FFJEIREI.renderBee(graphics, recipe, bee);
	}

	@Override
	public int getWidth() {
		return width + 50;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, HybridizingRecipe recipe, IFocusGroup focuses) {
		int xCenter = getWidth() / 2;
		List<FFJEIREI.Input> inputs = FFJEIREI.getInputs(recipe);
		int y = inputs.size() > 9 || recipe.showingActionsCount() > 9 ? 26 : 28;
		ingredientGroup(builder, inputs, xCenter - 45, y);
		actionGroup(builder, recipe, xCenter + 50, y);
		addBlockIngredients(builder, recipe);
		recipe.addInvisibleInputs(builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)::addItemStack);
		recipe.addInvisibleOutputs(builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)::addItemStack);
	}

	public void ingredientGroup(IRecipeLayoutBuilder builder, List<FFJEIREI.Input> inputs, int x, int y) {
		slotGroup(builder, x + 1, y + 1, 0, inputs, (layout0, input, i, x0, y0) -> {
			IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x0, y0);
			if (input.isItem()) {
				slot.addIngredients(Objects.requireNonNull(input.itemIngredient));
			} else {
				slot.addIngredient(JEICompat.POST_ACTION, new DummyBlockInput(input.block));
			}
			slot.setBackground(JEICompat.slot(JEICompat.SlotType.NORMAL), -1, -1);
		});
	}
}
