package snownee.fruits.compat.rei;

import java.util.List;
import java.util.Objects;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.fruits.compat.DummyBlockInput;
import snownee.fruits.compat.FFJEIREI;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.compat.rei.LEntryWidget;
import snownee.lychee.compat.rei.REICompat;
import snownee.lychee.compat.rei.SideBlockIcon;
import snownee.lychee.compat.rei.category.BaseREICategory;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.recipe.type.LycheeRecipeType;

public class HybridizingCategory extends BaseREICategory<LycheeContext, HybridizingRecipe, HybridizingDisplay> {
	private final Bee bee;

	public HybridizingCategory(LycheeRecipeType<LycheeContext, HybridizingRecipe> recipeType) {
		super(recipeType);
		bee = EntityType.BEE.create(Minecraft.getInstance().level);
		Objects.requireNonNull(bee);
		bee.setLevel(null);
		infoRect = new Rect2i(80, 38, 8, 8);
	}

	@Override
	public Renderer createIcon(List<HybridizingRecipe> recipes) {
		GuiGameElement.GuiRenderBuilder mainIcon = GuiGameElement.of(CoreModule.GRAPEFRUIT.get());
		return new REICompat.ScreenElementWrapper(new SideBlockIcon(mainIcon, Blocks.BEEHIVE::defaultBlockState));
	}

	@Override
	public int getDisplayWidth(HybridizingDisplay display) {
		return this.getRealWidth();
	}

	@Override
	public int getRealWidth() {
		return 170;
	}

	@Override
	public List<Widget> setupDisplay(HybridizingDisplay display, Rectangle bounds) {
		Point startPoint = new Point(bounds.getCenterX() - this.getRealWidth() / 2, bounds.getY() + 4);
		HybridizingRecipe recipe = display.recipe;
		List<Widget> widgets = super.setupDisplay(display, bounds);
		this.drawInfoBadge(widgets, display, startPoint);
		int xCenter = bounds.getCenterX();
		List<FFJEIREI.Input> inputs = FFJEIREI.getInputs(recipe);
		int y = inputs.size() <= 9 && recipe.showingActionsCount() <= 9 ? 28 : 26;
		this.ingredientGroup(widgets, startPoint, inputs, xCenter - 45 - startPoint.x, y);
		this.actionGroup(widgets, startPoint, recipe, xCenter + 50 - startPoint.x, y);
		this.drawExtra(widgets, display, bounds);
		return widgets;
	}

	public void ingredientGroup(List<Widget> widgets, Point startPoint, List<FFJEIREI.Input> inputs, int x, int y) {
		slotGroup(widgets, startPoint, x, y, inputs, (widgets0, startPoint0, input, x0, y0) -> {
			LEntryWidget slot = REICompat.slot(startPoint, x0, y0, REICompat.SlotType.NORMAL);
			if (input.isItem()) {
				slot.entries(EntryIngredients.ofIngredient(Objects.requireNonNull(input.itemIngredient)));
			} else {
				slot.entry(EntryStack.of(REICompat.POST_ACTION, new DummyBlockInput(input.block)));
			}
			slot.markInput();
			widgets.add(slot);
		});
	}

	public void drawExtra(List<Widget> widgets, HybridizingDisplay display, Rectangle bounds) {
		widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
			graphics.pose().pushPose();
			graphics.pose().translate(bounds.x, bounds.y + 4, 20);
			FFJEIREI.renderBee(graphics, display.recipe, bee);
			graphics.pose().popPose();
		}));
		widgets.add(Widgets.createTexturedWidget(new ResourceLocation(FruitfulFun.ID, "textures/gui/jei.png"),
				bounds.x + 68, bounds.y + 28, 12, 0, 31, 11, 64, 64));
	}

}
