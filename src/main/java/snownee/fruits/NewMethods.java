package snownee.fruits;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public final class NewMethods {

    public static boolean mixin;

    private NewMethods() {}

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
        PathNodeType type = blockstate.getAiPathNodeType(blockaccessIn, blockpos, entity);
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
}
