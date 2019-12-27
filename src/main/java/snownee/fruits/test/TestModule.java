package snownee.fruits.test;

import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import snownee.fruits.Fruits;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.block.trees.FruitTree;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Group;
import snownee.kiwi.RenderLayer;
import snownee.kiwi.RenderLayer.Layer;
import snownee.kiwi.item.ModItem;

@KiwiModule(name = "test")
@KiwiModule.Optional(disabledByDefault = true)
public class TestModule extends AbstractModule {

    @Group("building_blocks")
    public static final LogBlock CHERRY_LOG = new LogBlock(MaterialColor.DIRT, blockProp(Blocks.OAK_LOG));

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final LeavesBlock CHERRY_LEAVES = new FruitLeavesBlock(() -> FruitTypeExtension.CHERRY, blockProp(Blocks.OAK_LEAVES));

    @Group("decorations")
    @RenderLayer(Layer.CUTOUT)
    public static final SaplingBlock CHERRY_SAPLING = new SaplingBlock(new FruitTree(() -> FruitTypeExtension.CHERRY), blockProp(Blocks.OAK_SAPLING));

    public static final Item CHERRY = new ModItem(itemProp().group(ItemGroup.FOOD));

    static {
        FruitTypeExtension.CHERRY = Fruits.Type.create("cherry", CHERRY_LOG, CHERRY_LEAVES, () -> CHERRY_SAPLING, CHERRY);
    }

}
