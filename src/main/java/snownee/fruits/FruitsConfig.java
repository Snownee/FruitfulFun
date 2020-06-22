package snownee.fruits;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

@EventBusSubscriber(bus = Bus.MOD)
public final class FruitsConfig {

    public enum DropMode {
        NO_DROP,
        INDEPENDENT,
        ONE_BY_ONE
    }

    public static int growingSpeed = 5;
    private static DropMode fruitDropModeSingleplayer = DropMode.INDEPENDENT;
    private static DropMode fruitDropModeMultiplayer = DropMode.ONE_BY_ONE;
    public static float oakLeavesDropsAppleSapling = 0.2f;

    private static IntValue growingSpeedCfg;
    private static EnumValue<DropMode> fruitDropModeSingleplayerCfg;
    private static EnumValue<DropMode> fruitDropModeMultiplayerCfg;
    private static DoubleValue oakLeavesDropsAppleSaplingCfg;

    static final ForgeConfigSpec spec;

    static {
        spec = new ForgeConfigSpec.Builder().configure(FruitsConfig::new).getRight();
    }

    private FruitsConfig(ForgeConfigSpec.Builder builder) {
        growingSpeedCfg = builder.defineInRange("growingSpeed", growingSpeed, 0, 100);
        fruitDropModeSingleplayerCfg = builder.defineEnum("fruitDropModeSingleplayer", fruitDropModeSingleplayer);
        fruitDropModeMultiplayerCfg = builder.defineEnum("fruitDropModeMultiplayer", fruitDropModeMultiplayer);
        oakLeavesDropsAppleSaplingCfg = builder.defineInRange("oakLeavesDropsAppleSapling", oakLeavesDropsAppleSapling, 0, 1);
    }

    public static void refresh() {
        growingSpeed = growingSpeedCfg.get();
        fruitDropModeSingleplayer = fruitDropModeSingleplayerCfg.get();
        fruitDropModeMultiplayer = fruitDropModeMultiplayerCfg.get();
        oakLeavesDropsAppleSapling = oakLeavesDropsAppleSaplingCfg.get().floatValue();
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
        refresh();
    }

    public static DropMode getDropMode(World world) {
        return world.getServer().isDedicatedServer() ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
    }
}
