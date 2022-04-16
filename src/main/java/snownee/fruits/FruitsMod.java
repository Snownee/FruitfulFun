package snownee.fruits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FruitsMod.ID)
public final class FruitsMod {
	public static final String ID = "fruittrees";
	public static final String NAME = "Fruit Trees";

	public static Logger logger = LogManager.getLogger(FruitsMod.NAME);

	public FruitsMod() {
		MinecraftForge.EVENT_BUS.addListener(CoreModule::insertFeatures);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(FruitsEvents::addPackFinder);
	}
}
