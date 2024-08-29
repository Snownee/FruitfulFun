package snownee.fruits;

import snownee.kiwi.config.ConfigUI;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig(type = KiwiConfig.ConfigType.CLIENT)
public final class FFClientConfig {

	public enum CherryParticleOption {
		Vanilla, Modded, Disabled
	}

	public static CherryParticleOption cherryParticle = CherryParticleOption.Modded;
	@KiwiConfig.Range(min = 0, max = 50)
	@ConfigUI.Slider
	public static int moddedCherryParticleFrequency = 32;

	@KiwiConfig.Path("food.statusEffectTooltip")
	public static boolean foodStatusEffectTooltip = true;
	@KiwiConfig.Path("food.specialEffectTooltip")
	public static boolean foodSpecialEffectTooltip = true;
	//TODO: remove this in 1.22.
	// Due to the data component change, this will not work in 1.21.
	// But it becomes a vanilla feature in 1.22. So maybe we should skip to 1.22?
	public static boolean beehiveTooltipDisplayBees = true;

}
