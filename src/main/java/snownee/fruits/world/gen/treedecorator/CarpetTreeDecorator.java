package snownee.fruits.world.gen.treedecorator;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import snownee.fruits.MainModule;

public class CarpetTreeDecorator extends TreeDecorator {

    private BlockStateProvider carpetProvider;

    public CarpetTreeDecorator(BlockStateProvider carpetProvider) {
        super(MainModule.CARPET_DECORATOR);
        this.carpetProvider = carpetProvider;
    }

    public <T> CarpetTreeDecorator(Dynamic<T> dynamic) {
        this(Registry.BLOCK_STATE_PROVIDER_TYPE.getOrDefault(new ResourceLocation(dynamic.get("provider").get("type").asString().orElseThrow(RuntimeException::new))).func_227399_a_(dynamic.get("provider").orElseEmptyMap()));
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getKey(this.field_227422_a_).toString()), dynamicOps.createString("provider"), this.carpetProvider.serialize(dynamicOps))))).getValue();
    }

    @Override
    public void func_225576_a_(IWorld world, Random rand, List<BlockPos> trunkList, List<BlockPos> foliageList, Set<BlockPos> allBlocks, MutableBoundingBox boundingBox) {
        if (foliageList.isEmpty()) {
            return;
        }
        int y = foliageList.get(0).getY() + 1;
        for (BlockPos pos : foliageList) {
            if (pos.getY() > y) {
                break;
            }
            if (placeCarpet(world, pos, carpetProvider.getBlockState(rand, pos), 19)) {
                allBlocks.add(pos);
                boundingBox.expandTo(new MutableBoundingBox(pos, pos));
            }
        }
    }

    public static boolean placeCarpet(IWorld world, BlockPos pos, BlockState carpet, int flags) {
        int i = 0;
        BlockPos ground = pos;
        BlockState groundState = Blocks.AIR.getDefaultState();
        while (++i < 5) {
            ground = pos.down(i);
            groundState = world.getBlockState(ground);
            if (groundState.getBlock() != Blocks.GRASS && groundState.getBlock() != Blocks.FERN && !groundState.isAir(world, ground)) {
                if (i == 1) {
                    return false;
                } else {
                    break;
                }
            }
        }
        Block block = groundState.getBlock();
        if (block == Blocks.SNOW || block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BARRIER || block == Blocks.HONEY_BLOCK || block == Blocks.SOUL_SAND) {
            return false;
        }
        if (!Block.doesSideFillSquare(groundState.getCollisionShape(world, ground), Direction.UP)) {
            return false;
        }
        return world.setBlockState(ground.up(), carpet, flags);
    }

}
