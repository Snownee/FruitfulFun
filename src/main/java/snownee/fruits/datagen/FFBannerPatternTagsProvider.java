package snownee.fruits.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.cherry.CherryModule;

public class FFBannerPatternTagsProvider extends FabricTagsProvider<BannerPattern> {
	public FFBannerPatternTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.BANNER_PATTERN, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		getOrCreateRawBuilder(CoreModule.SNOWFLAKE_TAG).addOptionalElement(FruitfulFun.id("snowflake"));
		getOrCreateRawBuilder(CherryModule.HEART_TAG).addOptionalElement(FruitfulFun.id("heart"));
	}
}
