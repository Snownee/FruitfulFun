package snownee.fruits.mixin;

import java.util.Set;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.Fruits;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.HybridingContext;
import snownee.fruits.hybridization.Hybridization;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

@Mixin(BeeEntity.PollinateGoal.class)
public abstract class MixinPollinateGoal extends BeeEntity.PassiveGoal {

    @Shadow
    private BeeEntity this$0;

    MixinPollinateGoal(BeeEntity bee) {
        bee.super();
    }

    @Shadow
    private final Predicate<BlockState> field_226492_c_ = state -> {
        if (state.isIn(BlockTags.field_226148_H_)) {
            if (state.getBlock() == Blocks.SUNFLOWER) {
                return state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
            } else {
                return true;
            }
        } else if (state.isIn(BlockTags.SMALL_FLOWERS)) {
            return true;
        } else if (state.getBlock() instanceof FruitLeavesBlock) {
            if (!((FruitLeavesBlock) state.getBlock()).canGrow(state)) {
                return false;
            }
            return state.get(FruitLeavesBlock.AGE) == 2;
        } else {
            return false;
        }
    };

    @Inject(method = "resetTask", at = @At("HEAD"))
    public void onComplete(CallbackInfo cir) {
        if (this$0.field_226368_bH_ == null) {
            return;
        }
        BlockState state = this$0.world.getBlockState(this$0.field_226368_bH_);
        Block block = state.getBlock();
        Fruits.Type type = block instanceof FruitLeavesBlock ? ((FruitLeavesBlock) block).type.get() : null;
        NBTHelper data = NBTHelper.of(this$0.getPersistentData());
        int count = data.getInt("FruitsCount");
        ListNBT list = data.getTagList("FruitsList", Constants.NBT.TAG_STRING);
        if (list == null) {
            list = new ListNBT();
            data.setTag("FruitsList", list);
        }
        String id = type != null ? type.name() : "_" + Util.trimRL(block.getRegistryName());
        if (!list.stream().anyMatch(e -> e.getString().equals(id))) {
            StringNBT stringNBT = StringNBT.func_229705_a_(id);
            if (list.size() < 5) {
                list.add(stringNBT);
            } else {
                list.set(count % 5, stringNBT);
            }
            data.setInt("FruitsCount", count + 1);
        }
        if (list.size() > 1 && block instanceof FruitLeavesBlock) {
            Set<Either<Fruits.Type, Block>> ingredients = Sets.newHashSet();
            list.forEach(e -> {
                String _id = e.getString();
                if (_id.startsWith("_")) {
                    Block _block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(_id.substring(1)));
                    ingredients.add(Either.right(_block));
                } else {
                    Fruits.Type _type = Fruits.Type.parse(_id);
                    ingredients.add(Either.left(_type));
                }
            });
            this$0.world.getRecipeManager().getRecipe(Hybridization.RECIPE_TYPE, new HybridingContext(ingredients), this$0.world).ifPresent(recipe -> {
                data.remove("FruitsList");
                data.setInt("FruitsCount", 0);
                BlockState newState = recipe.getResult(ingredients).leaves.getDefaultState();
                newState = newState.with(FruitLeavesBlock.AGE, 2);
                newState = newState.with(FruitLeavesBlock.DISTANCE, state.get(FruitLeavesBlock.DISTANCE));
                this$0.world.setBlockState(this$0.field_226368_bH_, newState);
            });
        }
    }
}
