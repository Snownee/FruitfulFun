package snownee.fruits.market;

import java.util.List;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule(value = "market", modId = FruitfulFun.ID, dependencies = "@core")
@KiwiModule.Optional
public class MarketModule extends AbstractModule {
	@KiwiModule.Category(value = Categories.FUNCTIONAL_BLOCKS)
	public static final BlockObject<MarketBlock> MARKET = block(MarketBlock::new, () -> Blocks.BARREL);
	@KiwiModule.Name("market")
	public static final KiwiGO<BlockEntityType<MarketBlockEntity>> MARKET_ENTITY = blockEntity(
			MarketBlockEntity::new,
			MarketBlock.class);
	public static final KiwiGO<MenuType<MarketMenu>> MARKET_MENU = go(() -> new MenuType<>(
			MarketMenu::new,
			FeatureFlags.VANILLA_SET));
	public static final KiwiGO<DataComponentType<Long>> MARKET_MONEY = go(
			() -> DataComponentType.<Long>builder()
					.persistent(Codec.LONG)
					.networkSynchronized(ByteBufCodecs.LONG)
					.build(),
			Registries.DATA_COMPONENT_TYPE);
	public static final KiwiGO<DataComponentType<List<ItemStack>>> MARKET_ORDERS = go(
			() -> DataComponentType.<List<ItemStack>>builder()
					.persistent(ItemStack.OPTIONAL_CODEC.listOf())
					.networkSynchronized(ItemStack.OPTIONAL_LIST_STREAM_CODEC)
					.build(),
			Registries.DATA_COMPONENT_TYPE);

	public MarketModule() {
		Hooks.market = true;
		DynamicRegistries.registerSynced(FFRegistries.MARKET_PRICE_KEY, MarketPrice.CODEC, MarketPrice.CODEC);
	}
}
