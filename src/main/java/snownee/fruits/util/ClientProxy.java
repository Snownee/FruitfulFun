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
import static snownee.fruits.cherry.CherryModule.REDLOVE_WOOD_TYPE;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.bee.genetics.EditGeneNameScreen;
import snownee.fruits.client.SlidingDoorRenderer;
import snownee.fruits.client.particle.FoodSmokeParticle;
import snownee.fruits.client.particle.PetalParticle;
import snownee.fruits.compat.supplementaries.SupplementariesCompat;
import snownee.fruits.food.FoodModule;
import snownee.fruits.vacuum.AirVortexParticleOption;
import snownee.fruits.vacuum.VacModule;
import snownee.fruits.vacuum.client.ItemProjectileColor;
import snownee.fruits.vacuum.client.ItemProjectileRenderer;
import snownee.kiwi.util.ColorProviderUtil;

public class ClientProxy {
	public static void init() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
			event.registerEntityRenderer(CoreModule.SLIDING_DOOR.getOrCreate(), SlidingDoorRenderer::new);
		});

		WoodType.register(CoreModule.CITRUS_WOOD_TYPE);

		eventBus.addListener((RegisterColorHandlersEvent.Block event) -> {
			BlockColor oakBlockColor = ColorProviderUtil.delegate(Blocks.OAK_LEAVES);
			event.register(
					(state, world, pos, i) -> {
						if (i == 0) {
							return oakBlockColor.getColor(Blocks.OAK_LEAVES.defaultBlockState(), world, pos, i);
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
		});

		eventBus.addListener((RegisterColorHandlersEvent.Item event) -> {
			ItemStack oakLeaves = new ItemStack(Items.OAK_LEAVES);
			ItemColor itemColor = ColorProviderUtil.delegate(Items.OAK_LEAVES);
			event.register(
					(stack, i) -> itemColor.getColor(oakLeaves, i),
					TANGERINE_LEAVES.get(),
					LIME_LEAVES.get(),
					CITRON_LEAVES.get(),
					POMELO_LEAVES.get(),
					ORANGE_LEAVES.get(),
					LEMON_LEAVES.get(),
					GRAPEFRUIT_LEAVES.get(),
					APPLE_LEAVES.get());
		});

		WoodType.register(REDLOVE_WOOD_TYPE);

		eventBus.addListener((RegisterParticleProvidersEvent event) -> {
			event.registerSpriteSet(PETAL_CHERRY.getOrCreate(), PetalParticle.Factory::new);
			event.registerSpriteSet(PETAL_REDLOVE.getOrCreate(), PetalParticle.Factory::new);
		});

		eventBus.addListener((RegisterColorHandlersEvent.Block event) -> {
			BlockColor birchBlockColor = ColorProviderUtil.delegate(Blocks.BIRCH_LEAVES);
			event.register((state, world, pos, i) -> {
				if (i == 1) {
					return 0xC22626;
				}
				if (i == 2) {
					return birchBlockColor.getColor(Blocks.BIRCH_LEAVES.defaultBlockState(), world, pos, i);
				}
				return -1;
			}, REDLOVE_LEAVES.getOrCreate());
		});

		eventBus.addListener((RegisterColorHandlersEvent.Block event) -> {
			event.register((blockState, blockAndTintGetter, blockPos, i) -> {
				if (i != 0) {
					if (blockAndTintGetter == null || blockPos == null) {
						return GrassColor.getDefaultColor();
					}
					return BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos);
				}
				return -1;
			}, PEACH_PINK_PETALS.getOrCreate());
		});

		eventBus.addListener((ModelEvent.RegisterAdditional event) -> {
			event.register(new ResourceLocation(FruitfulFun.ID, "block/cherry_crown"));
			event.register(new ResourceLocation(FruitfulFun.ID, "block/redlove_crown"));
		});

		if (Hooks.bee) {
			eventBus.addListener((RegisterColorHandlersEvent.Item event) -> {
				event.register((stack, i) -> {
					if (i == 0) {
						CompoundTag tag = stack.getTag();
						return tag != null && tag.contains("Color") ? tag.getInt("Color") : 0xF3DCEB;
					}
					return -1;
				}, BeeModule.MUTAGEN.getOrCreate());
			});

			MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
				if (event.phase != TickEvent.Phase.START) {
					return;
				}
				Minecraft client = Minecraft.getInstance();
				if (client.player != null && client.player.isSpectator()) {
					return;
				}
				InspectorClientHandler.tick(client);
			});
		}

		if (Hooks.food) {
			eventBus.addListener((RegisterParticleProvidersEvent event) -> {
				event.registerSpriteSet(FoodModule.SMOKE.getOrCreate(), FoodSmokeParticle.Factory::new);
			});
		}

		if (Hooks.vac) {
			eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
				event.registerEntityRenderer(VacModule.ITEM_PROJECTILE.getOrCreate(), ItemProjectileRenderer::new);
			});
		}
	}

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
			LivingEntity livingEntity,
			ItemStack itemStack,
			ItemDisplayContext itemDisplayContext,
			boolean leftHand,
			PoseStack poseStack) {
		Vector3f vec = new Vector3f(0f, 0f, 0f);
		poseStack.last().pose().transformPosition(vec);
		Matrix4f screenToWorld = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Matrix4f rotation = new Matrix4f(RenderSystem.getInverseViewRotationMatrix());
		screenToWorld = rotation.mul(screenToWorld);
		Vec3 cameraPos = camera.getPosition();

//		Vector3f worldPos = screenToWorld.transformPosition(vec);
//		FruitfulFun.LOGGER.info(worldPos.toString(NumberFormat.getInstance()));

		screenToWorld.transformPosition(vec);

		vec.add((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);

		boolean mainArm = (livingEntity.getMainArm() == HumanoidArm.LEFT) == leftHand;
		livingEntity.level().addParticle(new AirVortexParticleOption(livingEntity.getId(), mainArm), vec.x(), vec.y(), vec.z(), 0, 0, 0);
	}
}
