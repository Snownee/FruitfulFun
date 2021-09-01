package snownee.fruits.levelgen.treedecorators;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import snownee.fruits.CoreModule;

public class CarpetTreeDecorator extends TreeDecorator {
	public static final Codec<CarpetTreeDecorator> CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(CarpetTreeDecorator::new, decorator -> {
		return decorator.carpetProvider;
	}).codec();
	private final BlockStateProvider carpetProvider;

	public CarpetTreeDecorator(BlockStateProvider carpetProvider) {
		this.carpetProvider = carpetProvider;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return CoreModule.CARPET_DECORATOR;
	}

	@Override
	public void place(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, Random pRandom, List<BlockPos> pLogPositions, List<BlockPos> pLeafPositions) {
		if (pLeafPositions.isEmpty()) {
			return;
		}
		int y = pLeafPositions.get(0).getY() + 1;
		for (BlockPos pos : pLeafPositions) {
			if (pos.getY() > y) {
				break;
			}
			placeCarpet(pLevel, pos, carpetProvider.getState(pRandom, pos), pBlockSetter);
		}
	}

	public static boolean placeCarpet(LevelSimulatedReader world, BlockPos pos, BlockState carpet, BiConsumer<BlockPos, BlockState> pBlockSetter) {
		int i = 0;
		MutableBlockPos ground = pos.mutable();
		while (++i < 5) {
			ground.move(Direction.DOWN);
			if (!world.isStateAtPosition(ground, CarpetTreeDecorator::isReplaceable)) {
				if (i == 1) {
					return false;
				} else {
					break;
				}
			}
		}
		if (!Feature.isGrassOrDirt(world, ground)) {
			return false;
		}
		pBlockSetter.accept(ground.move(Direction.UP), carpet);
		return true;
	}

	private static boolean isReplaceable(BlockState state) {
		return state.isAir() || state.is(Blocks.TALL_GRASS) || state.is(Blocks.FERN);
	}
}
