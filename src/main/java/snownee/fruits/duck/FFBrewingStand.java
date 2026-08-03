package snownee.fruits.duck;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import snownee.fruits.FFCommonConfig;

public interface FFBrewingStand {
	Component DEFAULT_NAME = Component.translatable("container.fruitfulfun.brewer");

	static double calculateBrewSpeedBonus(int bonus) {
		return Mth.clamp((double) bonus / FFCommonConfig.brewerMaxSpeedRequirement, 0, 1);
	}

	boolean fruits$isBrewer();

	ContainerData fruits$dataAccess();

	void fruits$updateRecipeHash(int[] recipeHash);
}
