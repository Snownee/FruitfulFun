package snownee.fruits;

import net.minecraft.world.level.Level;
import snownee.kiwi.config.KiwiConfig;
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
	@Range(min = 0, max = 1)
	public static float oakLeavesDropsAppleSapling = 0.2f;
	public static boolean worldGen = true;
	@Range(min = 0, max = 1)
	public static float treesGenInPlains = 0.004f;
	@Range(min = 0, max = 1)
	public static float treesGenInForest = 0.01f;

	public static DropMode getDropMode(Level world) {
		return world.getServer().isDedicatedServer() ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
	}
}
