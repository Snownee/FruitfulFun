package snownee.fruits.compat.jei;

import java.util.List;
import java.util.Objects;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeHasTrait;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.client.gui.ILightingSettings;
import snownee.lychee.compat.jei.JEICompat;
import snownee.lychee.compat.jei.SideBlockIcon;
import snownee.lychee.compat.jei.category.BaseJEICategory;
import snownee.lychee.core.LycheeContext;
import snownee.lychee.core.contextual.ContextualCondition;
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

		line = JEICompat.GUI.drawableBuilder(new ResourceLocation(FruitfulFun.ID, "textures/gui/jei.png"), 12, 0, 31, 11).setTextureSize(64, 64).build();
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

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return;
		}
		bee.setLevel(mc.level);
		bee.tickCount = mc.player.tickCount;

		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(85, 24, 20);
		matrixStack.scale(20, 20, 20);

		float toRad = 0.01745329251F;
		Quaternionf quaternion = new Quaternionf().rotateXYZ(170 * toRad, 135 * toRad, 0);
		matrixStack.mulPose(quaternion);

		ILightingSettings.DEFAULT_FLAT.applyLighting();
		EntityRenderDispatcher renderDispatcher = mc.getEntityRenderDispatcher();
		quaternion.conjugate();
		renderDispatcher.overrideCameraOrientation(quaternion);
		renderDispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

		BeeAttributes attributes = BeeAttributes.of(bee);
		attributes.getTraits().clear();
		for (ContextualCondition condition : recipe.getConditions()) {
			if (!BeeModule.BEE_HAS_TRAIT.is(condition.getType())) {
				continue;
			}
			BeeHasTrait beeHasTrait = (BeeHasTrait) condition;
			attributes.getTraits().add(beeHasTrait.trait());
		}
		attributes.updateTexture();
		renderDispatcher.render(bee, 0.0D, 0.0D, 0.0D, mc.getFrameTime(), 1, matrixStack, bufferSource, 15728880);

		bufferSource.endBatch();
		renderDispatcher.setRenderShadow(true);
		matrixStack.popPose();
		bee.setLevel(null);
		ILightingSettings.DEFAULT_3D.applyLighting();
	}

	@Override
	public int getWidth() {
		return width + 50;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, HybridizingRecipe recipe, IFocusGroup focuses) {
		int xCenter = getWidth() / 2;
		int y = recipe.getIngredients().size() > 9 || recipe.showingActionsCount() > 9 ? 26 : 28;
		ingredientGroup(builder, recipe, xCenter - 45, y);
		actionGroup(builder, recipe, xCenter + 50, y);
		addBlockIngredients(builder, recipe);
		recipe.addInvisibleIngredients(
				builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)::addItemStack,
				builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)::addItemStack);
	}
}
