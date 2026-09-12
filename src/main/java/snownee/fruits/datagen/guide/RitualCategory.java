package snownee.fruits.datagen.guide;

import static snownee.fruits.datagen.guide.GuideUtil.lines;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.IndexModeCategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.IndexModeEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;

import net.minecraft.world.item.Items;
import snownee.fruits.food.FoodModule;

public class RitualCategory extends IndexModeCategoryProvider {

	public RitualCategory(SingleBookSubProvider parent) {
		super(parent);
	}

	@Override
	protected void generateEntries() {
		add(new DragonRitualEntry(this).generate());
		add(new RitualRecipesEntry(this).generate());
	}

	@Override
	protected String categoryName() {
		return "仪式";
	}

	@Override
	protected String categoryDescription() {
		return lines("用龙首与烛光举行古老的仪式。");
	}

	@Override
	protected BookIconModel categoryIcon() {
		return BookIconModel.create(Items.DRAGON_HEAD);
	}

	@Override
	public String categoryId() {
		return "ritual";
	}

	public static class DragonRitualEntry extends IndexModeEntryProvider {

		public DragonRitualEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("搭建阵型");
			pageText(lines("""
					在平坦的地面上摆好阵型：
					中央放一块完整的紫颂果派
					一个（或多个）龙首朝向中央
					四周按图摆放点燃的蜡烛

					支撑方块是什么并不重要。

					把物品投入中央，仪式会将其转化为另一种物品。

					龙首越多，产出的龙息越多。仪式进行时会生成危险的龙息，当心别靠太近！
					"""));
			page("multiblock", () -> BookMultiblockPageModel.create()
					.withMultiblockId(modLoc("ritual"))
					.withText(context().pageText()));
			pageText(lines("图中展示了完整的仪式结构。"));
		}

		@Override
		protected String entryName() {
			return "龙之仪式";
		}

		@Override
		protected String entryDescription() {
			return "搭建阵型，转化物品。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(FoodModule.CHORUS_FRUIT_PIE.get());
		}

		@Override
		protected String entryId() {
			return "dragon_ritual";
		}
	}

	public static class RitualRecipesEntry extends IndexModeEntryProvider {

		public RitualRecipesEntry(CategoryProviderBase parent) {
			super(parent);
		}

		@Override
		protected void generatePages() {
			page("intro", () -> BookTextPageModel.create()
					.withTitle(context().pageTitle())
					.withText(context().pageText()));
			pageTitle("已知的配方");
			pageText(lines("""
					仪式可以把普通物品转化为稀有之物。以下是一些已为人知的配方：

					玻璃瓶 → 龙息
					酿造台 → 酿造机
					阳光探测器 → 雨探测器
					石榴 → 附魔石榴
					蜂箱（夜晚的苍白花园）→ 鬼魂蜂

					还有更多配方等待你去发现。
					"""));
		}

		@Override
		protected String entryName() {
			return "仪式配方";
		}

		@Override
		protected String entryDescription() {
			return "已知的仪式配方一览。";
		}

		@Override
		protected BookIconModel entryIcon() {
			return BookIconModel.create(Items.DRAGON_BREATH);
		}

		@Override
		protected String entryId() {
			return "ritual_recipes";
		}
	}
}