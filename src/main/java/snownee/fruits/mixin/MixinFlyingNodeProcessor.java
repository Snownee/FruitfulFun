package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.pathfinding.FlyingNodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.world.IBlockReader;
import snownee.fruits.NewMethods;

@Mixin(FlyingNodeProcessor.class)
public abstract class MixinFlyingNodeProcessor extends WalkNodeProcessor {

    @Redirect(
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/pathfinding/FlyingNodeProcessor;getPathNodeTypeRaw(Lnet/minecraft/world/IBlockReader;III)Lnet/minecraft/pathfinding/PathNodeType;"
            ), method = "getPathNodeType(Lnet/minecraft/world/IBlockReader;III)Lnet/minecraft/pathfinding/PathNodeType;"
    )
    private PathNodeType redirectGetPathNodeTypeRaw(IBlockReader blockaccessIn, int x, int y, int z) {
        return NewMethods.getPathNodeTypeRaw(blockaccessIn, x, y, z, entity);
    }

    @Redirect(
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/pathfinding/FlyingNodeProcessor;checkNeighborBlocks(Lnet/minecraft/world/IBlockReader;IIILnet/minecraft/pathfinding/PathNodeType;)Lnet/minecraft/pathfinding/PathNodeType;"
            ), method = "getPathNodeType(Lnet/minecraft/world/IBlockReader;III)Lnet/minecraft/pathfinding/PathNodeType;"
    )
    private PathNodeType redirectCheckNeighborBlocks(IBlockReader blockaccessIn, int x, int y, int z, PathNodeType pathNodeType) {
        return NewMethods.checkNeighborBlocks(blockaccessIn, x, y, z, pathNodeType, entity);
    }
}
