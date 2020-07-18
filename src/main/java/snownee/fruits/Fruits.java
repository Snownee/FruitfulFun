package snownee.fruits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import snownee.kiwi.KiwiModule.LoadingCondition;
import snownee.kiwi.LoadingContext;

@Mod(Fruits.MODID)
public final class Fruits {
    public static final String MODID = "fruittrees";
    public static final String NAME = "Fruit Trees";

    public static Logger logger = LogManager.getLogger(Fruits.NAME);

    public static boolean mixin;

    @LoadingCondition("hybridization")
    public static boolean shouldLoadHybridization(LoadingContext ctx) {
        return mixin;
    }
}
