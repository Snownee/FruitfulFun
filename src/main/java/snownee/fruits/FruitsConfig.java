package snownee.fruits;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.LevelRestart;
import snownee.kiwi.config.KiwiConfig.Range;

@KiwiConfig
public final class FruitsConfig {

    public enum DropMode {
        NO_DROP, INDEPENDENT, ONE_BY_ONE
    }

    @Range(min = 0, max = 100)
    public static int growingSpeed = 5;
    public static DropMode fruitDropModeSingleplayer = DropMode.INDEPENDENT;
    public static DropMode fruitDropModeMultiplayer = DropMode.ONE_BY_ONE;
    public static boolean appleSaplingFromHeroOfTheVillage = true;
    @LevelRestart
    public static boolean villageAppleTreeWorldGen = true;

    public static DropMode getDropMode(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        return (server != null && server.isDedicatedServer()) ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
    }
}
