package snownee.fruits.plugin.jei;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitType;
import snownee.fruits.FruitsMod;
import snownee.fruits.hybridization.HybridingRecipe;

public class HybridingCategory implements IRecipeCategory<HybridingRecipe> {

	private final TranslatableComponent localizedName;
	private final IDrawable icon;
	private final IDrawable background;
	private final IGuiHelper guiHelper;
	private final Bee bee;
	private final IDrawable x;
	private final IDrawable line;

	public static final int width = 116;
	public static final int height = 54;

	//InventoryScreen.renderEntityInInventory
	public HybridingCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
		localizedName = new TranslatableComponent("gui.fruittrees.jei.category.hybriding");
		icon = guiHelper.createDrawableIngredient(CoreModule.GRAPEFRUIT.getDefaultInstance());
		background = guiHelper.createBlankDrawable(width, height);
		float f = (float) Math.atan(1000 / 40.0F);
		float f1 = (float) Math.atan(-10 / 40.0F);
		bee = EntityType.BEE.create(Minecraft.getInstance().level);
		bee.yBodyRot = 180.0F + f * 20.0F;
		bee.setYRot(180.0F + f * 40.0F);
		bee.setXRot(-f1 * 20.0F);
		bee.yHeadRot = bee.getYRot();
		bee.yHeadRotO = bee.getYRot();
		x = guiHelper.drawableBuilder(new ResourceLocation(FruitsMod.MODID, "textures/gui/jei.png"), 0, 0, 10, 11).setTextureSize(64, 64).build();
		line = guiHelper.drawableBuilder(new ResourceLocation(FruitsMod.MODID, "textures/gui/jei.png"), 12, 4, 31, 3).setTextureSize(64, 64).build();
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
	public void draw(HybridingRecipe recipe, PoseStack matrix, double mouseX, double mouseY) {
		x.draw(matrix, 18, 22);
		line.draw(matrix, 54, 26);

		matrix.pushPose();

		matrix.translate(70, 24, 0);
		matrix.scale(20, 20, -20);

		float f1 = (float) Math.atan(-10 / 40.0F);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
		quaternion.mul(quaternion1);
		matrix.mulPose(quaternion);

		//		Lighting.setupForEntityInInventory();
		Minecraft mc = Minecraft.getInstance();
		EntityRenderDispatcher entityrenderermanager = mc.getEntityRenderDispatcher();
		quaternion1.conj();
		entityrenderermanager.overrideCameraOrientation(quaternion1);
		entityrenderermanager.setRenderShadow(false);
		BufferSource irendertypebuffer$impl = mc.renderBuffers().bufferSource();

		bee.tickCount = mc.player.tickCount;
		entityrenderermanager.render(bee, 0.0D, 0.0D, 0.0D, mc.getFrameTime(), 1, matrix, irendertypebuffer$impl, 15728880);

		irendertypebuffer$impl.endBatch();
		entityrenderermanager.setRenderShadow(true);
		//		Lighting.setupFor3DItems();

		matrix.popPose();
	}

	@Override
	public ResourceLocation getUid() {
		return JEIPlugin.UID;
	}

	@Override
	public void setIngredients(HybridingRecipe recipe, IIngredients ingredients) {
		Either<FruitType, Block> result = recipe.getResult(recipe.ingredients);
		ImmutableList.Builder<ItemStack> outputs = ImmutableList.builder();
		result.ifLeft(t -> {
			outputs.add(t.sapling.get().asItem().getDefaultInstance(), t.fruit.asItem().getDefaultInstance());
		}).ifRight(b -> {
			outputs.add(b.asItem().getDefaultInstance());
		});
		ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(outputs.build()));
		List<ItemStack> inputs = recipe.ingredients.stream().map(HybridingCategory::asItem).collect(Collectors.toList());
		ingredients.setInputs(VanillaTypes.ITEM, inputs);
	}

	public static ItemStack asItem(Either<FruitType, Block> either) {
		Optional<FruitType> left = either.left();
		if (left.isPresent()) {
			return left.get().leaves.asItem().getDefaultInstance();
		} else {
			return either.right().get().asItem().getDefaultInstance();
		}
	}

	@Override
	public void setRecipe(IRecipeLayout layout, HybridingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
		guiItemStacks.init(0, false, 94, 18);
		guiItemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
		guiItemStacks.setBackground(0, guiHelper.getSlotDrawable());
		guiItemStacks.addTooltipCallback((slot, input, stack, tooltip) -> {
			if (tooltip.isEmpty()) {
				return;
			}
			if (input) {
				if (stack.is(ItemTags.LEAVES)) {
					Component line = new TranslatableComponent("gui.fruittrees.jei.tip.flowering", tooltip.get(0));
					tooltip.set(0, line);
				}
			}
		});
		int size = recipe.ingredients.size();
		int x = 0;
		int y = size > 2 ? 5 : 18;
		for (int i = 0; i < size; ++i) {
			guiItemStacks.init(1 + i, true, x, y);
			guiItemStacks.set(1 + i, ingredients.getInputs(VanillaTypes.ITEM).get(i));
			guiItemStacks.setBackground(1 + i, guiHelper.getSlotDrawable());
			if (i == 1) {
				x = 0;
				y += 28;
			} else {
				x += 28;
			}
		}
		if (size == 3) {
			guiItemStacks.init(4, true, 28, y);
			guiItemStacks.set(4, (ItemStack) null);
			guiItemStacks.setBackground(4, guiHelper.getSlotDrawable());
		}
	}

	@Override
	public Class<? extends HybridingRecipe> getRecipeClass() {
		return HybridingRecipe.class;
	}

}
