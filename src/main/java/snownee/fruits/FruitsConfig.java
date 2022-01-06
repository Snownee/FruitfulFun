package snownee.fruits;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
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
	@Range(min = 2, max = 10000)
	public static int treesGenChunksInPlains = 500;
	@Range(min = 2, max = 10000)
	public static int treesGenChunksInForest = 200;
	@Range(min = 2, max = 10000)
	public static int treesGenChunksInJungle = 10;

	public static DropMode getDropMode(LevelAccessor level) {
		MinecraftServer server = level.getServer();
		return (server != null && server.isDedicatedServer()) ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
	}
}
