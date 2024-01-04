package snownee.fruits.util;

import java.util.Objects;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.GeneticData;
import snownee.kiwi.Mod;
import snownee.kiwi.util.Util;

@Mod(FruitfulFun.ID)
public class CommonProxy implements ModInitializer {
	public static boolean isCurativeItem(MobEffectInstance effectInstance, ItemStack stack) {
		return stack.is(Items.MILK_BUCKET);
	}

	public static boolean isFakePlayer(Entity entity) {
		return entity instanceof FakePlayer;
	}

	public static Packet<ClientGamePacketListener> getAddEntityPacket(Entity entity) {
		return new ClientboundAddEntityPacket(entity);
	}

	public static void maybeGrowCrops(ServerLevel world, BlockPos pos, BlockState state, boolean defaultResult, Runnable defaultAction) {
		if (defaultResult) {
			defaultAction.run();
		}
	}

	public static void addBuiltinPacks() {
		ModContainer modContainer = FabricLoader.getInstance().getModContainer(FruitfulFun.ID).orElseThrow();
		if (Hooks.cherry) {
			addBuiltinPack(modContainer, "cherry");
		}
		if (Hooks.food) {
			addBuiltinPack(modContainer, "food");
		}
		if (Hooks.farmersdelight) {
			addBuiltinPack(modContainer, "farmersdelight");
		}
		if (FFCommonConfig.villageAppleTreeWorldGen) {
			addBuiltinPack(modContainer, "apple_tree_in_village");
		}
	}

	private static void addBuiltinPack(ModContainer modContainer, String id) {
		ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(FruitfulFun.ID, id), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
	}

	public static boolean isShears(ItemStack stack) {
		return stack.is(ConventionalItemTags.SHEARS);
	}

	@Override
	public void onInitialize() {
		addFeature("citron");
		addFeature("tangerine");
		addFeature("lime");
		TradeOfferHelper.registerWanderingTraderOffers(1, trades -> {
			if (!FFCommonConfig.wanderingTraderSapling) {
				return;
			}
			trades.add((entity, random) -> {
				ItemStack sapling = net.minecraft.Util.getRandom(FFRegistries.FRUIT_TYPE.stream()
						.filter($ -> $.tier == 0)
						.map($ -> $.sapling.get())
						.toList(), random).asItem().getDefaultInstance();
				ItemStack emeralds = new ItemStack(Items.EMERALD, 8);
				return new MerchantOffer(emeralds, sapling, 5, 1, 1);
			});
		});
		// map in StatType is an IdentityHashMap, update the reference
		BeeModule.BEE_ONE_CM = Stats.makeCustomStat(BeeModule.BEE_ONE_CM.toString(), StatFormatter.DISTANCE);
		BeeModule.BEES_BRED = Stats.makeCustomStat(BeeModule.BEES_BRED.toString(), StatFormatter.DEFAULT);

		ServerWorldEvents.LOAD.register((server, world) -> {
			if (world == server.overworld()) {
				long seed = world.getSeed();
				GeneticData geneticData = world.getDataStorage().computeIfAbsent(GeneticData::load, GeneticData::new, "fruitfulfun_genetics");
				geneticData.initAlleles(seed);
			}
		});
	}

	public static void addFeature(String id) {
		ResourceKey<PlacedFeature> key = PlacementUtils.createKey(Objects.requireNonNull(Util.RL(id, FruitfulFun.ID)).toString());
		BiomeModifications.addFeature(context -> {
			return context.hasTag(ConventionalBiomeTags.TREE_DECIDUOUS) || context.hasTag(ConventionalBiomeTags.TREE_JUNGLE) || context.hasFeature(VegetationFeatures.TREES_PLAINS);
		}, GenerationStep.Decoration.VEGETAL_DECORATION, key);
	}
}
