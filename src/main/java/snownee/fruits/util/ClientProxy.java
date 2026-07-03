package snownee.fruits.util;

import static snownee.fruits.CoreModule.APPLE_LEAVES;
import static snownee.fruits.CoreModule.CITRON_LEAVES;
import static snownee.fruits.CoreModule.GRAPEFRUIT_LEAVES;
import static snownee.fruits.CoreModule.LEMON_LEAVES;
import static snownee.fruits.CoreModule.LIME_LEAVES;
import static snownee.fruits.CoreModule.ORANGE_LEAVES;
import static snownee.fruits.CoreModule.POMELO_LEAVES;
import static snownee.fruits.CoreModule.TANGERINE_LEAVES;
import static snownee.fruits.cherry.CherryModule.PEACH_PINK_PETALS;
import static snownee.fruits.cherry.CherryModule.PETAL_CHERRY;
import static snownee.fruits.cherry.CherryModule.PETAL_REDLOVE;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FFBoats;
import snownee.fruits.FFClientConfig;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.EditGeneNameScreen;
import snownee.fruits.bee.genetics.MutagenTintSource;
import snownee.fruits.bee.genetics.TransformBeesRenderer;
import snownee.fruits.client.Head;
import snownee.fruits.client.SlidingDoorRenderer;
import snownee.fruits.client.particle.FoodSmokeParticle;
import snownee.fruits.client.particle.GhostParticle;
import snownee.fruits.client.particle.PetalParticle;
import snownee.fruits.compat.supplementaries.SupplementariesCompat;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.food.FoodModule;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.client.AirVortexParticle;
import snownee.fruits.gadget.client.BuzzyCrafterRenderer;
import snownee.fruits.gadget.client.ItemProjectileColor;
import snownee.fruits.gadget.client.ItemProjectileRenderer;
import snownee.kiwi.BlockObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.loader.ClientPlatform;
import snownee.kiwi.util.client.ColorProviderUtil;
import snownee.lychee.util.action.ActionRenderer;

public class ClientProxy implements ClientModInitializer {
	private static final ExtraModelKey<BlockStateModel> CHERRY_CROWN_MODEL = ExtraModelKey.create();
	private static final ExtraModelKey<BlockStateModel> REDLOVE_CROWN_MODEL = ExtraModelKey.create();
	public static final RenderStateDataKey<ItemStack> SADDLE = RenderStateDataKey.create(() -> "saddle");
	public static final RenderStateDataKey<Identifier> TEXTURE = RenderStateDataKey.create(() -> "texture");
	public static final RenderStateDataKey<Function<Identifier, RenderType>> RENDER_TYPE = RenderStateDataKey.create(() -> "render_type");
	public static final RenderStateDataKey<Unit> NO_BOB = RenderStateDataKey.create(() -> "no_bob");

	@SuppressWarnings("unchecked")
	@Nullable
	public static BlockStateModel getModel(ModelManager modelManager, Object key) {
		return modelManager.getModel((ExtraModelKey<BlockStateModel>) key);
	}

	public static boolean poseArm(HumanoidRenderState state, ModelPart arm, ModelPart head, boolean rightArm) {
		if (!Hooks.bee && !Hooks.gadget) {
			return false;
		}
		ItemStack stack = rightArm ? state.rightHandItemStack : state.leftHandItemStack;
		if (Hooks.bee && BeeModule.INSPECTOR.is(stack)) {
			arm.xRot = Mth.clamp(head.xRot - 1.5198622f - (state.isCrouching ? 0.2617994f : 0.0f), -2.4f, 3.3f);
			arm.yRot = head.yRot - 0.2617994f * (rightArm ? 1 : -1);
			return true;
		} else if (Hooks.gadget && GadgetModule.VAC_GUN.is(stack)) {
			arm.xRot = Mth.clamp(head.xRot - 1.5198622f - (state.isCrouching ? 0.2617994f : 0.0f), -2.4f, 3.3f);
			arm.yRot = head.yRot - 0.2617994f * (rightArm ? 1 : -1);
			return true;
		}
		return false;
	}

	public static void openEditGeneNameScreen() {
		Minecraft.getInstance().setScreen(new EditGeneNameScreen());
	}

	public static @Nullable ItemProjectileColor getItemProjectileColor(ItemStack itemStack) {
		ItemProjectileColor color;
		if (Hooks.supplementaries && (color = SupplementariesCompat.getItemProjectileColor(itemStack)) != null) {
			return color;
		}
		return null;
	}

	public static void renderVacGunInHand(
			LivingEntity livingEntity,
			ItemStack itemStack,
			ItemDisplayContext itemDisplayContext,
			boolean leftHand,
			PoseStack poseStack) {
//		Vector3f vec = new Vector3f(0f, 0f, 0f);
//		poseStack.last().pose().transformPosition(vec);
//		Matrix4f screenToWorld = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();
//
//		Matrix4f rotation = new Matrix4f(RenderSystem.getInverseViewRotationMatrix());
//		screenToWorld = rotation.mul(screenToWorld);
//
//		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
//		Vec3 cameraPos = camera.getPosition();
////		screenToWorld.translate((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
//
////		Vector3f worldPos = screenToWorld.transformPosition(vec);
////		FruitfulFun.LOGGER.info(worldPos.toString(NumberFormat.getInstance()));
//
//		screenToWorld.transformPosition(vec);
//
////		Vec3 entityPos = livingEntity.getEyePosition();
////		vec.add((float) entityPos.x, (float) entityPos.y, (float) entityPos.z);
//		vec.add((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
//
////		Vector3f worldPos = screenToWorld.transformPosition(new Vector3f());
////		worldPos.add((float) entityPos.x, (float) entityPos.y, (float) entityPos.z);
////		livingEntity.level().addParticle(new AirVortexParticleOption(livingEntity.getId(), true), worldPos.x(), worldPos.y(), worldPos.z(), 0, 0, 0);
//
//		boolean mainArm = (livingEntity.getMainArm() == HumanoidArm.LEFT) == leftHand;
//		livingEntity.level().addParticle(new AirVortexParticleOption(livingEntity.getId(), mainArm), vec.x(), vec.y(), vec.z(), 0, 0, 0);
	}

	@Nullable
	public static Player getPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public void onInitializeClient() {
		ClientPlatform.registerEntityRenderer(CoreModule.SLIDING_DOOR.getOrCreate(), SlidingDoorRenderer::new);
		registerBoatRenderer("citrus", FFBoats.CITRUS_BOAT);
		registerBoatRenderer("citrus", FFBoats.CITRUS_CHEST_BOAT);
		registerBoatRenderer("redlove", FFBoats.REDLOVE_BOAT);
		registerBoatRenderer("redlove", FFBoats.REDLOVE_CHEST_BOAT);

		BlockTintSource oakBlockColor = ColorProviderUtil.delegate(Blocks.OAK_LEAVES, 0);
		List<BlockObject<?>> citrusLeaves = List.of(
				TANGERINE_LEAVES,
				LIME_LEAVES,
				CITRON_LEAVES,
				POMELO_LEAVES,
				ORANGE_LEAVES,
				LEMON_LEAVES,
				GRAPEFRUIT_LEAVES,
				APPLE_LEAVES);
		for (BlockObject<?> blockObject : citrusLeaves) {
			Block block = blockObject.getOrCreate();
			int fruitColor;
			if (CITRON_LEAVES.is(block)) {
				fruitColor = 0xDDCC58;
			} else if (GRAPEFRUIT_LEAVES.is(block)) {
				fruitColor = 0xF7B144;
			} else if (LEMON_LEAVES.is(block)) {
				fruitColor = 0xEBCA4B;
			} else if (LIME_LEAVES.is(block)) {
				fruitColor = 0xCADA76;
			} else if (TANGERINE_LEAVES.is(block)) {
				fruitColor = 0xF08A19;
			} else if (ORANGE_LEAVES.is(block)) {
				fruitColor = 0xF08A19;
			} else if (POMELO_LEAVES.is(block)) {
				fruitColor = 0xF7F67E;
			} else if (APPLE_LEAVES.is(block)) {
				fruitColor = 0xFC1C2A;
			} else {
				throw new IllegalStateException("Unknown block: " + block);
			}
			BlockColorRegistry.register(List.of(oakBlockColor, BlockTintSources.constant(fruitColor)), block);
		}

		ParticleProviderRegistry.getInstance().register(PETAL_CHERRY.getOrCreate(), PetalParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(PETAL_REDLOVE.getOrCreate(), PetalParticle.Factory::new);

		BlockColorRegistry.register(List.of(BlockColors.BLANK_LAYER, BlockTintSources.grass()), PEACH_PINK_PETALS.getOrCreate());

		ConditionalItemModelProperties.ID_MAPPER.put(FruitfulFun.id("head"), Head.MAP_CODEC);
		ModelLoadingPlugin.register(ctx -> {
			ctx.addModel(CHERRY_CROWN_MODEL, SimpleUnbakedExtraModel.blockStateModel(FruitfulFun.id("block/cherry_crown")));
			ctx.addModel(REDLOVE_CROWN_MODEL, SimpleUnbakedExtraModel.blockStateModel(FruitfulFun.id("block/redlove_crown")));
		});

		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			if (Hooks.bee && FFCommonConfig.allogamousTrees && BeeModule.isAllogamous(stack)) {
				lines.add(Component.translatable("tip.fruitfulfun.allogamy").withStyle(ChatFormatting.GRAY));
			}
			addFoodEffectTooltip(context, lines::add, flag, stack);
		});

		if (Hooks.bee) {
			ItemTintSources.ID_MAPPER.put(FruitfulFun.id("mutagen"), MutagenTintSource.CODEC);

			ClientTickEvents.START_CLIENT_TICK.register(client -> {
				if (client.player != null && client.player.isSpectator()) {
					return;
				}
				InspectorClientHandler.tick(client);
			});

			ClientTickEvents.END_CLIENT_TICK.register(mc -> {
				LocalPlayer localPlayer = mc.player;
				if (localPlayer instanceof FFPlayer player && player.fruits$isHaunting() && BeeModule.isHauntingNormalEntity(
						localPlayer,
						null) && mc.options.keyJump.isDown()) {
					localPlayer.setXRot(0);
					localPlayer.setYRot(0);
				}
			});

			ParticleProviderRegistry.getInstance().register(BeeModule.GHOST.getOrCreate(), GhostParticle.EmissiveProvider::new);
		}

		if (Hooks.food) {
			ParticleProviderRegistry.getInstance().register(FoodModule.SMOKE.getOrCreate(), FoodSmokeParticle.Factory::new);
		}

		if (CommonProxy.trinkets) {
//			TrinketsCompat.init();
		}

		if (Hooks.gadget) {
			ClientPlatform.registerEntityRenderer(GadgetModule.ITEM_PROJECTILE.getOrCreate(), ItemProjectileRenderer::new);
			ParticleProviderRegistry.getInstance().register(GadgetModule.AIR_VORTEX.getOrCreate(), AirVortexParticle.Factory::new);
			ClientPlatform.registerEntityRenderer(GadgetModule.SUMMONED_BEE.getOrCreate(), BeeRenderer::new);
			ClientPlatform.registerBlockEntityRenderer(GadgetModule.BUZZY_CRAFTER_ENTITY.getOrCreate(), BuzzyCrafterRenderer::new);
		}

		if (Hooks.ritual) {
			ActionRenderer.register(BeeModule.TRANSFORM_BEES.getOrCreate(), new TransformBeesRenderer());
		}
	}

	public static void addFoodEffectTooltip(
			Item.TooltipContext context,
			Consumer<Component> builder,
			TooltipFlag flag,
			DataComponentGetter components) {
		FoodProperties food = components.get(DataComponents.FOOD);
		Consumable consumable = components.get(DataComponents.CONSUMABLE);
		if (food == null || consumable == null) {
			return;
		}
		if (FFClientConfig.foodSpecialEffectTooltip) {
			consumable.onConsumeEffects()
					.stream()
					.map(ClientProxy::getTooltipProvider)
					.filter(Objects::nonNull)
					.forEach($ -> $.addToTooltip(context, builder, flag, components));
		}
		if (FFClientConfig.foodStatusEffectTooltip) {
			List<MobEffectInstance> effects = consumable.onConsumeEffects().stream().filter($ -> $.getClass() ==
					ApplyStatusEffectsConsumeEffect.class).flatMap($ -> ((ApplyStatusEffectsConsumeEffect) $).effects().stream()).toList();
			if (!effects.isEmpty()) {
				PotionContents.addPotionTooltip(effects, builder, 1, context.tickRate());
			}
		}
	}

	@Nullable
	public static TooltipProvider getTooltipProvider(ConsumeEffect effect) {
		return effect instanceof TooltipProvider provider ? provider : null;
	}

	public static void registerBoatRenderer(String wood, KiwiGO<? extends EntityType<? extends AbstractBoat>> boat) {
		boolean isChestBoat = boat.key().getPath().endsWith("_chest_boat");
		ModelLayerLocation modelLayer = new ModelLayerLocation(FruitfulFun.id((isChestBoat ? "chest_boat/" : "boat/") + wood), "main");
		ModelLayerRegistry.registerModelLayer(modelLayer, isChestBoat ? BoatModel::createChestBoatModel : BoatModel::createBoatModel);
		ClientPlatform.registerEntityRenderer(boat.getOrCreate(), $ -> new BoatRenderer($, modelLayer));
	}
}
