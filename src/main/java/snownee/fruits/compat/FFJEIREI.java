package snownee.fruits.compat;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
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
		attributes.getGenes().getTraits().clear();
		for (ContextualCondition condition : recipe.getConditions()) {
			if (!BeeModule.BEE_HAS_TRAIT.is(condition.getType())) {
				continue;
			}
			BeeHasTrait beeHasTrait = (BeeHasTrait) condition;
			attributes.getGenes().getTraits().add(beeHasTrait.trait());
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
				info = I18n.get("tip.fruitfulfun.appleSaplingFromHeroOfTheVillage");
			}
			if (FFCommonConfig.villageAppleTreeWorldGen) {
				if (FFCommonConfig.appleSaplingFromHeroOfTheVillage) {
					info += "\n";
				}
				info += I18n.get("tip.fruitfulfun.villageAppleTreeWorldGen");
			}
			ItemStack appleSapling = CoreModule.APPLE_SAPLING.itemStack();
			registrar.accept(List.of(appleSapling), Component.literal(info));
		}
	}

	public static List<Input> getInputs(HybridizingRecipe recipe) {
		List<Input> inputs = Lists.newArrayListWithExpectedSize(recipe.pollens.size());
		for (String pollen : recipe.pollens) {
			Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(pollen));
			Item item = block.asItem();
			if (item == Items.AIR) {
				inputs.add(new Input(block));
			} else {
				inputs.add(new Input(Ingredient.of(item)));
			}
		}
		return inputs;
	}

	public static class Input {
		@Nullable
		public final Ingredient itemIngredient;
		@Nullable
		public final Block block;

		public Input(@NotNull Ingredient itemIngredient) {
			this.itemIngredient = Objects.requireNonNull(itemIngredient);
			this.block = null;
		}

		public Input(@NotNull Block block) {
			this.itemIngredient = null;
			this.block = Objects.requireNonNull(block);
		}

		public boolean isItem() {
			return itemIngredient != null;
		}
	}
}
