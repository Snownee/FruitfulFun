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

import java.util.function.Supplier;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.CoreModule;
import snownee.fruits.FFClientConfig;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.EditGeneNameScreen;
import snownee.fruits.client.SlidingDoorRenderer;
import snownee.fruits.client.particle.FoodSmokeParticle;
import snownee.fruits.client.particle.PetalParticle;
import snownee.fruits.compat.supplementaries.SupplementariesCompat;
import snownee.fruits.compat.trinkets.TrinketsCompat;
import snownee.fruits.duck.FFPlayer;
import snownee.fruits.food.FoodModule;
import snownee.fruits.vacuum.AirVortexParticleOption;
import snownee.fruits.vacuum.VacModule;
import snownee.fruits.vacuum.client.AirVortexParticle;
import snownee.fruits.vacuum.client.ItemProjectileColor;
import snownee.fruits.vacuum.client.ItemProjectileRenderer;

public class ClientProxy implements ClientModInitializer {
	public static BakedModel getModel(ModelManager modelManager, ResourceLocation id) {
		return modelManager.getModel(id);
	}

	public static boolean poseArm(LivingEntity entity, ModelPart arm, ModelPart head, boolean rightArm) {
		if (!Hooks.bee && !Hooks.vac) {
			return false;
		}
		HumanoidArm mainArm = entity.getMainArm();
		boolean isMainArm = rightArm ? mainArm == HumanoidArm.RIGHT : mainArm == HumanoidArm.LEFT;
		ItemStack stack = isMainArm ? entity.getMainHandItem() : entity.getOffhandItem();
		if (Hooks.bee && BeeModule.INSPECTOR.is(stack)) {
			arm.xRot = Mth.clamp(head.xRot - 1.5198622f - (entity.isCrouching() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
			arm.yRot = head.yRot - 0.2617994f * (rightArm ? 1 : -1);
			return true;
		} else if (Hooks.vac && VacModule.VAC_GUN.is(stack)) {
			arm.xRot = Mth.clamp(head.xRot - 1.5198622f - (entity.isCrouching() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
			arm.yRot = head.yRot - 0.2617994f * (rightArm ? 1 : -1);
			return true;
		}
		return false;
	}

	public static void openEditGeneNameScreen() {
		Minecraft.getInstance().setScreen(new EditGeneNameScreen());
	}

	public static ItemProjectileColor getItemProjectileColor(Item item) {
		ItemProjectileColor color;
		if (Hooks.supplementaries && (color = SupplementariesCompat.getItemProjectileColor(item)) != null) {
			return color;
		}
		return null;
	}

	public static void renderVacGunInHand(
			LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean leftHand, PoseStack poseStack) {
		Vector3f vec = new Vector3f(0f, 0f, 0f);
		poseStack.last().pose().transformPosition(vec);
		Matrix4f screenToWorld = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();

		Matrix4f rotation = new Matrix4f(RenderSystem.getInverseViewRotationMatrix());
		screenToWorld = rotation.mul(screenToWorld);

		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.getPosition();
//		screenToWorld.translate((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);

//		Vector3f worldPos = screenToWorld.transformPosition(vec);
//		FruitfulFun.LOGGER.info(worldPos.toString(NumberFormat.getInstance()));

		screenToWorld.transformPosition(vec);

//		Vec3 entityPos = livingEntity.getEyePosition();
//		vec.add((float) entityPos.x, (float) entityPos.y, (float) entityPos.z);
		vec.add((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);

//		Vector3f worldPos = screenToWorld.transformPosition(new Vector3f());
//		worldPos.add((float) entityPos.x, (float) entityPos.y, (float) entityPos.z);
//		livingEntity.level().addParticle(new AirVortexParticleOption(livingEntity.getId(), true), worldPos.x(), worldPos.y(), worldPos.z(), 0, 0, 0);

		boolean mainArm = (livingEntity.getMainArm() == HumanoidArm.LEFT) == leftHand;
		livingEntity.level().addParticle(new AirVortexParticleOption(livingEntity.getId(), mainArm), vec.x(), vec.y(), vec.z(), 0, 0, 0);
	}

	public static Player getPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(CoreModule.SLIDING_DOOR.getOrCreate(), SlidingDoorRenderer::new);

		Supplier<BlockColor> oakBlockColor = ColorProviderUtil.delegate(Blocks.OAK_LEAVES);
		ColorProviderRegistry.BLOCK.register(
				(state, world, pos, i) -> {
					if (i == 0) {
						return oakBlockColor.get().getColor(Blocks.OAK_LEAVES.defaultBlockState(), world, pos, i);
					}
					if (i == 1) {
						if (CITRON_LEAVES.is(state)) {
							return 0xDDCC58;
						}
						if (GRAPEFRUIT_LEAVES.is(state)) {
							return 0xF7B144;
						}
						if (LEMON_LEAVES.is(state)) {
							return 0xEBCA4B;
						}
						if (LIME_LEAVES.is(state)) {
							return 0xCADA76;
						}
						if (TANGERINE_LEAVES.is(state)) {
							return 0xF08A19;
						}
						if (ORANGE_LEAVES.is(state)) {
							return 0xF08A19;
						}
						if (POMELO_LEAVES.is(state)) {
							return 0xF7F67E;
						}
						if (APPLE_LEAVES.is(state)) {
							return 0xFC1C2A;
						}
					}
					return -1;
				},
				TANGERINE_LEAVES.getOrCreate(),
				LIME_LEAVES.getOrCreate(),
				CITRON_LEAVES.getOrCreate(),
				POMELO_LEAVES.getOrCreate(),
				ORANGE_LEAVES.getOrCreate(),
				LEMON_LEAVES.getOrCreate(),
				GRAPEFRUIT_LEAVES.getOrCreate(),
				APPLE_LEAVES.getOrCreate());

		ItemStack oakLeaves = new ItemStack(Items.OAK_LEAVES);
		Supplier<ItemColor> oakItemColor = ColorProviderUtil.delegate(Items.OAK_LEAVES);
		ColorProviderRegistry.ITEM.register(
				(stack, i) -> oakItemColor.get().getColor(oakLeaves, i),
				TANGERINE_LEAVES.get(),
				LIME_LEAVES.get(),
				CITRON_LEAVES.get(),
				POMELO_LEAVES.get(),
				ORANGE_LEAVES.get(),
				LEMON_LEAVES.get(),
				GRAPEFRUIT_LEAVES.get(),
				APPLE_LEAVES.get());

		ParticleFactoryRegistry.getInstance().register(PETAL_CHERRY.getOrCreate(), PetalParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(PETAL_REDLOVE.getOrCreate(), PetalParticle.Factory::new);

		Supplier<BlockColor> birchBlockColor = ColorProviderUtil.delegate(Blocks.BIRCH_LEAVES);
		ColorProviderRegistry.BLOCK.register((state, world, pos, i) -> {
			if (i == 1) {
				return 0xC22626;
			}
			if (i == 2) {
				return birchBlockColor.get().getColor(Blocks.BIRCH_LEAVES.defaultBlockState(), world, pos, i);
			}
			return -1;
		}, REDLOVE_LEAVES.getOrCreate());

		ColorProviderRegistry.BLOCK.register((state, world, pos, i) -> {
			if (i != 0) {
				if (world == null || pos == null) {
					return GrassColor.getDefaultColor();
				}
				return BiomeColors.getAverageGrassColor(world, pos);
			}
			return -1;
		}, PEACH_PINK_PETALS.getOrCreate());

		ModelLoadingPlugin.register(ctx -> {
			ctx.addModels(
					FruitfulFun.id("block/cherry_crown"),
					FruitfulFun.id("block/redlove_crown"));
		});

		if (Hooks.bee) {
			ColorProviderRegistry.ITEM.register((stack, i) -> {
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

			ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
				if (FFClientConfig.beehiveTooltipDisplayBees && CommonProxy.isBeehive(stack)) {
					CompoundTag blockEntityData = BlockItem.getBlockEntityData(stack);
					if (blockEntityData == null) {
						return;
					}
					int bees = blockEntityData.getList(BeehiveBlockEntity.BEES, 10).size();
					lines.add(Component.translatable("tip.fruitfulfun.bees", bees).withStyle(ChatFormatting.GRAY));
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
		}

		if (Hooks.food) {
			ParticleFactoryRegistry.getInstance().register(FoodModule.SMOKE.getOrCreate(), FoodSmokeParticle.Factory::new);
		}

		if (Hooks.trinkets) {
			TrinketsCompat.init();
		}

		if (Hooks.vac) {
			EntityRendererRegistry.register(VacModule.ITEM_PROJECTILE.getOrCreate(), ItemProjectileRenderer::new);
			ParticleFactoryRegistry.getInstance().register(VacModule.AIR_VORTEX.getOrCreate(), AirVortexParticle.Factory::new);
		}
	}
}
