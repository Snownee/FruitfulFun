package snownee.fruits;

import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.HybridingContext;
import snownee.fruits.hybridization.Hybridization;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

public final class Hook {

    public static boolean mixin;

    private Hook() {}

    public static PathNodeType func_227480_b_(IBlockReader p_227480_0_, int p_227480_1_, int p_227480_2_, int p_227480_3_, MobEntity entity) {
        PathNodeType pathnodetype = getPathNodeTypeRaw(p_227480_0_, p_227480_1_, p_227480_2_, p_227480_3_, entity);
        if (pathnodetype == PathNodeType.OPEN && p_227480_2_ >= 1) {
            Block block = p_227480_0_.getBlockState(new BlockPos(p_227480_1_, p_227480_2_ - 1, p_227480_3_)).getBlock();
            PathNodeType pathnodetype1 = getPathNodeTypeRaw(p_227480_0_, p_227480_1_, p_227480_2_ - 1, p_227480_3_, entity);
            pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
                pathnodetype = PathNodeType.DAMAGE_OTHER;
            }

            if (pathnodetype1 == PathNodeType.STICKY_HONEY) {
                pathnodetype = PathNodeType.STICKY_HONEY;
            }
        }

        if (pathnodetype == PathNodeType.WALKABLE) {
            pathnodetype = checkNeighborBlocks(p_227480_0_, p_227480_1_, p_227480_2_, p_227480_3_, pathnodetype, entity);
        }

        return pathnodetype;
    }

    public static PathNodeType checkNeighborBlocks(IBlockReader p_193578_0_, int blockaccessIn, int x, int y, PathNodeType z, MobEntity entity) {
        try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain()) {
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        if (i != 0 || k != 0) {
                            PathNodeType type = getPathNodeTypeRaw(p_193578_0_, blockaccessIn, x, y, entity);
                            if (type == PathNodeType.DANGER_CACTUS || type == PathNodeType.DANGER_FIRE || type == PathNodeType.DANGER_OTHER)
                                z = type;
                        }
                    }
                }
            }
        }
        return z;
    }

    public static PathNodeType getPathNodeTypeRaw(IBlockReader blockaccessIn, int x, int y, int z, MobEntity entity) {
        BlockPos blockpos = new BlockPos(x, y, z);
        BlockState blockstate = blockaccessIn.getBlockState(blockpos);
        PathNodeType type;
        if (Hybridization.INSTANCE == null) {
            type = blockstate.getAiPathNodeType(blockaccessIn, blockpos);
        } else {
            type = blockstate.getAiPathNodeType(blockaccessIn, blockpos, entity);
        }
        if (type != null)
            return type;
        Block block = blockstate.getBlock();
        Material material = blockstate.getMaterial();
        if (blockstate.isAir(blockaccessIn, blockpos)) {
            return PathNodeType.OPEN;
        } else if (!block.isIn(BlockTags.TRAPDOORS) && block != Blocks.LILY_PAD) {
            if (block == Blocks.FIRE) {
                return PathNodeType.DAMAGE_FIRE;
            } else if (block == Blocks.CACTUS) {
                return PathNodeType.DAMAGE_CACTUS;
            } else if (block == Blocks.SWEET_BERRY_BUSH) {
                return PathNodeType.DAMAGE_OTHER;
            } else if (block == Blocks.field_226907_mc_) {
                return PathNodeType.STICKY_HONEY;
            } else if (block == Blocks.COCOA) {
                return PathNodeType.COCOA;
            } else if (block instanceof DoorBlock && material == Material.WOOD && !blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_WOOD_CLOSED;
            } else if (block instanceof DoorBlock && material == Material.IRON && !blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_IRON_CLOSED;
            } else if (block instanceof DoorBlock && blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_OPEN;
            } else if (block instanceof AbstractRailBlock) {
                return PathNodeType.RAIL;
            } else if (block instanceof LeavesBlock) {
                return PathNodeType.LEAVES;
            } else if (!block.isIn(BlockTags.FENCES) && !block.isIn(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.get(FenceGateBlock.OPEN))) {
                IFluidState ifluidstate = blockaccessIn.getFluidState(blockpos);
                if (ifluidstate.isTagged(FluidTags.WATER)) {
                    return PathNodeType.WATER;
                } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
                    return PathNodeType.LAVA;
                } else {
                    return blockstate.allowsMovement(blockaccessIn, blockpos, PathType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
                }
            } else {
                return PathNodeType.FENCE;
            }
        } else {
            return PathNodeType.TRAPDOOR;
        }
    }

    public static boolean safeSetBlock(World world, BlockPos pos, BlockState state) {
        BlockState old = world.getBlockState(pos);
        if (old == state || old.hasTileEntity() || old.getPushReaction() == PushReaction.BLOCK || old.getBlock() == Blocks.OBSIDIAN || old.getBlock().isIn(BlockTags.WITHER_IMMUNE)) {
            return false;
        }
        return world.setBlockState(pos, state);
    }

    public static boolean canPollinate(BlockState state) {
        if (state.isIn(BlockTags.field_226148_H_)) {
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
        BlockState state = bee.world.getBlockState(bee.field_226368_bH_);
        Block block = state.getBlock();
        Fruits.Type type = block instanceof FruitLeavesBlock ? ((FruitLeavesBlock) block).type.get() : null;
        NBTHelper data = NBTHelper.of(bee.getPersistentData());
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
        if (list.size() > 1) {
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
            bee.world.getRecipeManager().getRecipe(Hybridization.RECIPE_TYPE, new HybridingContext(ingredients), bee.world).ifPresent(recipe -> {
                Block newBlock = recipe.getResultAsBlock(ingredients);
                boolean isLeaves = newBlock instanceof FruitLeavesBlock;
                boolean isFlower = !isLeaves && newBlock.isIn(BlockTags.field_226149_I_); // flowers
                boolean isMisc = !isLeaves && !isFlower;
                if (!isMisc && (isLeaves != (block instanceof FruitLeavesBlock))) {
                    return;
                }
                BlockPos root = bee.field_226368_bH_;
                if (block.isIn(BlockTags.field_226148_H_) && state.has(DoublePlantBlock.HALF) && state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                    root = root.down();
                } else if (isMisc && !newBlock.canSpawnInBlock() && !(block instanceof FruitLeavesBlock)) {
                    root = root.down();
                }
                BlockState newState = newBlock.getDefaultState();
                boolean isBigFlower = false;
                if (isLeaves) {
                    newState = newState.with(FruitLeavesBlock.AGE, 2);
                    newState = newState.with(FruitLeavesBlock.DISTANCE, state.get(FruitLeavesBlock.DISTANCE));
                } else if (isFlower) {
                    if (newBlock.isIn(BlockTags.field_226148_H_) && newState.has(DoublePlantBlock.HALF)) {
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
}
