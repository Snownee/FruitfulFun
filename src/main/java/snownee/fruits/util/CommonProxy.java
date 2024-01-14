package snownee.fruits.util;

import java.util.List;
import java.util.Objects;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import snownee.fruits.CoreFruitTypes;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.GeneticData;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.compat.curios.CuriosCompat;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.Util;

@Mod(FruitfulFun.ID)
public class CommonProxy {
	public CommonProxy() {
		CoreFruitTypes.APPLE.getOrCreate();
	}

	public static void init() {
		addFeature("citron");
		addFeature("tangerine");
		addFeature("lime");

		MinecraftForge.EVENT_BUS.addListener((WandererTradesEvent event) -> {
			if (!FFCommonConfig.wanderingTraderSapling) {
				return;
			}
			List<VillagerTrades.ItemListing> trades = event.getGenericTrades();
			trades.add((entity, random) -> {
				ItemStack sapling = net.minecraft.Util.getRandom(FFRegistries.FRUIT_TYPE.stream().filter($ -> $.tier == 0).map($ -> $.sapling.get()).toList(), random).asItem().getDefaultInstance();
				ItemStack emeralds = new ItemStack(Items.EMERALD, 8);
				return new MerchantOffer(emeralds, sapling, 5, 1, 1);
			});
		});

		MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
			MinecraftServer server = event.getServer();
			ServerLevel world = server.overworld();
			long seed = world.getSeed();
			GeneticData geneticData = world.getDataStorage().computeIfAbsent(GeneticData::load, GeneticData::new, "fruitfulfun_genetics");
			geneticData.initAlleles(seed);
		});

//		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
//		eventBus.addListener((AddPackFindersEvent event) -> {
//			event.addRepositorySource();
//		});

		if (Platform.isPhysicalClient()) {
			ClientProxy.init();
		}

		if (Hooks.curios) {
			CuriosCompat.init();
		}
	}

	public static boolean isCurativeItem(MobEffectInstance effectInstance, ItemStack stack) {
		return effectInstance.isCurativeItem(stack);
	}

	public static boolean isFakePlayer(Entity entity) {
		return entity instanceof FakePlayer;
	}

	public static Packet<ClientGamePacketListener> getAddEntityPacket(Entity entity) {
		return NetworkHooks.getEntitySpawningPacket(entity);
	}

	public static void maybeGrowCrops(ServerLevel world, BlockPos pos, BlockState state, boolean defaultResult, Runnable defaultAction) {
		if (ForgeHooks.onCropsGrowPre(world, pos, state, defaultResult)) {
			defaultAction.run();
			ForgeHooks.onCropsGrowPost(world, pos, state);
		}
	}

	public static void addBuiltinPacks() {
		init();
		ModContainer modContainer = FabricLoader.getInstance().getModContainer(FruitfulFun.ID).orElseThrow();
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
		return stack.canPerformAction(ToolActions.SHEARS_HARVEST);
	}

	public static ItemStack getRecipeRemainder(ItemStack itemStack) {
		return itemStack.getCraftingRemainingItem();
	}

	public static void initBeeModule() {
		// map in StatType is an IdentityHashMap, update the reference
		BeeModule.BEE_ONE_CM = Stats.makeCustomStat(BeeModule.BEE_ONE_CM.toString(), StatFormatter.DISTANCE);
		BeeModule.BEES_BRED = Stats.makeCustomStat(BeeModule.BEES_BRED.toString(), StatFormatter.DEFAULT);
	}

	public static void addFeature(String id) {
		ResourceKey<PlacedFeature> key = PlacementUtils.createKey(Objects.requireNonNull(Util.RL(id, FruitfulFun.ID)).toString());
		BiomeModifications.addFeature(context -> {
			return context.hasTag(ConventionalBiomeTags.TREE_DECIDUOUS) || context.hasTag(ConventionalBiomeTags.TREE_JUNGLE) || context.hasFeature(VegetationFeatures.TREES_PLAINS);
		}, GenerationStep.Decoration.VEGETAL_DECORATION, key);
	}

	public static FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (stack.getItem() instanceof FlowerCrownItem item) {
			return item;
		}
		if (Hooks.curios) {
			return CuriosCompat.getFlowerCrown(entity);
		}
		return null;
	}
}
