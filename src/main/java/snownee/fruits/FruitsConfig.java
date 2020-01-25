package snownee.fruits;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

@EventBusSubscriber(bus = Bus.MOD)
public final class FruitsConfig {

    public static int growingSpeed = 15;
    public static boolean fruitDrops = true;
    public static float oakLeavesDropsAppleSapling = 0.2f;

    private static IntValue growingSpeedCfg;
    private static BooleanValue fruitDropsCfg;
    private static DoubleValue oakLeavesDropsAppleSaplingCfg;

    static final ForgeConfigSpec spec;

    static {
        spec = new ForgeConfigSpec.Builder().configure(FruitsConfig::new).getRight();
    }

    private FruitsConfig(ForgeConfigSpec.Builder builder) {
        growingSpeedCfg = builder.defineInRange("growingSpeed", growingSpeed, 0, 100);
        fruitDropsCfg = builder.define("fruitDrops", fruitDrops);
        oakLeavesDropsAppleSaplingCfg = builder.defineInRange("oakLeavesDropsAppleSapling", oakLeavesDropsAppleSapling, 0, 1);
    }

    public static void refresh() {
        growingSpeed = growingSpeedCfg.get();
        fruitDrops = fruitDropsCfg.get();
        oakLeavesDropsAppleSapling = oakLeavesDropsAppleSaplingCfg.get().floatValue();
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
        refresh();
    }
}
