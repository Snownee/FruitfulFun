package snownee.fruits.mixin;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import snownee.fruits.MainModule;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {

    protected MixinServerWorld(WorldInfo info, DimensionType dimType, BiFunction<World, Dimension, AbstractChunkProvider> provider, IProfiler profilerIn, boolean remote) {
        super(info, dimType, provider, profilerIn, remote);
    }

    @Inject(at = @At("RETURN"), method = "addLightningBolt")
    private void onLightningBolt(LightningBoltEntity entityIn, CallbackInfo cir) {
        BlockPos pos = entityIn.getPosition();
        for (BlockPos pos2 : BlockPos.getAllInBoxMutable(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2)) {
            BlockState state2 = this.getBlockState(pos2);
            if (state2.getBlock() == MainModule.CITRON_LEAVES && state2.get(FruitLeavesBlock.AGE) == 3) {
                this.setBlockState(pos2, state2.with(FruitLeavesBlock.AGE, 1));
                if (this.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && !this.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
                {
                    ItemStack stack = new ItemStack(MainModule.EMPOWERED_CITRON);
                    double d0 = this.rand.nextFloat() * 0.5F + 0.25D;
                    double d1 = this.rand.nextFloat() * 0.5F + 0.25D;
                    double d2 = this.rand.nextFloat() * 0.5F + 0.25D;
                    ItemEntity entityitem = new ItemEntity(this, pos2.getX() + d0, pos2.getY() + d1, pos2.getZ() + d2, stack);
                    entityitem.setDefaultPickupDelay();
                    entityitem.setInvulnerable(true);
                    this.addEntity(entityitem);
                    BatEntity bat = new BatEntity(EntityType.BAT, this);
                    bat.setPosition(pos2.getX() + d0, pos2.getY() + d1, pos2.getZ() + d2);
                    bat.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 200, 10));
                    bat.setCustomName(new TranslationTextComponent("fruittrees.forestbat"));
                    bat.setCustomNameVisible(true);
                    this.addEntity(bat);
                }
            }
        }
    }

}
