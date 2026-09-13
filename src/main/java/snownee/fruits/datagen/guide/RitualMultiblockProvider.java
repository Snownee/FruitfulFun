package snownee.fruits.datagen.guide;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.MultiblockProvider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.food.FoodModule;

public class RitualMultiblockProvider extends MultiblockProvider {

	public RitualMultiblockProvider(PackOutput packOutput) {
		super(packOutput, FruitfulFun.ID);
	}

	@Override
	public void buildMultiblocks() {
		JsonObject json = new DenseMultiblockBuilder()
				// 顶层：北侧边缘一个龙首（朝南，指向中央），四角蜡烛
				.layer(
						"_________",
						"_________",
						"__C___C__",
						"_________",
						"H________",
						"_________",
						"__C___C__",
						"_________",
						"_________")
				// 中层：八根蜡烛围成菱形环，中央放紫颂果派
				.layer(
						"_________",
						"_________",
						"__*C_C*__",
						"__C___C__",
						"____0____",
						"__C___C__",
						"__*C_C*__",
						"_________",
						"_________")
				// 底层：棋盘格地面
				.layer(
						"*+*+*+*+*",
						"+*+*+*+*+",
						"*+*+*+*+*",
						"+*+*+*+*+",
						"*+*+*+*+*",
						"+*+*+*+*+",
						"*+*+*+*+*",
						"+*+*+*+*+",
						"*+*+*+*+*")
				.display('*', () -> Blocks.LIME_TERRACOTTA)
				.display('+', () -> Blocks.WHITE_CONCRETE)
				.display('0', () -> FoodModule.CHORUS_FRUIT_PIE.get(), "[servings=4]")
				.tag('C', CoreModule.CANDLES, "[lit=true]", () -> Blocks.CANDLE, "[lit=true]")
				.blockstate('H', () -> Blocks.DRAGON_WALL_HEAD, "[facing=south]")
				.build(false);

		JsonObject ordered = new JsonObject();
		ordered.add("type", json.remove("type"));
		ordered.add("mapping", json.remove("mapping"));
		ordered.add("pattern", json.remove("pattern"));
		ordered.getAsJsonObject("mapping").getAsJsonObject("C").remove("display");
		add(modLoc("ritual"), ordered);
	}
}