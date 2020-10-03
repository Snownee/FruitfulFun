package snownee.fruits;

import net.minecraft.world.World;
import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.Range;

@KiwiConfig
public final class FruitsConfig {

    public enum DropMode {
        NO_DROP,
        INDEPENDENT,
        ONE_BY_ONE
    }

    @Range(min = 0, max = 100)
    public static int growingSpeed = 5;
    public static DropMode fruitDropModeSingleplayer = DropMode.INDEPENDENT;
    public static DropMode fruitDropModeMultiplayer = DropMode.ONE_BY_ONE;
    @Range(min = 0, max = 1)
    public static double oakLeavesDropsAppleSapling = 0.2;
    public static boolean worldGen = true;

    public static DropMode getDropMode(World world) {
        return world.getServer().isDedicatedServer() ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
    }
}
