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
import static snownee.fruits.cherry.CherryModule.REDLOVE_LEAVES;

import java.util.List;

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
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.fruits.CoreModule;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.EditGeneNameScreen;
import snownee.fruits.bee.genetics.TransformBeesRenderer;
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
import snownee.kiwi.util.client.ColorProviderUtil;
import snownee.lychee.util.action.ActionRenderer;

public class ClientProxy implements ClientModInitializer {
	private static final ExtraModelKey<BlockStateModel> CHERRY_CROWN_MODEL = ExtraModelKey.create();
	private static final ExtraModelKey<BlockStateModel> REDLOVE_CROWN_MODEL = ExtraModelKey.create();

	@SuppressWarnings("unchecked")
	@Nullable
	public static BlockStateModel getModel(ModelManager modelManager, Object key) {
		return modelManager.getModel((ExtraModelKey<BlockStateModel>) key);
	}

	public static boolean poseArm(LivingEntity entity, ModelPart arm, ModelPart head, boolean rightArm) {
		if (!Hooks.bee && !Hooks.gadget) {
			return false;
		}
		HumanoidArm mainArm = entity.getMainArm();
		boolean isMainArm = rightArm ? mainArm == HumanoidArm.RIGHT : mainArm == HumanoidArm.LEFT;
		ItemStack stack = isMainArm ? entity.getMainHandItem() : entity.getOffhandItem();
		if (Hooks.bee && BeeModule.INSPECTOR.is(stack)) {
			arm.xRot = Mth.clamp(head.xRot - 1.5198622f - (entity.isCrouching() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
			arm.yRot = head.yRot - 0.2617994f * (rightArm ? 1 : -1);
			return true;
		} else if (Hooks.gadget && GadgetModule.VAC_GUN.is(stack)) {
			arm.xRot = Mth.clamp(head.xRot - 1.5198622f - (entity.isCrouching() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
			arm.yRot = head.yRot - 0.2617994f * (rightArm ? 1 : -1);
			return true;
		}
		return false;
	}

	public static void openEditGeneNameScreen() {
		Minecraft.getInstance().setScreen(new EditGeneNameScreen());
	}

	public static ItemProjectileColor getItemProjectileColor(ItemStack itemStack) {
		ItemProjectileColor color;
		if (Hooks.supplementaries && (color = SupplementariesCompat.getItemProjectileColor(itemStack)) != null) {
			return color;
		}
		return null;
	}

	public static void renderVacGunInHand(
			LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean leftHand, PoseStack poseStack) {
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
		EntityRendererRegistry.register(CoreModule.SLIDING_DOOR.getOrCreate(), SlidingDoorRenderer::new);

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

		ItemStack oakLeaves = new ItemStack(Items.OAK_LEAVES);
		ItemColor oakItemColor = ColorProviderUtil.delegate(Items.OAK_LEAVES);
		ColorProviderRegistry.ITEM.register(
				(stack, i) -> oakItemColor.getColor(oakLeaves, i),
				TANGERINE_LEAVES.get(),
				LIME_LEAVES.get(),
				CITRON_LEAVES.get(),
				POMELO_LEAVES.get(),
				ORANGE_LEAVES.get(),
				LEMON_LEAVES.get(),
				GRAPEFRUIT_LEAVES.get(),
				APPLE_LEAVES.get());

		ParticleProviderRegistry.getInstance().register(PETAL_CHERRY.getOrCreate(), PetalParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(PETAL_REDLOVE.getOrCreate(), PetalParticle.Factory::new);

		BlockTintSource birchBlockColor = ColorProviderUtil.delegate(Blocks.BIRCH_LEAVES, 0);
		ColorProviderRegistry.BLOCK.register(
				(state, world, pos, i) -> {
					if (i == 1) {
						return 0xC22626;
					}
					if (i == 2) {
						return birchBlockColor.getColor(Blocks.BIRCH_LEAVES.defaultBlockState(), world, pos, i);
					}
					return -1;
				}, REDLOVE_LEAVES.getOrCreate());

		BlockColorRegistry.register(List.of(BlockColors.BLANK_LAYER, BlockTintSources.grass()), PEACH_PINK_PETALS.getOrCreate());

		ModelLoadingPlugin.register(ctx -> {
			ctx.addModel(CHERRY_CROWN_MODEL, SimpleUnbakedExtraModel.blockStateModel(FruitfulFun.id("block/cherry_crown")));
			ctx.addModel(REDLOVE_CROWN_MODEL, SimpleUnbakedExtraModel.blockStateModel(FruitfulFun.id("block/redlove_crown")));
		});

		if (Hooks.bee) {
			ColorProviderRegistry.ITEM.register(
					(stack, i) -> {
						if (i == 0) {
							CompoundTag tag = stack.getTag();
							return tag != null && tag.contains("Color") ? tag.getInt("Color") : 0xF3DCEB;
						}
						return -1;
					}, BeeModule.MUTAGEN.getOrCreate());

			ClientTickEvents.START_CLIENT_TICK.register(client -> {
				if (client.player != null && client.player.isSpectator()) {
					return;
				}
				InspectorClientHandler.tick(client);
			});

			ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
				if (Hooks.bee && FFCommonConfig.allogamousTrees && BeeModule.isAllogamous(stack)) {
					lines.add(Component.translatable("tip.fruitfulfun.allogamy").withStyle(ChatFormatting.GRAY));
				}
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
			EntityRendererRegistry.register(GadgetModule.ITEM_PROJECTILE.getOrCreate(), ItemProjectileRenderer::new);
			ParticleProviderRegistry.getInstance().register(GadgetModule.AIR_VORTEX.getOrCreate(), AirVortexParticle.Factory::new);

			EntityRendererRegistry.register(GadgetModule.SUMMONED_BEE.getOrCreate(), BeeRenderer::new);
			BlockEntityRenderers.register(GadgetModule.BUZZY_CRAFTER_ENTITY.getOrCreate(), BuzzyCrafterRenderer::new);

			Identifier blocking = new Identifier("blocking");
			if (ItemProperties.getProperty(Items.SHIELD, blocking) instanceof ClampedItemPropertyFunction function) {
				ItemProperties.register(GadgetModule.BUZZY_SHIELD.getOrCreate(), blocking, function);
			} else {
				FruitfulFun.LOGGER.warn("Failed to register shield blocking property");
			}
		}

		if (Hooks.ritual) {
			ActionRenderer.register(BeeModule.TRANSFORM_BEES.getOrCreate(), new TransformBeesRenderer());
		}
	}
}
