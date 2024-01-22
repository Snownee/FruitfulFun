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

}
