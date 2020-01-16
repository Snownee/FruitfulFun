package snownee.fruits.hybridization;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import snownee.fruits.hybridization.ai.FruitsGrowCropsGoal;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.Name;

@KiwiModule(name = "hybridization")
@KiwiModule.Optional
@KiwiModule.Subscriber
public class Hybridization extends AbstractModule {

    @Name("hybriding")
    public static final IRecipeType<HybridingRecipe> RECIPE_TYPE = new IRecipeType() {};

    @Name("hybriding")
    public static final IRecipeSerializer<HybridingRecipe> SERIALIZER = new HybridingRecipe.Serializer();

    @SubscribeEvent
    public void replaceBeeAI(EntityJoinWorldEvent event) {
//        if (event.getWorld().isRemote || !(event.getEntity() instanceof BeeEntity)) {
//            return;
//        }
//        BeeEntity bee = (BeeEntity) event.getEntity();
//        bee.goalSelector.goals.removeIf(g -> g.getGoal().getClass() == BeeEntity.FindPollinationTargetGoal.class);
//        bee.goalSelector.flagGoals.values().removeIf(g -> g.getGoal().getClass() == BeeEntity.FindPollinationTargetGoal.class);
//        bee.goalSelector.addGoal(7, new FruitsGrowCropsGoal(bee));
    }

}
