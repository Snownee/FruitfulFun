package snownee.fruits.compat.lychee;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeHolder;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.BeeVariant;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.fruits.bee.genetics.BeeHasTrait;
import snownee.fruits.bee.genetics.BeeIsVariant;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.util.ClientProxy;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.client.gui.RenderElement;
import snownee.lychee.compat.recipeviewer.category.DecorationMapBuilder;
import snownee.lychee.compat.recipeviewer.category.RvCategory;
import snownee.lychee.compat.recipeviewer.category.RvCategoryLayoutBuilder;
import snownee.lychee.ui.SpriteElementRenderer;
import snownee.lychee.util.contextual.ContextualCondition;

public class HybridizingRecipeCategory extends RvCategory<HybridizingRecipe> {
	public static final Vector2fc INFO_POSITION = new Vector2f(80, 38);

	public HybridizingRecipeCategory() {
		super(BeeModule.RECIPE_TYPE.get());
	}

	@Override
	public Vector2fc infoPosition(HybridizingRecipe recipe) {
		return INFO_POSITION;
	}

	@Override
	public void setupDecorations(DecorationMapBuilder<HybridizingRecipe> mapBuilder) {
		mapBuilder.info(this::infoPosition);
		mapBuilder.put(
				"bee", (builder, recipeHolder) -> {
					List<ContextualCondition> conditions = recipeHolder.value().conditions().conditions();
					ResourceKey<BeeVariant> variant = conditions.stream()
							.filter(BeeIsVariant.class::isInstance)
							.map(BeeIsVariant.class::cast)
							.map(BeeIsVariant::variant)
							.findFirst()
							.orElse(null);
					if (variant == null) {
						variant = conditions
								.stream()
								.filter(BeeHasTrait.class::isInstance)
								.map(BeeHasTrait.class::cast)
								.flatMap($ -> $.traits().stream())
								.map(Trait::variant)
								.filter(Optional::isPresent)
								.map(Optional::get)
								.findFirst()
								.orElse(null);
					}
					BeeRenderState bee = new BeeRenderState();
					bee.entityType = EntityType.BEE;
					if (variant != null) {
						bee.setData(
								ClientProxy.BEE_VARIANT,
								Objects.requireNonNull(Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess())
										.getOrThrow(variant));
					}
					builder.addElement(RenderElement.create(
							GuiGameElement.of(bee)
									.scale(20)
									.rotate(170, 135, 0)
									.atLocal(0.0F, -0.5F, 1.0F)
									.withSize(width, height), _ -> {
								bee.ageInTicks = Objects.requireNonNull(Minecraft.getInstance().level).getGameTime() +
										Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(
												true);
							}));
				});
		mapBuilder.put(
				"arrow", (builder, _) -> {
					builder.addElement(new SpriteElementRenderer(FruitfulFun.id("recipe_arrow"))
							.at(width / 2F - 15, 24)
							.withSize(31, 11));
				});
	}

	@Override
	public void configureLayout(RvCategoryLayoutBuilder<HybridizingRecipe> builder, RecipeHolder<HybridizingRecipe> recipeHolder) {
		super.configureLayout(builder, recipeHolder);
	}

//	@Override
//	public List<Widget> setupDisplay(HybridizingDisplay display, Rectangle bounds) {
//		Point startPoint = new Point(bounds.getCenterX() - this.getRealWidth() / 2, bounds.getY() + 4);
//		HybridizingRecipe recipe = display.recipe;
//		List<Widget> widgets = super.setupDisplay(display, bounds);
//		this.drawInfoBadge(widgets, display, startPoint);
//		int xCenter = bounds.getCenterX();
//		List<LycheeCompat.Input> inputs = LycheeCompat.getInputs(recipe);
//		int y = inputs.size() <= 9 && recipe.showingActionsCount() <= 9 ? 28 : 26;
//		this.ingredientGroup(widgets, startPoint, inputs, xCenter - 45 - startPoint.x, y);
//		this.actionGroup(widgets, startPoint, recipe, xCenter + 50 - startPoint.x, y);
//		this.drawExtra(widgets, display, bounds);
//		return widgets;
//	}
//
//	public void ingredientGroup(List<Widget> widgets, Point startPoint, List<LycheeCompat.Input> inputs, int x, int y) {
//		slotGroup(
//				widgets, startPoint, x, y, inputs, (widgets0, startPoint0, input, x0, y0) -> {
//					LEntryWidget slot = REICompat.slot(startPoint, x0, y0, REICompat.SlotType.NORMAL);
//					if (input.isItem()) {
//						slot.entries(EntryIngredients.ofIngredient(Objects.requireNonNull(input.itemIngredient)));
//					} else {
//						slot.entry(EntryStack.of(REICompat.POST_ACTION, new DummyBlockInput(input.block)));
//					}
//					slot.markInput();
//					widgets.add(slot);
//				});
//	}
//
//	public void drawExtra(List<Widget> widgets, HybridizingDisplay display, Rectangle bounds) {
//		widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
//			graphics.pose().pushPose();
//			graphics.pose().translate(bounds.x, bounds.y + 4, 20);
//			LycheeCompat.renderBee(graphics, display.recipe, bee);
//			graphics.pose().popPose();
//		}));
//		widgets.add(Widgets.createTexturedWidget(
//				FruitfulFun.id("textures/gui/jei.png"),
//				bounds.x + 68, bounds.y + 28, 12, 0, 31, 11, 64, 64));
//	}

}
