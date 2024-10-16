package snownee.fruits.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import snownee.fruits.CoreFruitTypes;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.HauntingManager;
import snownee.fruits.bee.genetics.GeneticSavedData;
import snownee.fruits.bee.genetics.Trait;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.command.FFCommands;
import snownee.fruits.compat.curios.CuriosCompat;
import snownee.fruits.compat.farmersdelight.FarmersDelightModule;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.ritual.BeehiveIngredient;
import snownee.fruits.vacuum.VacGunItem;
import snownee.fruits.vacuum.VacModule;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.Util;

@Mod(FruitfulFun.ID)
public class CommonProxy {
	private static final TagKey<Item> KNIVES = AbstractModule.itemTag("farmersdelight", "tools/knives");

	public CommonProxy() {
		CoreFruitTypes.APPLE.getOrCreate();
	}

	public static void init() {
		addFeature("citron");
		addFeature("tangerine");
		addFeature("lime");

		MinecraftForge.EVENT_BUS.addListener((WandererTradesEvent event) -> {
			if (FFCommonConfig.wanderingTraderSaplingPrice == 0) {
				return;
			}
			List<VillagerTrades.ItemListing> trades = event.getGenericTrades();
			trades.add((entity, random) -> {
				ItemStack sapling = net.minecraft.Util.getRandom(FFRegistries.FRUIT_TYPE.stream()
						.filter($ -> $.tier == 0)
						.map($ -> $.sapling.get())
						.toList(), random).asItem().getDefaultInstance();
				ItemStack emeralds = new ItemStack(Items.EMERALD, FFCommonConfig.wanderingTraderSaplingPrice);
				return new MerchantOffer(emeralds, sapling, 5, 1, 1);
			});
		});

		MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
			MinecraftServer server = event.getServer();
			ServerLevel world = server.overworld();
			long seed = world.getSeed();
			GeneticSavedData geneticData = world.getDataStorage().computeIfAbsent(
					GeneticSavedData::load,
					GeneticSavedData::new,
					"fruitfulfun_genetics");
			geneticData.initAlleles(seed);
		});

		if (Platform.isModLoaded("leaves_us_in_peace")) {
			MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
				Player player = event.getEntity();
				if (FFCommonConfig.leavesUsInPeaceIncompatibilityNotified || isFakePlayer(player)) {
					return;
				}
				MutableComponent msg = Component.translatable("tip.fruitfulfun.leavesUsInPeace");
				if (player.getServer() != null) {
					player.getServer().sendSystemMessage(msg);
				}
				player.sendSystemMessage(msg);
				FFCommonConfig.leavesUsInPeaceIncompatibilityNotified = true;
				KiwiConfigManager.getHandler(FFCommonConfig.class).save();
			});
		}

		if (!Platform.isProduction()) {
			MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
				event.getDispatcher().register(FFCommands.register());
			});
		}

		if (Platform.isPhysicalClient()) {
			ClientProxy.init();
		}

		if (Hooks.curios) {
			CuriosCompat.init();
		}

		CustomIngredientSerializer.register(BeehiveIngredient.SERIALIZER);
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
			String mode = FarmersDelightModule.getMode();
			if ("vectorwing".equals(mode)) {
				addBuiltinPack(modContainer, "farmersdelight");
			} else {
				addBuiltinPack(modContainer, "farmersdelight_" + mode);
			}
		}
		if (FFCommonConfig.villageAppleTreeWorldGen) {
			addBuiltinPack(modContainer, "apple_tree_in_village");
		}
	}

	private static void addBuiltinPack(ModContainer modContainer, String id) {
		ResourceManagerHelper.registerBuiltinResourcePack(FruitfulFun.id(id), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
	}

	public static boolean isShears(ItemStack stack) {
		return stack.canPerformAction(ToolActions.SHEARS_HARVEST);
	}

	public static boolean isBookshelf(BlockState blockState) {
		return blockState.is(Tags.Blocks.BOOKSHELVES);
	}

	public static boolean insertItem(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			@Nullable BlockEntity blockEntity,
			Direction direction,
			ItemStack item) {
		if (item.isEmpty()) {
			return false;
		}
		LazyOptional<IItemHandler> cap;
		if (blockEntity != null && (cap = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction)).isPresent()) {
			IItemHandler itemHandler = cap.orElseThrow(NullPointerException::new);
			ItemStack ret = ItemHandlerHelper.insertItem(itemHandler, item.copy(), false);
			if (ret.getCount() == item.getCount()) {
				return false;
			}
			item.setCount(ret.getCount());
			return true;
		}
		if (blockState.getBlock() instanceof WorldlyContainerHolder containerHolder) {
			WorldlyContainer container = containerHolder.getContainer(blockState, level, blockPos);
			//noinspection ConstantValue
			if (container == null) {
				return false;
			}
			ItemStack ret = HopperBlockEntity.addItem(null, container, item, direction);
			if (ret.getCount() == item.getCount()) {
				return false;
			}
			item.setCount(ret.getCount());
			return true;
		}
		return false;
	}

	public static ItemStack extractOneItem(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			@Nullable BlockEntity blockEntity,
			Direction direction) {
		LazyOptional<IItemHandler> cap;
		if (blockEntity != null && (cap = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction)).isPresent()) {
			VacGunItem.playContainerAnimation(blockEntity);
			IItemHandler itemHandler = cap.orElseThrow(NullPointerException::new);
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stack = itemHandler.extractItem(i, 1, false);
				if (!stack.isEmpty()) {
					return stack;
				}
			}
		}
		if (blockState.getBlock() instanceof WorldlyContainerHolder containerHolder) {
			WorldlyContainer container = containerHolder.getContainer(blockState, level, blockPos);
			//noinspection ConstantValue
			if (container == null || container.isEmpty()) {
				return ItemStack.EMPTY;
			}
			for (int slot : container.getSlotsForFace(direction)) {
				ItemStack itemStack = container.getItem(slot);
				if (itemStack.isEmpty()) {
					continue;
				}
				itemStack = itemStack.copyWithCount(1);
				if (!container.canTakeItemThroughFace(slot, itemStack, direction)) {
					continue;
				}
				itemStack = container.removeItem(slot, 1);
				if (!itemStack.isEmpty()) {
					container.setChanged();
					return itemStack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getRecipeRemainder(ItemStack itemStack) {
		return itemStack.getCraftingRemainingItem();
	}

	public static void initBeeModule() {
		// map in StatType is an IdentityHashMap, update the reference
		BeeModule.BEE_ONE_CM = Stats.makeCustomStat(BeeModule.BEE_ONE_CM.toString(), StatFormatter.DISTANCE);
		BeeModule.BEES_BRED = Stats.makeCustomStat(BeeModule.BEES_BRED.toString(), StatFormatter.DEFAULT);

		MinecraftForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
			Player oldPlayer = event.getOriginal();
			Player newPlayer = event.getEntity();
			Map<String, FFPlayer.GeneName> map = FFPlayer.of(oldPlayer).fruits$getGeneNames();
			FFPlayer.of(newPlayer).fruits$setGeneNames(map);
		});

		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.EntityInteract event) -> {
			Entity target = event.getTarget();
			Level level = event.getLevel();
			FFPlayer ffPlayer = (FFPlayer) event.getEntity();
			if (target instanceof LivingEntity && !target.getType().is(BeeModule.CANNOT_HAUNT) &&
					ffPlayer.fruits$hauntingTarget() instanceof Bee bee &&
					BeeAttributes.of(bee).hasTrait(Trait.GHOST)) {
				if (!level.isClientSide) {
					ffPlayer.fruits$setHauntingTarget(target);
					HauntingManager manager = ffPlayer.fruits$hauntingManager();
					if (manager != null) {
						manager.storeBee(bee);
					}
				}
				event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
				event.setCanceled(true);
			}
		});
	}

	public static void initVacModule() {
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock event) -> {
			if (VacModule.VAC_GUN.is(event.getItemStack())) {
				event.getEntity().startUsingItem(event.getHand());
				event.setCanceled(true);
			}
		});
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.LeftClickBlock event) -> {
			if (VacModule.VAC_GUN.is(event.getItemStack())) {
				event.setCanceled(true);
			}
		});
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.EntityInteract event) -> {
			if (VacModule.VAC_GUN.is(event.getItemStack())) {
				event.getEntity().startUsingItem(event.getHand());
				event.setCanceled(true);
			}
		});
		MinecraftForge.EVENT_BUS.addListener((AttackEntityEvent event) -> {
			if (VacModule.VAC_GUN.is(event.getEntity().getMainHandItem())) {
				event.setCanceled(true);
			}
		});
	}

	public static void addFeature(String id) {
		ResourceKey<PlacedFeature> key = PlacementUtils.createKey(Objects.requireNonNull(Util.RL(id, FruitfulFun.ID)).toString());
		BiomeModifications.addFeature(context -> {
			return context.hasTag(ConventionalBiomeTags.TREE_DECIDUOUS) || context.hasTag(ConventionalBiomeTags.TREE_JUNGLE) ||
					context.hasFeature(VegetationFeatures.TREES_PLAINS);
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

	public static boolean isLitCandle(BlockState blockState) {
		return blockState.hasProperty(AbstractCandleBlock.LIT) && blockState.getValue(AbstractCandleBlock.LIT) &&
				blockState.is(CoreModule.CANDLES);
	}

	public static void extinguishCandle(@Nullable Player player, BlockState blockState, LevelAccessor level, BlockPos blockPos) {
		if (blockState.getBlock() instanceof AbstractCandleBlock) {
			AbstractCandleBlock.extinguish(player, blockState, level, blockPos);
			return;
		}
		if (blockState.is(CoreModule.CANDLES) && blockState.getValue(AbstractCandleBlock.LIT)) {
			level.setBlock(blockPos, blockState.setValue(AbstractCandleBlock.LIT, false), 11);
			level.addParticle(ParticleTypes.SMOKE, blockPos.getX() + 0.5, blockPos.getY() + 0.9, blockPos.getZ() + 0.5, 0.0, 0.1, 0.0);
			level.playSound(null, blockPos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
			level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
		}
	}

	public static boolean isKnife(ItemStack itemStack) {
		return itemStack.is(KNIVES);
	}

	public static boolean isBeehive(ItemStack itemStack) {
		return itemStack.is(Items.BEEHIVE) || itemStack.is(Items.BEE_NEST) || Block.byItem(itemStack.getItem()).defaultBlockState().is(
				BlockTags.BEEHIVES);
	}
}
