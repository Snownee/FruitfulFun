package snownee.fruits;

import snownee.kiwi.config.KiwiConfig;

@KiwiConfig(type = KiwiConfig.ConfigType.CLIENT)
public final class FFClientConfig {

	public enum CherryParticleOption {
		Vanilla, Modded, Disabled
	}

	public static CherryParticleOption cherryParticle = CherryParticleOption.Modded;

}
