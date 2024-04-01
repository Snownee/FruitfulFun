package snownee.fruits.compat;

import java.util.List;
import java.util.function.BiConsumer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeHasTrait;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.lychee.client.gui.ILightingSettings;
import snownee.lychee.core.contextual.ContextualCondition;

public class FFJEIREI {

	public static void renderBee(GuiGraphics graphics, HybridizingRecipe recipe, Bee bee) {
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

	public static void addInformation(BiConsumer<List<ItemStack>, Component> registrar) {
		if (FFCommonConfig.appleSaplingFromHeroOfTheVillage || FFCommonConfig.villageAppleTreeWorldGen) {
			String info = "";
			if (FFCommonConfig.appleSaplingFromHeroOfTheVillage) {
				info = I18n.get("gui.fruitfulfun.tip.appleSaplingFromHeroOfTheVillage");
			}
			if (FFCommonConfig.villageAppleTreeWorldGen) {
				if (FFCommonConfig.appleSaplingFromHeroOfTheVillage) {
					info += "\n";
				}
				info += I18n.get("gui.fruitfulfun.tip.villageAppleTreeWorldGen");
			}
			ItemStack appleSapling = CoreModule.APPLE_SAPLING.itemStack();
			registrar.accept(List.of(appleSapling), Component.literal(info));
		}
	}
}
