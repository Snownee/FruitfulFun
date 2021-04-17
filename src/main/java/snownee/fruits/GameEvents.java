package snownee.fruits;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import snownee.fruits.block.FruitLeavesBlock;

@EventBusSubscriber
public final class GameEvents {

	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent event) {
		if (!event.getWorld().isRemote() && !event.getPlayer().isCreative() && event.getState().getBlock() == Blocks.OAK_LEAVES && event.getWorld() instanceof World) {
			if (event.getWorld().getRandom().nextFloat() < FruitsConfig.oakLeavesDropsAppleSapling) {
				Block.spawnAsEntity((World) event.getWorld(), event.getPos(), new ItemStack(CoreModule.APPLE_SAPLING));
			}
		}
	}

	@SubscribeEvent
	public static void onLightningBolt(EntityJoinWorldEvent event) {
		World world = event.getWorld();
		Entity entity = event.getEntity();
		if (world.isRemote || !(entity instanceof LightningBoltEntity)) {
			return;
		}
		LightningBoltEntity entityIn = (LightningBoltEntity) entity;
		BlockPos pos = entityIn.getPosition();
		for (BlockPos pos2 : BlockPos.getAllInBoxMutable(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2)) {
			BlockState state2 = world.getBlockState(pos2);
			if (state2.getBlock() == CoreModule.CITRON_LEAVES && state2.get(FruitLeavesBlock.AGE) == 3) {
				world.setBlockState(pos2, state2.with(FruitLeavesBlock.AGE, 1));
				if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
				{
					ItemStack stack = new ItemStack(CoreModule.EMPOWERED_CITRON);
					double d0 = world.rand.nextFloat() * 0.5F + 0.25D;
					double d1 = world.rand.nextFloat() * 0.5F + 0.25D;
					double d2 = world.rand.nextFloat() * 0.5F + 0.25D;
					ItemEntity entityitem = new ItemEntity(world, pos2.getX() + d0, pos2.getY() + d1, pos2.getZ() + d2, stack);
					entityitem.setDefaultPickupDelay();
					entityitem.setInvulnerable(true);
					world.addEntity(entityitem);
					BatEntity bat = new BatEntity(EntityType.BAT, world);
					bat.setPosition(pos2.getX() + d0, pos2.getY() + d1, pos2.getZ() + d2);
					bat.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 200, 10));
					bat.setCustomName(new TranslationTextComponent("fruittrees.forestbat"));
					bat.setCustomNameVisible(true);
					world.addEntity(bat);
				}
			}
		}
	}

}
