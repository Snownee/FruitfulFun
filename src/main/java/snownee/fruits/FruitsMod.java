package snownee.fruits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FruitsMod.ID)
public final class FruitsMod {
	public static final String ID = "fruittrees";
	public static final String NAME = "Fruit Trees";

	public static Logger logger = LogManager.getLogger(FruitsMod.NAME);

	public FruitsMod() {
		var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(FruitsEvents::addPackFinder);
		eventBus.addListener(FruitsEvents::newRegistry);
	}
}
