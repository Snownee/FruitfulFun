package snownee.fruits.plugin.jei;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import snownee.fruits.Fruits;
import snownee.fruits.Fruits.Type;
import snownee.fruits.MainModule;
import snownee.fruits.hybridization.HybridingRecipe;

public class HybridingCategory implements IRecipeCategory<HybridingRecipe> {

    private final String localizedName;
    private final IDrawable icon;
    private final IDrawable background;
    private final IGuiHelper guiHelper;
    private final BeeEntity bee;
    private final IDrawable x;
    private final IDrawable line;

    public static final int width = 116;
    public static final int height = 54;

    public HybridingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        localizedName = I18n.format("gui.fruittrees.jei.category.hybriding");
        icon = guiHelper.createDrawableIngredient(MainModule.GRAPEFRUIT.getDefaultInstance());
        background = guiHelper.createBlankDrawable(width, height);
        float f = (float) Math.atan(1000 / 40.0F);
        float f1 = (float) Math.atan(-10 / 40.0F);
        bee = EntityType.BEE.create(Minecraft.getInstance().world);
        bee.renderYawOffset = 180.0F + f * 20.0F;
        bee.rotationYaw = 180.0F + f * 40.0F;
        bee.rotationPitch = -f1 * 20.0F;
        bee.rotationYawHead = bee.rotationYaw;
        bee.prevRotationYawHead = bee.rotationYaw;
        Minecraft.getInstance().getRenderManager();
        x = guiHelper.drawableBuilder(new ResourceLocation(Fruits.MODID, "textures/gui/jei.png"), 0, 0, 10, 11).setTextureSize(64, 64).build();
        line = guiHelper.drawableBuilder(new ResourceLocation(Fruits.MODID, "textures/gui/jei.png"), 12, 4, 31, 3).setTextureSize(64, 64).build();
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
    public String getTitle() {
        return localizedName;
    }

    @Override
    public void draw(HybridingRecipe recipe, double mouseX, double mouseY) {
        float f1 = (float) Math.atan(-10 / 40.0F);
        x.draw(18, 22);
        line.draw(54, 26);

        RenderSystem.pushMatrix();

        RenderSystem.translatef(70, 24, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);

        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale(20, 20, 20);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);

        Minecraft mc = Minecraft.getInstance();
        EntityRendererManager entityrenderermanager = mc.getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getRenderTypeBuffers().getBufferSource();

        bee.ticksExisted = mc.player.ticksExisted;
        entityrenderermanager.renderEntityStatic(bee, 0.0D, 0.0D, 0.0D, mc.getRenderPartialTicks(), 1, matrixstack, irendertypebuffer$impl, 15728880);

        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);

        RenderSystem.popMatrix();
    }

    @Override
    public ResourceLocation getUid() {
        return JEIPlugin.UID;
    }

    @Override
    public void setIngredients(HybridingRecipe recipe, IIngredients ingredients) {
        Either<Type, Block> result = recipe.getResult(recipe.ingredients);
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

    public static ItemStack asItem(Either<Fruits.Type, Block> either) {
        Optional<Fruits.Type> left = either.left();
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
                if (stack.getItem().isIn(ItemTags.LEAVES)) {
                    String line = I18n.format("gui.fruittrees.jei.tip.flowering", tooltip.get(0));
                    tooltip.set(0, line);
                }
            } else {
                boolean showAdvanced = Minecraft.getInstance().gameSettings.advancedItemTooltips || Screen.hasShiftDown();
                if (showAdvanced) {
                    tooltip.add(TextFormatting.DARK_GRAY + I18n.format("jei.tooltip.recipe.id", recipe.getId()));
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
