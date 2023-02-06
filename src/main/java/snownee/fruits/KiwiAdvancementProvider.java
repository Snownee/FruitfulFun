package snownee.fruits;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

//TODO: 1.19.3: move to kiwi
public class KiwiAdvancementProvider extends AdvancementProvider {

	protected final ResourceLocation id;
	private final List<Consumer<Consumer<Advancement>>> myTabs = Lists.newArrayList();

	public KiwiAdvancementProvider(ResourceLocation id, DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
		super(generatorIn, fileHelperIn);
		this.id = id;
	}

	@Override
	protected void registerAdvancements(Consumer<Advancement> consumer, net.minecraftforge.common.data.ExistingFileHelper fileHelper) {
		for (Consumer<Consumer<Advancement>> consumer1 : myTabs) {
			consumer1.accept(consumer);
		}
	}

	@Override
	public String getName() {
		return "Advancements - " + id;
	}

	public KiwiAdvancementProvider add(Consumer<Consumer<Advancement>> tab) {
		myTabs.add(tab);
		return this;
	}

}
