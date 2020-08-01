package snownee.fruits;

import java.util.Set;

import com.google.common.collect.Sets;
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
        int count = data.getInt("FruitsCount");
        ListNBT list = data.getTagList("FruitsList", Constants.NBT.TAG_STRING);
        if (list == null) {
            list = new ListNBT();
            data.setTag("FruitsList", list);
        }
        String id = type != null ? type.name() : "_" + Util.trimRL(block.getRegistryName());
        if (!list.stream().anyMatch(e -> e.getString().equals(id))) {
            StringNBT stringNBT = StringNBT.valueOf(id);
            if (list.size() < 5) {
                list.add(stringNBT);
            } else {
                list.set(count % 5, stringNBT);
            }
            data.setInt("FruitsCount", count + 1);
        }
        if (list.size() > 1) {
            Set<Either<FruitType, Block>> ingredients = Sets.newHashSet();
            list.forEach(e -> {
                String _id = e.getString();
                if (_id.startsWith("_")) {
                    Block _block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(_id.substring(1)));
                    ingredients.add(Either.right(_block));
                } else {
                    FruitType _type = FruitType.parse(_id);
                    ingredients.add(Either.left(_type));
                }
            });
            bee.world.getRecipeManager().getRecipe(Hybridization.RECIPE_TYPE, new HybridingContext(ingredients), bee.world).ifPresent(recipe -> {
                Block newBlock = recipe.getResultAsBlock(ingredients);
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
                boolean success = Hook.safeSetBlock(bee.world, root, newState);
                if (success && isBigFlower) {
                    newState = newState.with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                    Hook.safeSetBlock(bee.world, root.up(), newState);
                }
                if (success) {
                    data.remove("FruitsList");
                    data.setInt("FruitsCount", 0);
                }
            });
        }
    }

    @LoadingCondition("hybridization")
    public static boolean shouldLoadHybridization(LoadingContext ctx) {
        return FruitsMod.mixin;
    }
}
