package snownee.fruits;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.fruits.cherry.block.SlidingDoorEntity;
import snownee.fruits.hybridization.HybridingContext;
import snownee.fruits.hybridization.HybridingRecipe;
import snownee.fruits.hybridization.Hybridization;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.Util;

public final class Hooks {

	public static boolean cherry;
	public static boolean hybridization;

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
		FruitType type = block instanceof FruitLeavesBlock ? ((FruitLeavesBlock) block).type.get() : null;
		NBTHelper data = NBTHelper.of(bee.getPersistentData());
		ListTag list = data.getTagList("FruitsList", Tag.TAG_STRING);
		if (list == null) {
			list = new ListTag();
			data.setTag("FruitsList", list);
		}
		String newPollen = type != null ? type.name() : "_" + Util.trimRL(block.getRegistryName());
		if (list.stream().anyMatch(e -> e.getAsString().equals(newPollen))) {
			return;
		}
		StringTag newPollenNBT = StringTag.valueOf(newPollen);
		if (!list.isEmpty()) {
			Collection<Either<FruitType, Block>> pollenList = readPollen(list);
			pollenList.add(parsePollen(newPollen));
			Optional<HybridingRecipe> recipe = bee.level.getRecipeManager().getRecipeFor(Hybridization.RECIPE_TYPE, new HybridingContext(pollenList), bee.level);
			if (recipe.isPresent()) {
				Block newBlock = recipe.get().getResultAsBlock(pollenList);
				boolean isLeaves = newBlock instanceof FruitLeavesBlock;
				boolean isFlower = !isLeaves && BlockTags.FLOWERS.contains(newBlock);
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
				BlockState newState = newBlock.defaultBlockState();
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
				}
				if (placed) {
					data.remove("FruitsList");
					return;
				}
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

	public static List<Either<FruitType, Block>> readPollen(ListTag list) {
		List<Either<FruitType, Block>> pollenList = Lists.newArrayList();
		list.forEach(e -> pollenList.add(parsePollen(e.getAsString())));
		return pollenList;
	}

	public static Either<FruitType, Block> parsePollen(String id) {
		if (id.startsWith("_")) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id.substring(1)));
			return Either.right(block);
		} else {
			FruitType type = FruitType.parse(id);
			return Either.left(type);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void modifyRayTraceResult(Minecraft mc) {
		if (mc.hitResult instanceof EntityHitResult) {
			Entity entity = ((EntityHitResult) mc.hitResult).getEntity();
			if (entity instanceof SlidingDoorEntity) {
				Vec3 vec = mc.hitResult.getLocation();
				BlockPos pos = entity.blockPosition();
				if (vec.y - pos.getY() >= 1)
					pos = pos.above();
				//mc.level.addParticle(ParticleTypes.ANGRY_VILLAGER, vec.x, vec.y, vec.z, 0, 0, 0);
				mc.hitResult = new BlockHitResult(vec, Direction.UP, pos, false);
			}
		}
	}

}
