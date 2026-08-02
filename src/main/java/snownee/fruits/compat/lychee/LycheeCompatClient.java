package snownee.fruits.compat.lychee;

import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.Hooks;
import snownee.fruits.food.FoodModule;
import snownee.lychee.client.gui.GuiGameElement;
import snownee.lychee.compat.recipeviewer.element.SideBlockIcon;
import snownee.lychee.util.CommonProxy;

public class LycheeCompatClient {
	public static void init() {
		if (Hooks.bee) {
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
}
