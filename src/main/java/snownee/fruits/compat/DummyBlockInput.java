package snownee.fruits.compat;

import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.util.BlockPredicateHelper;
import snownee.lychee.context.ActionContext;
import snownee.lychee.util.action.PostAction;
import snownee.lychee.util.action.PostActionCommonProperties;
import snownee.lychee.util.action.PostActionType;
import snownee.lychee.util.context.LycheeContext;

//TODO move to Lychee
public record DummyBlockInput(PostActionCommonProperties commonProperties, BlockPredicate block) implements PostAction {

	public DummyBlockInput(Block block) {
		this(PostActionCommonProperties.EMPTY, BlockPredicate.Builder.block().of(BuiltInRegistries.BLOCK, block).build());
	}

	@Override
	public PostActionType<?> type() {
		return null;
	}

	@Override
	public void apply(LycheeContext lycheeContext, ActionContext actionContext, int i) {

	}

	@Override
	public Component getDisplayName() {
		BlockState state = BlockPredicateHelper.anyBlockState(this.block);
		return state.getBlock().getName();
	}
}
