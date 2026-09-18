package snownee.fruits.minigame;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.util.CommonProxy;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule.Optional
@KiwiModule(value = "minigame")
public final class MinigameModule extends AbstractModule {
	@KiwiModule.Category(Categories.FUNCTIONAL_BLOCKS)
	public static final BlockObject<BattleTableBlock> BATTLE_TABLE = block(BattleTableBlock::new, () -> Blocks.CRAFTING_TABLE);
	public static final KiwiGO<BlockEntityType<BattleTableBlockEntity>> BATTLE_TABLE_ENTITY = blockEntity(
			BattleTableBlockEntity::new,
			BattleTableBlock.class);
	public static final TagKey<Block> MINIGAME_TRIGGER = blockTag(FruitfulFun.ID, "minigame_trigger");
	public static Identifier MINIGAME_PIECES_CLEARED = FruitfulFun.id("minigame_pieces_cleared");
	public static Identifier MINIGAME_LINE_CLEARS = FruitfulFun.id("minigame_line_clears");
	public static Identifier MINIGAME_GOALS_COMPLETED = FruitfulFun.id("minigame_goals_completed");

	public MinigameModule() {
		Hooks.minigame = true;
	}

	@Override
	protected void addEntries() {
		CommonProxy.initMinigameModule();
	}

	public static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.getBlockState(hitResult.getBlockPos()).is(MINIGAME_TRIGGER)) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			MinigameManager.promptSolo(serverPlayer);
		}
		return InteractionResult.SUCCESS;
	}
}
