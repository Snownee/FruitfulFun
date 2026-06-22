package snownee.fruits.compat;

import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.util.BlockPredicateHelper;
import snownee.lychee.action.PlaceBlock;

public class DummyBlockInput extends PlaceBlock {
	public DummyBlockInput(Block block) {
		super(BlockPredicate.Builder.block().of(block).build(), BlockPos.ZERO);
	}

	@Override
	public Component getDisplayName() {
		BlockState state = BlockPredicateHelper.anyBlockState(this.block);
		return state.getBlock().getName();
	}
}
