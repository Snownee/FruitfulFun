package snownee.fruits;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public final class FFCommonConfig {

	public enum DropMode {
		NoDrop, Independent, OneByOne
	}

	@KiwiConfig.Range(min = 0, max = 100)
	public static int growingSpeed = 5;
	@KiwiConfig.Path("fruitDropMode.singleplayer")
	public static DropMode fruitDropModeSingleplayer = DropMode.Independent;
	@KiwiConfig.Path("fruitDropMode.multiplayer")
	public static DropMode fruitDropModeMultiplayer = DropMode.OneByOne;
	@KiwiConfig.Path("fruitTreeLifespan.min")
	public static int fruitTreeLifespanMin = 14;
	@KiwiConfig.Path("fruitTreeLifespan.max")
	public static int fruitTreeLifespanMax = 24;
	public static boolean appleSaplingFromHeroOfTheVillage = true;
	@KiwiConfig.GameRestart
	public static boolean villageAppleTreeWorldGen = true;
	public static String hornHarvestingInstrument = "minecraft:sing_goat_horn";
	public static boolean wanderingTraderSapling = true;
	public static int beeNaturalHealingInterval = 900;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.RC")
	public static float mutationRateRC = 0.06f;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.FC")
	public static float mutationRateFC = 0.06f;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.FT1")
	public static float mutationRateFT1 = 0.06f;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.FT2")
	public static float mutationRateFT2 = 0.06f;

	public static DropMode getDropMode(LevelAccessor level) {
		MinecraftServer server = level.getServer();
		return (server != null && server.isDedicatedServer()) ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
	}
}
