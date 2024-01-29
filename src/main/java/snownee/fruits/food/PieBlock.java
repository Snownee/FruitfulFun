package snownee.fruits.food;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.fruits.util.CommonProxy;

public class PieBlock extends FeastBlock {
	public static final IntegerProperty PIE_SERVINGS = IntegerProperty.create("servings", 1, 4);

	public PieBlock(VoxelShape northShape, @Nullable VoxelShape leftoverShape, Supplier<Item> servingItem) {
		super(northShape, leftoverShape, servingItem);
	}

	@Override
	public IntegerProperty getServingsProperty() {
		return PIE_SERVINGS;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (getServings(state) > 0 && CommonProxy.isKnife(itemStack)) {
			if (!level.isClientSide) {
				ItemStack servingItem = new ItemStack(this.servingItem.get());
				consumeServing(level, pos, state, player);
				popResource(level, pos, servingItem);
			}
			level.playSound(player, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return super.use(state, level, pos, player, hand, hit);
	}
}
