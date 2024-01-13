//package snownee.fruits.datagen;
//
//import java.util.Optional;
//
//import net.minecraft.data.models.model.ModelTemplate;
//import net.minecraft.data.models.model.TextureSlot;
//import net.minecraft.resources.ResourceLocation;
//import snownee.fruits.FruitfulFun;
//
//public class FFModelTemplates {
//	public static final TextureSlot LEAVES = TextureSlot.create("leaves");
//	public static final TextureSlot FLOWERS = TextureSlot.create("flowers");
//	public static final ModelTemplate FLOWERING_LEAVES = create("template_leaves_flowering", "_2", LEAVES, FLOWERS);
//	public static final ModelTemplate FRUIT_LG_LEAVES = create("template_leaves_fruit_lg", "_3", LEAVES);
//	public static final ModelTemplate FRUIT_MD_LEAVES = create("template_leaves_fruit_md", "_3", LEAVES);
//	public static final ModelTemplate FRUIT_SM_LEAVES = create("template_leaves_fruit_sm", "_3", LEAVES);
//
//	private static ModelTemplate create(TextureSlot... textureSlots) {
//		return new ModelTemplate(Optional.empty(), Optional.empty(), textureSlots);
//	}
//
//	private static ModelTemplate create(String string, TextureSlot... textureSlots) {
//		return new ModelTemplate(Optional.of(new ResourceLocation(FruitfulFun.ID, "block/" + string)), Optional.empty(), textureSlots);
//	}
//
//	private static ModelTemplate createItem(String string, TextureSlot... textureSlots) {
//		return new ModelTemplate(Optional.of(new ResourceLocation(FruitfulFun.ID, "item/" + string)), Optional.empty(), textureSlots);
//	}
//
//	private static ModelTemplate create(String string, String string2, TextureSlot... textureSlots) {
//		return new ModelTemplate(Optional.of(new ResourceLocation(FruitfulFun.ID, "block/" + string)), Optional.of(string2), textureSlots);
//	}
//}
