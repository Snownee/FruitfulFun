package snownee.fruits.datagen.guide;

import com.klikli_dev.modonomicon.api.datagen.MultiblockProvider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;

public class RitualMultiblockProvider extends MultiblockProvider {

	public RitualMultiblockProvider(PackOutput packOutput) {
		super(packOutput, FruitfulFun.ID);
	}

	@Override
	public void buildMultiblocks() {
		add(modLoc("ritual"), new DenseMultiblockBuilder()
				// 顶层：四角蜡烛 + 一个龙首（朝南，指向中央）
				.layer(
						"____H____",
						"_________",
						"__C___C__",
						"_________",
						"_________",
						"_________",
						"__C___C__",
						"_________",
						"_________")
				// 底层：八根蜡烛围成菱形环，中央留空（放紫颂果派）
				.layer(
						"_________",
						"_________",
						"___C_C___",
						"__C___C__",
						"____0____",
						"__C___C__",
						"___C_C___",
						"_________",
						"_________")
				.tag('C', CoreModule.CANDLES, "[lit=true]", () -> Blocks.CANDLE, "[lit=true]")
				.any('0')
				.blockstate('H', () -> Blocks.DRAGON_HEAD, "[rotation=4]"));
	}
}