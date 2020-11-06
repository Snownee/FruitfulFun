package snownee.fruits;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.HybridingContext;
import snownee.fruits.hybridization.HybridingRecipe;
import snownee.fruits.hybridization.Hybridization;
import snownee.kiwi.KiwiModule.LoadingCondition;
import snownee.kiwi.LoadingContext;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

public final class Hook {

    private Hook() {}

    public static boolean safeSetBlock(World world, BlockPos pos, BlockState state) {
        BlockState old = world.getBlockState(pos);
        if (old == state || old.hasTileEntity() || old.getPushReaction() == PushReaction.BLOCK || old.getBlock() == Blocks.OBSIDIAN || old.getBlock().isIn(BlockTags.WITHER_IMMUNE)) {
            return false;
        }
        return world.setBlockState(pos, state);
    }

    public static boolean canPollinate(BlockState state) {
        if (state.isIn(BlockTags.TALL_FLOWERS)) {
            if (state.getBlock() == Blocks.SUNFLOWER) {
                return state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
            } else {
                return true;
            }
        } else if (state.isIn(BlockTags.SMALL_FLOWERS)) {
            return true;
        } else if (Hybridization.INSTANCE != null && state.getBlock() instanceof FruitLeavesBlock) {
            if (!((FruitLeavesBlock) state.getBlock()).canGrow(state)) {
                return false;
            }
            return state.get(FruitLeavesBlock.AGE) == 2;
        } else {
            return false;
        }
    }

    public static void onPollinateComplete(BeeEntity bee) {
        BlockState state = bee.world.getBlockState(bee.savedFlowerPos);
        Block block = state.getBlock();
        FruitType type = block instanceof FruitLeavesBlock ? ((FruitLeavesBlock) block).type.get() : null;
        NBTHelper data = NBTHelper.of(bee.getPersistentData());
        ListNBT list = data.getTagList("FruitsList", Constants.NBT.TAG_STRING);
        if (list == null) {
            list = new ListNBT();
            data.setTag("FruitsList", list);
        }
        String newPollen = type != null ? type.name() : "_" + Util.trimRL(block.getRegistryName());
        if (list.stream().anyMatch(e -> e.getString().equals(newPollen))) {
            return;
        }
        StringNBT newPollenNBT = StringNBT.valueOf(newPollen);
        if (list.size() >= 1) {
            Collection<Either<FruitType, Block>> pollenList = readPollen(list);
            pollenList.add(parsePollen(newPollen));
            Optional<HybridingRecipe> recipe = bee.world.getRecipeManager().getRecipe(Hybridization.RECIPE_TYPE, new HybridingContext(pollenList), bee.world);
            if (recipe.isPresent()) {
                Block newBlock = recipe.get().getResultAsBlock(pollenList);
                boolean isLeaves = newBlock instanceof FruitLeavesBlock;
                boolean isFlower = !isLeaves && newBlock.isIn(BlockTags.FLOWERS);
                boolean isMisc = !isLeaves && !isFlower;
                if (!isMisc && (isLeaves != (block instanceof FruitLeavesBlock))) {
                    return;
                }
                BlockPos root = bee.savedFlowerPos;
                if (block.isIn(BlockTags.TALL_FLOWERS) && state.hasProperty(DoublePlantBlock.HALF) && state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                    root = root.down();
                } else if (isMisc && !newBlock.canSpawnInBlock() && !(block instanceof FruitLeavesBlock)) {
                    root = root.down();
                }
                BlockState newState = newBlock.getDefaultState();
                boolean isBigFlower = false;
                if (isLeaves) {
                    newState = newState.with(FruitLeavesBlock.AGE, 2);
                    newState = newState.with(LeavesBlock.DISTANCE, state.get(LeavesBlock.DISTANCE));
                } else if (isFlower) {
                    if (newBlock.isIn(BlockTags.TALL_FLOWERS) && newState.hasProperty(DoublePlantBlock.HALF)) {
                        newState = newState.with(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
                        isBigFlower = true;
                    }
                }
                boolean placed = safeSetBlock(bee.world, root, newState);
                if (placed && isBigFlower) {
                    newState = newState.with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                    safeSetBlock(bee.world, root.up(), newState);
                }
                if (placed) {
                    data.remove("FruitsList");
                    return;
                }
            }
        }
        /* off */
        if (list.size() > 3) {
            int toRemove = list.size() - 3;
            while (toRemove --> 0) {
                list.remove(0);
            }
        }
        list.add(newPollenNBT);
        /* on */
    }

    public static List<Either<FruitType, Block>> readPollen(ListNBT list) {
        List<Either<FruitType, Block>> pollenList = Lists.newArrayList();
        list.forEach(e -> pollenList.add(parsePollen(e.getString())));
        return pollenList;
    }

    public static Either<FruitType, Block> parsePollen(String id) {
        if (id.startsWith("_")) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id.substring(1)));
            return Either.right(block);
        } else {
            FruitType type = FruitType.parse(id);
            return Either.left(type);
        }
    }

    @LoadingCondition("hybridization")
    public static boolean shouldLoadHybridization(LoadingContext ctx) {
        return FruitsMod.mixin;
    }
}
