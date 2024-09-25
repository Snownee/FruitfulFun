package snownee.fruits.datagen;

import java.util.Optional;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import snownee.fruits.FruitfulFun;

public class FFModelTemplates {
	public static final TextureSlot LEAVES = TextureSlot.create("leaves");
	public static final TextureSlot FLOWERS = TextureSlot.create("flowers");
	public static final ModelTemplate FLOWERING_LEAVES = create("template_leaves_flowering", "_flowers", FLOWERS);
	public static final ModelTemplate FLOWERING_INVENTORY = createItem("template_leaves_flowering", LEAVES, FLOWERS);

	private static ModelTemplate create(TextureSlot... textureSlots) {
		return new ModelTemplate(Optional.empty(), Optional.empty(), textureSlots);
	}

	private static ModelTemplate create(String string, TextureSlot... textureSlots) {
		return new ModelTemplate(Optional.of(FruitfulFun.id("block/" + string)), Optional.empty(), textureSlots);
	}

	private static ModelTemplate createItem(String string, TextureSlot... textureSlots) {
		return new ModelTemplate(Optional.of(FruitfulFun.id("item/" + string)), Optional.empty(), textureSlots);
	}

	private static ModelTemplate create(String string, String string2, TextureSlot... textureSlots) {
		return new ModelTemplate(Optional.of(FruitfulFun.id("block/" + string)), Optional.of(string2), textureSlots);
	}
}
