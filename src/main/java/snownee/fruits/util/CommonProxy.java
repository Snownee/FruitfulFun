package snownee.fruits.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
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
import snownee.fruits.cherry.CherryModule;
import snownee.fruits.cherry.item.FlowerCrownItem;
import snownee.fruits.command.FFCommands;
import snownee.fruits.compat.lychee.LycheeCompat;
import snownee.fruits.compat.trinkets.TrinketsCompat;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.ScentType;
import snownee.fruits.gadget.VacGunItem;
import snownee.fruits.ritual.BeehiveIngredient;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Kiwi;
import snownee.kiwi.KiwiModuleContainer;
import snownee.kiwi.KiwiModules;
import snownee.kiwi.Mod;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.KUtil;

@Mod(FruitfulFun.ID)
public class CommonProxy implements ModInitializer {
	private static final TagKey<Item> KNIVES = AbstractModule.itemTag("c", "tools/knives");
	private static final Map<ScentType, AttachmentType<Long>> SCENT_ATTACHMENT_TYPES = Maps.newHashMap();
	public static boolean trinkets = Platform.isModLoaded("trinkets_updated");

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
		if (Hooks.gadget) {
			addBuiltinPack(modContainer, "gadget");
		}
		if (Hooks.farmersdelight) {
			addBuiltinPack(modContainer, "farmersdelight");
		}
		if (FFCommonConfig.villageAppleTreeWorldGen) {
			addBuiltinPack(modContainer, "apple_tree_in_village");
		}
	}

	private static void addBuiltinPack(ModContainer modContainer, String id) {
		ResourceLoader.registerBuiltinPack(FruitfulFun.id(id), modContainer, PackActivationType.ALWAYS_ENABLED);
	}

	public static boolean isBookshelf(BlockState blockState) {
		return blockState.is(ConventionalBlockTags.BOOKSHELVES);
	}

	public static long insertItem(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			@Nullable BlockEntity blockEntity,
			Direction direction,
			ItemStack item) {
		Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, blockPos, blockState, blockEntity, direction);
		if (storage == null || !storage.supportsInsertion()) {
			return 0;
		}
		long inserted;
		try (Transaction tx = Transaction.openOuter()) {
			inserted = storage.insert(ItemVariant.of(item), item.getCount(), tx);
			if (inserted > 0) {
				tx.commit();
				item.shrink((int) inserted);
			}
		}
		return inserted;
	}

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

	public static boolean isKnife(ItemStack itemStack) {
		return itemStack.is(KNIVES);
	}

	public static boolean isBeehive(ItemStack itemStack) {
		return itemStack.is(Items.BEEHIVE) || itemStack.is(Items.BEE_NEST) || Block.byItem(itemStack.getItem()).defaultBlockState().is(
				BlockTags.BEEHIVES);
	}

	public static void setScentTime(ChunkAccess chunk, ScentType type, long time) {
		if (chunk instanceof EmptyLevelChunk) {
			return;
		}
		AttachmentType<Long> attachmentType = SCENT_ATTACHMENT_TYPES.get(type);
		if (attachmentType == null) {
			return;
		}
		if (time <= 0) {
			chunk.removeAttached(attachmentType);
		} else {
			Long attached = chunk.getAttached(attachmentType);
			if (attached != null && attached > time) {
				return;
			}
			chunk.setAttached(attachmentType, time);
		}
	}

	public static long getScentTime(ChunkAccess chunk, ScentType type) {
		AttachmentType<Long> attachmentType = SCENT_ATTACHMENT_TYPES.get(type);
		if (attachmentType == null) {
			return -1;
		}
		Long timeUntil = chunk.getAttached(attachmentType);
		if (timeUntil == null) {
			return -1;
		}
		return timeUntil;
	}

	@Override
	public void onInitialize() {
		Kiwi.onInitialize();
		addFeature("citron");
		addFeature("tangerine");
		addFeature("lime");

		BlockEntityType.SHELF.addValidBlock(CoreModule.CITRUS_SHELF.getOrCreate());
		BlockEntityType.SHELF.addValidBlock(CherryModule.REDLOVE_SHELF.getOrCreate());
		BlockEntityType.SIGN.addValidBlock(CoreModule.CITRUS_SIGN.getOrCreate());
		BlockEntityType.SIGN.addValidBlock(CherryModule.REDLOVE_SIGN.getOrCreate());
		BlockEntityType.HANGING_SIGN.addValidBlock(CoreModule.CITRUS_WALL_HANGING_SIGN.getOrCreate());
		BlockEntityType.HANGING_SIGN.addValidBlock(CherryModule.REDLOVE_WALL_HANGING_SIGN.getOrCreate());

		ServerLevelEvents.LOAD.register((server, world) -> {
			if (world == server.overworld()) {
				long seed = world.getSeed();
				GeneticSavedData data = world.getDataStorage().computeIfAbsent(GeneticSavedData.TYPE);
				data.initAlleles(seed);
			}
		});

		if (Platform.isModLoaded("brainierbees") || Platform.isModLoaded("leaves_us_in_peace")) {
			ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
				if (Platform.isFakePlayer(handler.getPlayer())) {
					return;
				}
				boolean save = false;
				if (Hooks.bee && Platform.isModLoaded("brainierbees") && !FFCommonConfig.leavesUsInPeaceIncompatibilityNotified) {
					MutableComponent msg = Component.translatable("tip.fruitfulfun.brainierBees");
					server.sendSystemMessage(msg);
					handler.getPlayer().sendSystemMessage(msg);
					save = FFCommonConfig.brainierBeesIncompatibilityNotified = true;
				}
				if (Platform.isModLoaded("leaves_us_in_peace") && !FFCommonConfig.leavesUsInPeaceIncompatibilityNotified) {
					MutableComponent msg = Component.translatable("tip.fruitfulfun.leavesUsInPeace");
					server.sendSystemMessage(msg);
					handler.getPlayer().sendSystemMessage(msg);
					save = FFCommonConfig.leavesUsInPeaceIncompatibilityNotified = true;
				}
				if (save) {
					KiwiConfigManager.getHandler(FFCommonConfig.class).save();
				}
			});
		}

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (Hooks.gadget) {
				dispatcher.register(FFCommands.register());
			}
		});

		CustomIngredientSerializer.register(BeehiveIngredient.SERIALIZER);
		LycheeCompat.init();
	}

	@SuppressWarnings("DataFlowIssue")
	public static void initBeeModule() {
		// map in StatType is an IdentityHashMap, update the reference
		BeeModule.BEE_ONE_CM = makeCustomStat(BeeModule.BEE_ONE_CM, StatFormatter.DISTANCE);
		BeeModule.BEES_BRED = makeCustomStat(BeeModule.BEES_BRED, StatFormatter.DEFAULT);

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			Map<String, FFPlayer.GeneName> map = FFPlayer.of(oldPlayer).fruits$getGeneNames();
			FFPlayer.of(newPlayer).fruits$setGeneNames(map);
		});

		UseEntityCallback.EVENT.register((player, level, hand, target, hitResult) -> {
			FFPlayer ffPlayer = FFPlayer.of(player);
			if (target instanceof LivingEntity && !target.is(BeeModule.CANNOT_HAUNT) &&
					ffPlayer.fruits$hauntingTarget() instanceof Bee bee && BeeAttributes.of(bee).hasTrait(Trait.GHOST)) {
				if (!level.isClientSide()) {
					ffPlayer.fruits$setHauntingTarget(target);
					HauntingManager manager = ffPlayer.fruits$hauntingManager();
					if (manager != null) {
						manager.storeBee(bee);
					}
				}
				return InteractionResult.SUCCESS_SERVER;
			}
			return InteractionResult.PASS;
		});
	}

	public static void initGadgetModule() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (GadgetModule.VAC_GUN.is(player.getItemInHand(hand))) {
				player.startUsingItem(hand);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		});
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if (GadgetModule.VAC_GUN.is(player.getItemInHand(hand))) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (GadgetModule.VAC_GUN.is(player.getItemInHand(hand))) {
				player.startUsingItem(hand);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.PASS;
		});
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (GadgetModule.VAC_GUN.is(player.getItemInHand(hand))) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});

		for (KiwiModuleContainer module : KiwiModules.get()) {
			module.getRegistryEntries(FFRegistries.SCENT_TYPE_KEY).forEach($ -> {
				onScentTypeAdded($.key(), $.get());
			});
		}

		FabricDefaultAttributeRegistry.register(GadgetModule.SUMMONED_BEE.getOrCreate(), Bee.createAttributes());
	}

	public static void onScentTypeAdded(Identifier id, ScentType scentType) {
		AttachmentType<Long> type = AttachmentRegistry.create(id.withSuffix("_scent"), builder -> builder.persistent(Codec.LONG));
		SCENT_ATTACHMENT_TYPES.put(scentType, type);
	}

	public static void addFeature(String id) {
		ResourceKey<PlacedFeature> key = ResourceKey.create(
				Registries.PLACED_FEATURE,
				Objects.requireNonNull(KUtil.RL(id, FruitfulFun.ID)));
		BiomeModifications.addFeature(
				context -> context.hasTag(ConventionalBiomeTags.IS_DECIDUOUS_TREE) ||
						context.hasTag(ConventionalBiomeTags.IS_JUNGLE_TREE) ||
						context.hasFeature(VegetationFeatures.TREES_PLAINS), GenerationStep.Decoration.VEGETAL_DECORATION, key);
	}

	@Nullable
	public static FlowerCrownItem getFlowerCrown(LivingEntity entity) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (stack.getItem() instanceof FlowerCrownItem item) {
			return item;
		}
		if (trinkets) {
			return TrinketsCompat.getFlowerCrown(entity);
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

	public static Identifier makeCustomStat(Identifier id, StatFormatter formatter) {
		Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
		Stats.CUSTOM.get(id, formatter);
		return id;
	}
}
