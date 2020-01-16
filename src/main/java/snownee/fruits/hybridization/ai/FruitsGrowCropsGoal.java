package snownee.fruits.hybridization.ai;

import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.Fruits;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.HybridingContext;
import snownee.fruits.hybridization.Hybridization;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

public class FruitsGrowCropsGoal extends Goal {

    protected final BeeEntity bee;

    public FruitsGrowCropsGoal(BeeEntity bee) {
        this.bee = bee;
    }

    public boolean canBeeStart() {
        if (bee.func_226419_eM_/*getCropsGrownSincePollination*/() >= 10) {
            return false;
        } else if (bee.getRNG().nextFloat() < 0.3F) {
            return false;
        } else {
            return bee.func_226411_eD_/*hasNectar*/() && bee.func_226422_eP_/*isHiveValid*/();
        }
    }

    public boolean canBeeContinue() {
        return this.canBeeStart();
    }

    @Override
    public void tick() {
        if (bee.getRNG().nextInt(30) == 0) {
            for (int i = 0; i <= 3; ++i) {
                BlockPos blockPos = new BlockPos(bee).down(i);
                BlockState blockState = bee.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                boolean bl = false;
                IntegerProperty intProperty = null;
                if (block.isIn(BlockTags.field_226153_ac_/*BEE_GROWABLES*/)) {
                    Fruits.Type type = null; // patch
                    if (block instanceof CropsBlock) {
                        CropsBlock cropBlock = (CropsBlock) block;
                        if (!cropBlock.isMaxAge(blockState)) {
                            bl = true;
                            intProperty = cropBlock.getAgeProperty();
                        }
                    } else {
                        int k;
                        if (block instanceof StemBlock) {
                            k = blockState.get(StemBlock.AGE);
                            if (k < 7) {
                                bl = true;
                                intProperty = StemBlock.AGE;
                            }
                        } else if (block == Blocks.SWEET_BERRY_BUSH) {
                            k = blockState.get(SweetBerryBushBlock.AGE);
                            if (k < 3) {
                                bl = true;
                                intProperty = SweetBerryBushBlock.AGE;
                            }
                        } // patch start
                        else if (block instanceof FruitLeavesBlock) {
                            k = blockState.get(FruitLeavesBlock.AGE);
                            if (k > 0 && k < 3) {
                                bl = true;
                                intProperty = FruitLeavesBlock.AGE;
                                type = ((FruitLeavesBlock) block).type.get();
                            }
                        } // patch end
                    }

                    if (bl) {
                        bee.world.playEvent(2005, blockPos, 0);
                        bee.world.setBlockState(blockPos, blockState.with(intProperty, blockState.get(intProperty) + 1));
                        bee.func_226421_eO_/*addCropCounter*/();
                        // patch start
                        NBTHelper data = NBTHelper.of(bee.getPersistentData());
                        int count = data.getInt("FruitsCount");
                        ListNBT list = data.getTagList("FruitsList", Constants.NBT.TAG_STRING);
                        if (list == null) {
                            list = new ListNBT();
                            data.setTag("FruitsList", list);
                        }
                        System.out.println(list);
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
                            bee.world.getRecipeManager().getRecipe(Hybridization.RECIPE_TYPE, new HybridingContext(ingredients), bee.world);
                        }
                    }
                }
            }

        }
    }

    @Override
    public boolean shouldExecute() {
        return this.canBeeStart() && !bee.func_226427_ez_/*isAngry*/();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.canBeeContinue() && !bee.func_226427_ez_/*isAngry*/();
    }

}
