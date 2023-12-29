package snownee.fruits;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public final class FFEvents {

	public static void addPackFinder(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			event.addRepositorySource((Consumer<Pack> consumer, Pack.PackConstructor constructor) -> {
				PackMetadataSection section = new PackMetadataSection(Component.literal("Fruitful Fun Conditional Resources"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
				consumer.accept(constructor.create("mod:fruitfulfun:conditional", Component.literal(FruitfulFun.NAME), true, () -> new FruitsConditionalPackResources(section), section, Position.TOP, PackSource.DEFAULT, true));
			});
		}
	}

	@SubscribeEvent
	public static void addWandererTrades(WandererTradesEvent event) {
		event.getGenericTrades().add((entity, random) -> {
			/* off */
			List<Block> saplings = FruitType.REGISTRY.getValues().stream()
					.filter($ -> $.tier == 0)
					.map($ -> $.sapling.get())
					.filter(Objects::nonNull)
					.map(Block.class::cast)
					.toList();
			/* on */
			ItemStack sapling = new ItemStack(saplings.get(random.nextInt(saplings.size())));
			return new MerchantOffer(new ItemStack(Items.EMERALD, 10), sapling, 5, 1, 1);
		});
	}

}
