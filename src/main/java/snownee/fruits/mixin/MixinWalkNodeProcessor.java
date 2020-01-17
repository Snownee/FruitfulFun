package snownee.fruits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.world.IBlockReader;
import snownee.fruits.Hook;

@Mixin(WalkNodeProcessor.class)
public abstract class MixinWalkNodeProcessor extends NodeProcessor {

    @Redirect(
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/pathfinding/WalkNodeProcessor;func_227480_b_(Lnet/minecraft/world/IBlockReader;III)Lnet/minecraft/pathfinding/PathNodeType;"
            ), method = "getPathNodeType(Lnet/minecraft/world/IBlockReader;III)Lnet/minecraft/pathfinding/PathNodeType;"
    )
    private PathNodeType redirectGetPathNodeTypeRaw(IBlockReader blockaccessIn, int x, int y, int z) {
        return Hook.func_227480_b_(blockaccessIn, x, y, z, entity);
    }

}
