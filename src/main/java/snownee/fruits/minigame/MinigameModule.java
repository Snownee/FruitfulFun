package snownee.fruits.minigame;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

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
}
