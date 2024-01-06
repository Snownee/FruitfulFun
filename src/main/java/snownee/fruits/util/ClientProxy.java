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

import java.util.function.Supplier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import snownee.fruits.CoreModule;
import snownee.fruits.FruitfulFun;
import snownee.fruits.Hooks;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.InspectorClientHandler;
import snownee.fruits.client.SlidingDoorRenderer;
import snownee.fruits.client.particle.FoodSmokeParticle;
import snownee.fruits.client.particle.PetalParticle;
import snownee.fruits.compat.trinkets.TrinketsCompat;
import snownee.fruits.food.FoodModule;

public class ClientProxy implements ClientModInitializer {
	public static BakedModel getModel(ModelManager modelManager, ResourceLocation id) {
		return modelManager.getModel(id);
	}

	public static boolean poseArm(LivingEntity entity, ModelPart arm, ModelPart head, boolean rightArm) {
		if (!Hooks.bee) {
			return false;
		}
		HumanoidArm mainArm = entity.getMainArm();
		boolean isMainArm = rightArm ? mainArm == HumanoidArm.RIGHT : mainArm == HumanoidArm.LEFT;
		ItemStack stack = isMainArm ? entity.getMainHandItem() : entity.getOffhandItem();
		if (!BeeModule.INSPECTOR.is(stack)) {
			return false;
		}
		arm.xRot = Mth.clamp(head.xRot - 1.5198622f - (entity.isCrouching() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
		arm.yRot = head.yRot - 0.2617994f * (rightArm ? 1 : -1);
		return true;
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(CoreModule.SLIDING_DOOR.getOrCreate(), SlidingDoorRenderer::new);

		WoodType.register(CoreModule.CITRUS_WOOD_TYPE);

		Supplier<BlockColor> oakBlockColor = ColorProviderUtil.delegate(Blocks.OAK_LEAVES);
		ColorProviderRegistry.BLOCK.register((state, world, pos, i) ->
				{
					if (i == 0) {
						return oakBlockColor.get().getColor(Blocks.OAK_LEAVES.defaultBlockState(), world, pos, i);
					}
					if (i == 1) {
						if (CITRON_LEAVES.is(state))
							return 0xDDCC58;
						if (GRAPEFRUIT_LEAVES.is(state))
							return 0xF7B144;
						if (LEMON_LEAVES.is(state))
							return 0xEBCA4B;
						if (LIME_LEAVES.is(state))
							return 0xCADA76;
						if (TANGERINE_LEAVES.is(state))
							return 0xF08A19;
						if (ORANGE_LEAVES.is(state))
							return 0xF08A19;
						if (POMELO_LEAVES.is(state))
							return 0xF7F67E;
						if (APPLE_LEAVES.is(state))
							return 0xFC1C2A;
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
		Supplier<ItemColor> itemColor = ColorProviderUtil.delegate(Items.OAK_LEAVES);
		ColorProviderRegistry.ITEM.register((stack, i) -> itemColor.get().getColor(oakLeaves, i),
				TANGERINE_LEAVES.get(),
				LIME_LEAVES.get(),
				CITRON_LEAVES.get(),
				POMELO_LEAVES.get(),
				ORANGE_LEAVES.get(),
				LEMON_LEAVES.get(),
				GRAPEFRUIT_LEAVES.get(),
				APPLE_LEAVES.get());

		WoodType.register(REDLOVE_WOOD_TYPE);

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

		ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
			if (i != 0) {
				if (blockAndTintGetter == null || blockPos == null) {
					return GrassColor.getDefaultColor();
				}
				return BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos);
			}
			return -1;
		}, PEACH_PINK_PETALS.getOrCreate());

		ModelLoadingPlugin.register(ctx -> {
			ctx.addModels(
					new ResourceLocation(FruitfulFun.ID, "block/cherry_crown"),
					new ResourceLocation(FruitfulFun.ID, "block/redlove_crown"));
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
		}

		if (Hooks.food) {
			ParticleFactoryRegistry.getInstance().register(FoodModule.SMOKE.getOrCreate(), FoodSmokeParticle.Factory::new);
		}

		if (Hooks.trinkets) {
			TrinketsCompat.init();
		}
	}
}
