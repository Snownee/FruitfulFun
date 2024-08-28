package snownee.fruits.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FFRegistries;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.genetics.GeneticSavedData;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.command.FFCommands;
import snownee.fruits.compat.farmersdelight.FarmersDelightModule;
import snownee.fruits.compat.trinkets.TrinketsCompat;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.vacuum.VacGunItem;
import snownee.fruits.vacuum.VacModule;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Mod;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.Util;

@Mod(FruitfulFun.ID)
public class CommonProxy implements ModInitializer {
	public static final UUID FAKE_PLAYER_UUID = UUID.fromString("ae5efe90-eef0-4899-94fc-de4786c242e8");
	private static final GameProfile FAKE_PLAYER_PROFILE = new GameProfile(FAKE_PLAYER_UUID, "[FruitfulFun]");
	private static final TagKey<Item> KNIVES = AbstractModule.itemTag("c", "tools/knives");

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
		ResourceManagerHelper.registerBuiltinResourcePack(
				new ResourceLocation(FruitfulFun.ID, id), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
	}

	public static boolean isShears(ItemStack stack) {
		return stack.is(ConventionalItemTags.SHEARS);
	}

	public static boolean isBookshelf(BlockState blockState) {
		return blockState.is(ConventionalBlockTags.BOOKSHELVES);
	}

	@SuppressWarnings("UnstableApiUsage")
	public static boolean insertItem(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			@Nullable BlockEntity blockEntity,
			Direction direction,
			ItemStack item) {
		Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, blockPos, blockState, blockEntity, direction);
		if (storage == null || !storage.supportsInsertion()) {
			return false;
		}
		boolean success = false;
		try (Transaction tx = Transaction.openOuter()) {
			long inserted = storage.insert(ItemVariant.of(item), item.getCount(), tx);
			if (inserted > 0) {
				tx.commit();
				item.shrink((int) inserted);
				success = true;
			}
		}
		return success;
	}

	@SuppressWarnings("UnstableApiUsage")
	public static ItemStack extractOneItem(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			@Nullable BlockEntity blockEntity,
			Direction direction) {
		Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, blockPos, blockState, blockEntity, direction);
		if (storage == null || !storage.supportsExtraction()) {
			return ItemStack.EMPTY;
		}
		VacGunItem.playContainerAnimation(blockEntity);
		Iterator<StorageView<ItemVariant>> iterator = storage.nonEmptyIterator();
		if (!iterator.hasNext()) {
			return ItemStack.EMPTY;
		}
		ItemStack result = ItemStack.EMPTY;
		try (Transaction tx = Transaction.openOuter()) {
			ItemVariant resource = iterator.next().getResource();
			long extracted = storage.extract(resource, 1, tx);
			if (extracted > 0) {
				tx.commit();
				result = resource.toStack();
			}
		}
		return result;
	}

	public static ServerPlayer getFakePlayer(Level level) {
		return FakePlayer.get((ServerLevel) level, FAKE_PLAYER_PROFILE);
	}

	public static boolean isKnife(ItemStack itemStack) {
		return itemStack.is(KNIVES);
	}

	public static boolean isBeehive(ItemStack itemStack) {
		return itemStack.is(Items.BEEHIVE) || itemStack.is(Items.BEE_NEST) || Block.byItem(itemStack.getItem()).defaultBlockState().is(
				BlockTags.BEEHIVES);
	}

	@Override
	public void onInitialize() {
		addFeature("citron");
		addFeature("tangerine");
		addFeature("lime");
		TradeOfferHelper.registerWanderingTraderOffers(1, trades -> {
			if (FFCommonConfig.wanderingTraderSaplingPrice == 0) {
				return;
			}
			trades.add((entity, random) -> {
				ItemStack sapling = net.minecraft.Util.getRandom(
								FFRegistries.FRUIT_TYPE.stream().filter($ -> $.tier == 0).map($ -> $.sapling.get()).toList(), random)
						.asItem()
						.getDefaultInstance();
				ItemStack emeralds = new ItemStack(Items.EMERALD, FFCommonConfig.wanderingTraderSaplingPrice);
				return new MerchantOffer(emeralds, sapling, 5, 1, 1);
			});
		});

		ServerWorldEvents.LOAD.register((server, world) -> {
			if (world == server.overworld()) {
				long seed = world.getSeed();
				GeneticSavedData data = world.getDataStorage()
						.computeIfAbsent(GeneticSavedData::load, GeneticSavedData::new, "fruitfulfun_genetics");
				data.initAlleles(seed);
			}
		});

		if (Platform.isModLoaded("leaves_us_in_peace")) {
			ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
				if (FFCommonConfig.leavesUsInPeaceIncompatibilityNotified || isFakePlayer(handler.getPlayer())) {
					return;
				}
				MutableComponent msg = Component.translatable("tip.fruitfulfun.leavesUsInPeace");
				server.sendSystemMessage(msg);
				handler.getPlayer().sendSystemMessage(msg);
				FFCommonConfig.leavesUsInPeaceIncompatibilityNotified = true;
				KiwiConfigManager.getHandler(FFCommonConfig.class).save();
			});
		}

		if (!Platform.isProduction()) {
			CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
				dispatcher.register(FFCommands.register());
			});
		}
	}

	public static ItemStack getRecipeRemainder(ItemStack itemStack) {
		return itemStack.getRecipeRemainder();
	}

	public static void initBeeModule() {
		// map in StatType is an IdentityHashMap, update the reference
		BeeModule.BEE_ONE_CM = Stats.makeCustomStat(BeeModule.BEE_ONE_CM.toString(), StatFormatter.DISTANCE);
		BeeModule.BEES_BRED = Stats.makeCustomStat(BeeModule.BEES_BRED.toString(), StatFormatter.DEFAULT);

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			Map<String, FFPlayer.GeneName> map = FFPlayer.of(oldPlayer).fruits$getGeneNames();
			FFPlayer.of(newPlayer).fruits$setGeneNames(map);
		});
	}

	public static void initVacModule() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (VacModule.VAC_GUN.is(player.getItemInHand(hand))) {
				player.startUsingItem(hand);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		});
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if (VacModule.VAC_GUN.is(player.getItemInHand(hand))) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (VacModule.VAC_GUN.is(player.getItemInHand(hand))) {
				player.startUsingItem(hand);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.PASS;
		});
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (VacModule.VAC_GUN.is(player.getItemInHand(hand))) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
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
		if (Hooks.trinkets) {
			return TrinketsCompat.getFlowerCrown(entity);
		}
		return null;
	}

	public static boolean isLitCandle(BlockState blockState) {
		return blockState.hasProperty(AbstractCandleBlock.LIT)
				&& blockState.getValue(AbstractCandleBlock.LIT)
				&& blockState.is(CoreModule.CANDLES);
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
}
