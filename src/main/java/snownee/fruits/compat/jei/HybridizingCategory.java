package snownee.fruits.compat.jei;

import java.util.List;

import org.joml.Quaternionf;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Axis;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.fruits.FruitfulFun;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.HybridizingRecipe;

public class HybridizingCategory implements IRecipeCategory<HybridizingRecipe> {

	private final Component localizedName;
	private final IDrawable icon;
	private final IDrawable background;
	private final IGuiHelper guiHelper;
	private final Bee bee;
	private final IDrawable x;
	private final IDrawable line;

	public static final int width = 116;
	public static final int height = 54;

	//InventoryScreen.renderEntityInInventory
	public HybridizingCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
		localizedName = Component.translatable("gui.fruitfulfun.hybridizing");
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, CoreModule.GRAPEFRUIT.itemStack());
		background = guiHelper.createBlankDrawable(width, height);
		float f = (float) Math.atan(1000 / 40.0F);
		float f1 = (float) Math.atan(-10 / 40.0F);
		bee = EntityType.BEE.create(Minecraft.getInstance().level);
		bee.yBodyRot = 180.0F + f * 20.0F;
		bee.setYRot(180.0F + f * 40.0F);
		bee.setXRot(-f1 * 20.0F);
		bee.yHeadRot = bee.getYRot();
		bee.yHeadRotO = bee.getYRot();
		x = guiHelper.drawableBuilder(new ResourceLocation(FruitfulFun.ID, "textures/gui/jei.png"), 0, 0, 10, 11).setTextureSize(64, 64).build();
		line = guiHelper.drawableBuilder(new ResourceLocation(FruitfulFun.ID, "textures/gui/jei.png"), 12, 4, 31, 3).setTextureSize(64, 64).build();
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public Component getTitle() {
		return localizedName;
	}

	@Override
	public void draw(HybridizingRecipe recipe, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY) {
		x.draw(graphics, 18, 22);
		line.draw(graphics, 54, 26);

		graphics.pose().pushPose();

		graphics.pose().translate(70, 24, 100);
		graphics.pose().scale(20, 20, -20);

		float f1 = (float) Math.atan(-10 / 40.0F);
		Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
		Quaternionf quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
		quaternion.mul(quaternion1);
		graphics.pose().mulPose(quaternion);

		//		Lighting.setupForEntityInInventory();
		Minecraft mc = Minecraft.getInstance();
		EntityRenderDispatcher entityrenderermanager = mc.getEntityRenderDispatcher();
		quaternion1.conjugate();
		entityrenderermanager.overrideCameraOrientation(quaternion1);
		entityrenderermanager.setRenderShadow(false);
		BufferSource irendertypebuffer$impl = mc.renderBuffers().bufferSource();

		bee.tickCount = mc.player.tickCount;
		entityrenderermanager.render(bee, 0.0D, 0.0D, 0.0D, mc.getFrameTime(), 1, graphics.pose(), irendertypebuffer$impl, 15728880);

		irendertypebuffer$impl.endBatch();
		entityrenderermanager.setRenderShadow(true);
		//		Lighting.setupFor3DItems();

		graphics.pose().popPose();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, HybridizingRecipe recipe, IFocusGroup focuses) {
		Block result = recipe.getResult(recipe.ingredients);
		ImmutableList.Builder<ItemStack> outputs = ImmutableList.builder();
		if (result instanceof FruitLeavesBlock leavesBlock) {
			FruitType type = leavesBlock.type.get();
			outputs.add(type.sapling.get().asItem().getDefaultInstance(), type.fruit.get().getDefaultInstance());
		} else {
			outputs.add(result.asItem().getDefaultInstance());
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19).addItemStacks(outputs.build()).setBackground(guiHelper.getSlotDrawable(), -1, -1);

		List<ItemStack> inputs = recipe.ingredients.stream().map(FruitType::getFruitOrDefault).map(Item::getDefaultInstance).toList();
		int size = recipe.ingredients.size();
		int x = 1;
		int y = size > 2 ? 6 : 19;
		for (int i = 0; i < size; ++i) {
			ItemStack input = inputs.get(i);
			IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStack(input).setBackground(guiHelper.getSlotDrawable(), -1, -1);
			if (input.is(ItemTags.LEAVES)) {
				slot.addTooltipCallback((IRecipeSlotView view, List<Component> tooltip) -> {
					Component line = Component.translatable("gui.fruitfulfun.tip.flowering", tooltip.get(0));
					tooltip.set(0, line);
				});
			}
			if (i == 1) {
				x = 1;
				y += 28;
			} else {
				x += 28;
			}
		}
		if (size == 3) {
			builder.addSlot(RecipeIngredientRole.INPUT, 28, y);
		}
	}

	@Override
	public RecipeType<HybridizingRecipe> getRecipeType() {
		return JEICompat.RECIPE_TYPE;
	}

}
