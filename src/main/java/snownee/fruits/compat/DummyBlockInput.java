package snownee.fruits.compat;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.lychee.core.def.BlockPredicateHelper;
import snownee.lychee.core.post.PlaceBlock;

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
