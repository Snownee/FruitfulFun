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

    MANDARIN(MainModule.CITRUS_LOG, MainModule.MANDARIN_LEAVES, () -> MainModule.MANDARIN_SAPLING, MainModule.MANDARIN),
    LIME(MainModule.CITRUS_LOG, MainModule.LIME_LEAVES, () -> MainModule.LIME_SAPLING, MainModule.LIME),
    CITRON(MainModule.CITRUS_LOG, MainModule.CITRON_LEAVES, () -> MainModule.CITRON_SAPLING, MainModule.CITRON),
    POMELO(MainModule.CITRUS_LOG, MainModule.POMELO_LEAVES, () -> MainModule.POMELO_SAPLING, MainModule.POMELO),
    ORANGE(MainModule.CITRUS_LOG, MainModule.ORANGE_LEAVES, () -> MainModule.ORANGE_SAPLING, MainModule.ORANGE),
    LEMON(MainModule.CITRUS_LOG, MainModule.LEMON_LEAVES, () -> MainModule.LEMON_SAPLING, MainModule.LEMON),
    GRAPEFRUIT(MainModule.CITRUS_LOG, MainModule.GRAPEFRUIT_LEAVES, () -> MainModule.GRAPEFRUIT_SAPLING, MainModule.GRAPEFRUIT),
    APPLE(Blocks.OAK_LOG, MainModule.APPLE_LEAVES, () -> MainModule.APPLE_SAPLING, Items.APPLE);

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
