package snownee.fruits;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public final class FFCommonConfig {

	public enum DropMode {
		NO_DROP, INDEPENDENT, ONE_BY_ONE
	}

	@KiwiConfig.Range(min = 0, max = 100)
	public static int growingSpeed = 5;
	public static DropMode fruitDropModeSingleplayer = DropMode.INDEPENDENT;
	public static DropMode fruitDropModeMultiplayer = DropMode.ONE_BY_ONE;
	public static boolean appleSaplingFromHeroOfTheVillage = true;
	@KiwiConfig.LevelRestart
	public static boolean villageAppleTreeWorldGen = true;
	public static String hornHarvestingInstrument = "minecraft:sing_goat_horn";

	public static DropMode getDropMode(LevelAccessor level) {
		MinecraftServer server = level.getServer();
		return (server != null && server.isDedicatedServer()) ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
	}
}
