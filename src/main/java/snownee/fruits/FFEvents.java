/*
package snownee.fruits;

import java.util.function.Consumer;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public final class FFEvents {

	public static void addPackFinder(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			event.addRepositorySource((Consumer<Pack> consumer, Pack.PackConstructor constructor) -> {
				PackMetadataSection section = new PackMetadataSection(Component.literal("Fruitful Fun Conditional Resources"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
				consumer.accept(constructor.create("mod:fruitfulfun:conditional", Component.literal(FruitfulFun.NAME), true, () -> new FFConditionalPackResources(section), section, Position.TOP, PackSource.DEFAULT, true));
			});
		}
	}

}
*/
