package snownee.fruits.compat.lychee;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HybridizingRecipe;
import snownee.fruits.food.FoodModule;
import snownee.fruits.ritual.RitualModule;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.compat.recipeviewer.element.SideBlockIcon;
import snownee.lychee.util.CommonProxy;

public class LycheeCompat {
	public static void init() {
		if (Hooks.bee) {
			RecipeSynchronization.synchronizeRecipeSerializer(BeeModule.RECIPE_SERIALIZER.getOrCreate());
			CommonProxy.registerRecipeCategoryListener($ -> {
				$.register(
						new HybridizingRecipeCategory(), it -> {
							it.width = 170;
							it.iconProvider = _ -> new SideBlockIcon(
									GuiGameElement.of(CoreModule.GRAPEFRUIT),
									Blocks.BEEHIVE::defaultBlockState);
						});
			});
		}
		if (Hooks.ritual) {
			RecipeSynchronization.synchronizeRecipeSerializer(RitualModule.RECIPE_SERIALIZER.getOrCreate());
			CommonProxy.registerRecipeCategoryListener($ -> {
				$.register(
						new DragonRitualCategory(), it -> {
							it.iconProvider = _ -> new SideBlockIcon(
									GuiGameElement.of(Items.DRAGON_HEAD),
									FoodModule.CHORUS_FRUIT_PIE::defaultBlockState);
							it.setSimpleWorkstationProvider(_ -> List.of(Items.DRAGON_HEAD, FoodModule.CHORUS_FRUIT_PIE.asItem()));
						});
			});
		}
	}
//	public static void renderBee(GuiGraphics graphics, HybridizingRecipe recipe, Bee bee) {
//		Minecraft mc = Minecraft.getInstance();
//		if (mc.player == null) {
//			return;
//		}
//		bee.setLevel(mc.level);
//		bee.tickCount = mc.player.tickCount;
//
//		PoseStack matrixStack = graphics.pose();
//		matrixStack.pushPose();
//		matrixStack.translate(85, 24, 20);
//		matrixStack.scale(20, 20, 20);
//
//		float toRad = 0.01745329251F;
//		Quaternionf quaternion = new Quaternionf().rotateXYZ(170 * toRad, 135 * toRad, 0);
//		matrixStack.mulPose(quaternion);
//
//		ILightingSettings.DEFAULT_FLAT.applyLighting();
//		EntityRenderDispatcher renderDispatcher = mc.getEntityRenderDispatcher();
//		quaternion.conjugate();
//		renderDispatcher.overrideCameraOrientation(quaternion);
//		renderDispatcher.setRenderShadow(false);
//		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
//
//		BeeAttributes attributes = BeeAttributes.of(bee);
//		attributes.getGenes().getTraits().clear();
//		for (ContextualCondition condition : recipe.getConditions()) {
//			if (!BeeModule.BEE_HAS_TRAIT.is(condition.getType())) {
//				continue;
//			}
//			BeeHasTrait beeHasTrait = (BeeHasTrait) condition;
//			attributes.getGenes().getTraits().add(beeHasTrait.trait());
//		}
//		attributes.updateTexture();
//		renderDispatcher.render(bee, 0.0D, 0.0D, 0.0D, mc.getFrameTime(), 1, matrixStack, bufferSource, 15728880);
//
//		bufferSource.endBatch();
//		renderDispatcher.setRenderShadow(true);
//		matrixStack.popPose();
//		bee.setLevel(null);
//		ILightingSettings.DEFAULT_3D.applyLighting();
//	}

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
		List<Input> inputs = Lists.newArrayListWithExpectedSize(recipe.pollens().size());
		for (String pollen : recipe.pollens()) {
			Block block = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(pollen));
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

		public Input(Ingredient itemIngredient) {
			this.itemIngredient = Objects.requireNonNull(itemIngredient);
			this.block = null;
		}

		public Input(Block block) {
			this.itemIngredient = null;
			this.block = Objects.requireNonNull(block);
		}

		public boolean isItem() {
			return itemIngredient != null;
		}
	}
}
