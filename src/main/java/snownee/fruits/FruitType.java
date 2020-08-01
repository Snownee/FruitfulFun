package snownee.fruits;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.common.IPlantable;

public enum FruitType implements IExtensibleEnum {
    MANDARIN(CoreModule.CITRUS_LOG, CoreModule.MANDARIN_LEAVES, () -> CoreModule.MANDARIN_SAPLING, CoreModule.MANDARIN),
    LIME(CoreModule.CITRUS_LOG, CoreModule.LIME_LEAVES, () -> CoreModule.LIME_SAPLING, CoreModule.LIME),
    CITRON(CoreModule.CITRUS_LOG, CoreModule.CITRON_LEAVES, () -> CoreModule.CITRON_SAPLING, CoreModule.CITRON),
    POMELO(CoreModule.CITRUS_LOG, CoreModule.POMELO_LEAVES, () -> CoreModule.POMELO_SAPLING, CoreModule.POMELO),
    ORANGE(CoreModule.CITRUS_LOG, CoreModule.ORANGE_LEAVES, () -> CoreModule.ORANGE_SAPLING, CoreModule.ORANGE),
    LEMON(CoreModule.CITRUS_LOG, CoreModule.LEMON_LEAVES, () -> CoreModule.LEMON_SAPLING, CoreModule.LEMON),
    GRAPEFRUIT(CoreModule.CITRUS_LOG, CoreModule.GRAPEFRUIT_LEAVES, () -> CoreModule.GRAPEFRUIT_SAPLING, CoreModule.GRAPEFRUIT),
    APPLE(Blocks.OAK_LOG, CoreModule.APPLE_LEAVES, () -> CoreModule.APPLE_SAPLING, Items.APPLE);

    public final Block log;
    public final LeavesBlock leaves;
    public final Supplier<SaplingBlock> sapling;
    public final Item fruit;

    private FruitType(Block log, LeavesBlock leaves, Supplier<SaplingBlock> sapling, Item fruit) {
        this.log = log;
        this.leaves = leaves;
        this.sapling = sapling;
        this.fruit = fruit;
    }

    public static FruitType create(String name, Block log, LeavesBlock leaves, Supplier<IPlantable> sapling, Item fruit) {
        throw new IllegalStateException("Enum not extended");
    }

    public static FruitType parse(String name) {
        try {
            return valueOf(name);
        } catch (Exception e) {
            return CITRON;
        }
    }
}
