package snownee.fruits;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.hybridization.HybridizationModule;
import snownee.fruits.hybridization.HybridizingContext;
import snownee.fruits.hybridization.HybridizingRecipe;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

public final class Hooks {

	public static boolean hybridization;
	public static boolean food;
	public static boolean farmersdelight = Platform.isModLoaded("farmersdelight");

	private Hooks() {
	}

	public static boolean safeSetBlock(Level world, BlockPos pos, BlockState state) {
		BlockState old = world.getBlockState(pos);
		if (old == state || old.hasBlockEntity() || old.getPistonPushReaction() == PushReaction.BLOCK || old.getBlock() == Blocks.OBSIDIAN || old.is(BlockTags.WITHER_IMMUNE)) {
			return false;
		}
		return world.setBlockAndUpdate(pos, state);
	}

	public static boolean canPollinate(BlockState state) {
		if (state.is(BlockTags.TALL_FLOWERS)) {
			if (state.getBlock() == Blocks.SUNFLOWER) {
				return state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
			} else {
				return true;
			}
		} else if (state.is(BlockTags.SMALL_FLOWERS)) {
			return true;
		} else if (hybridization && state.getBlock() instanceof FruitLeavesBlock) {
			if (!((FruitLeavesBlock) state.getBlock()).canGrow(state)) {
				return false;
			}
			return state.getValue(FruitLeavesBlock.AGE) == 2;
		} else {
			return false;
		}
	}

	public static void onPollinateComplete(Bee bee) {
		final BlockState state = bee.level.getBlockState(bee.getSavedFlowerPos());
		Block block = state.getBlock();
		NBTHelper data = NBTHelper.of(bee.getPersistentData());
		ListTag list = data.getTagList("FruitsList", Tag.TAG_STRING);
		if (list == null) {
			list = new ListTag();
			data.setTag("FruitsList", list);
		}
		String newPollen = Util.trimRL(Registry.BLOCK.getKey(block));
		if (list.stream().anyMatch(e -> e.getAsString().equals(newPollen))) {
			return;
		}
		StringTag newPollenNBT = StringTag.valueOf(newPollen);
		if (list.isEmpty()) {
			list.add(newPollenNBT);
			return;
		}
		List<Block> pollenList = readPollen(list);
		pollenList.add(block);
		Optional<HybridizingRecipe> recipe = bee.level.getRecipeManager().getRecipeFor(HybridizationModule.RECIPE_TYPE, new HybridizingContext(pollenList), bee.level);
		if (recipe.isPresent()) {
			Block newBlock = recipe.get().getResult(pollenList);
			BlockState newState = newBlock.defaultBlockState();
			boolean isLeaves = newBlock instanceof FruitLeavesBlock;
			boolean isFlower = !isLeaves && newState.is(BlockTags.FLOWERS);
			boolean isMisc = !isLeaves && !isFlower;
			if (!isMisc && (isLeaves != (block instanceof FruitLeavesBlock))) {
				return;
			}
			BlockPos root = bee.getSavedFlowerPos();
			if (state.is(BlockTags.TALL_FLOWERS) && state.hasProperty(DoublePlantBlock.HALF) && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
				root = root.below();
			} else if (isMisc && !newBlock.isPossibleToRespawnInThis() && !(block instanceof FruitLeavesBlock)) {
				root = root.below();
			}
			boolean isBigFlower = false;
			if (isLeaves) {
				newState = newState.setValue(FruitLeavesBlock.AGE, 2);
				newState = newState.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE));
			} else if (isFlower) {
				if (newState.is(BlockTags.TALL_FLOWERS) && newState.hasProperty(DoublePlantBlock.HALF)) {
					newState = newState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
					isBigFlower = true;
				}
			}
			boolean placed = safeSetBlock(bee.level, root, newState);
			if (placed && isBigFlower) {
				newState = newState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
				safeSetBlock(bee.level, root.above(), newState);
				bee.level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, root.above(), 0); // bonemeal effects
			}
			if (placed) {
				bee.level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, root, 0);
				data.remove("FruitsList");
				return;
			}
		}
		/* off */
        if (list.size() > 3) {
            int toRemove = list.size() - 3;
            while (toRemove --> 0) {
                list.remove(0);
            }
        }
        list.add(newPollenNBT);
        /* on */
	}

	@SuppressWarnings("deprecation")
	public static List<Block> readPollen(ListTag list) {
		/* off */
		return list.stream()
				.map(Tag::getAsString)
				.map($ -> $.startsWith("_") ? $.substring(1) : $) //TODO: data fixing. remove in 1.20
				.map(ResourceLocation::tryParse)
				.filter(Objects::nonNull)
				.map(Registry.BLOCK::get)
				.filter(Predicate.not(Blocks.AIR::equals))
				.collect(Collectors.toCollection(ArrayList::new));
		/* on */
	}

	public static void modifyRayTraceResult(HitResult hitResult, Consumer<HitResult> consumer) {
		if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
			return;
		}
		Entity entity = ((EntityHitResult) hitResult).getEntity();
		if (entity.getType() != CoreModule.SLIDING_DOOR) {
			return;
		}
		Vec3 vec = hitResult.getLocation();
		BlockPos pos = entity.blockPosition();
		if (vec.y - pos.getY() >= 1)
			pos = pos.above();
		AABB intersection = entity.getBoundingBox().intersect(new AABB(pos));
		vec = intersection.getCenter();
		//mc.level.addParticle(ParticleTypes.ANGRY_VILLAGER, vec.x, vec.y, vec.z, 0, 0, 0);
		consumer.accept(new BlockHitResult(vec, Direction.UP, pos, false));
	}

}
