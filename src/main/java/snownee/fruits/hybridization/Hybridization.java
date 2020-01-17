package snownee.fruits.hybridization;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.fruits.Hook;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiManager;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.Name;

@KiwiModule(name = "hybridization")
@KiwiModule.Optional
@KiwiModule.Subscriber(Bus.MOD)
public class Hybridization extends AbstractModule {

    public static Hybridization INSTANCE;

    @Name("hybriding")
    public static final IRecipeType<HybridingRecipe> RECIPE_TYPE = new IRecipeType() {};

    @Name("hybriding")
    public static final IRecipeSerializer<HybridingRecipe> SERIALIZER = new HybridingRecipe.Serializer();

    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {
        if (!Hook.mixin) {
            KiwiManager.MODULES.remove(uid);
        }
    }

}
