package snownee.fruits.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

@Mixin(FoliagePlacer.class)
public interface FoliagePlacerAccess {
	@Invoker
	boolean callShouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6);

	@Invoker
	void callCreateFoliage(
			LevelSimulatedReader var1,
			FoliagePlacer.FoliageSetter var2,
			RandomSource var3,
			TreeConfiguration var4,
			int var5,
			FoliagePlacer.FoliageAttachment var6,
			int var7,
			int var8,
			int var9);
}
