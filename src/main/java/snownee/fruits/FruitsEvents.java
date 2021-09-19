package snownee.fruits;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import snownee.fruits.block.FruitLeavesBlock;

@EventBusSubscriber
public final class FruitsEvents {

	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent event) {
		if (!event.getWorld().isClientSide() && !event.getPlayer().isCreative() && event.getState().getBlock() == Blocks.OAK_LEAVES && event.getWorld() instanceof Level) {
			if (event.getWorld().getRandom().nextFloat() < FruitsConfig.oakLeavesDropsAppleSapling) {
				Block.popResource((Level) event.getWorld(), event.getPos(), new ItemStack(CoreModule.APPLE_SAPLING));
			}
		}
	}

	@SubscribeEvent
	public static void onLightningBolt(EntityJoinWorldEvent event) {
		Level world = event.getWorld();
		Entity entity = event.getEntity();
		if (world.isClientSide || !(entity instanceof LightningBolt)) {
			return;
		}
		LightningBolt entityIn = (LightningBolt) entity;
		BlockPos pos = entityIn.blockPosition();
		for (BlockPos pos2 : BlockPos.betweenClosed(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2)) {
			BlockState state2 = world.getBlockState(pos2);
			if (state2.getBlock() == CoreModule.CITRON_LEAVES && state2.getValue(FruitLeavesBlock.AGE) == 3) {
				world.setBlockAndUpdate(pos2, state2.setValue(FruitLeavesBlock.AGE, 1));
				if (world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
				{
					ItemStack stack = new ItemStack(CoreModule.EMPOWERED_CITRON);
					double d0 = world.random.nextFloat() * 0.5F + 0.25D;
					double d1 = world.random.nextFloat() * 0.5F + 0.25D;
					double d2 = world.random.nextFloat() * 0.5F + 0.25D;
					ItemEntity entityitem = new ItemEntity(world, pos2.getX() + d0, pos2.getY() + d1, pos2.getZ() + d2, stack);
					entityitem.setDefaultPickUpDelay();
					entityitem.setInvulnerable(true);
					world.addFreshEntity(entityitem);
					Bat bat = new Bat(EntityType.BAT, world);
					bat.setPos(pos2.getX() + d0, pos2.getY() + d1, pos2.getZ() + d2);
					bat.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 10));
					bat.setCustomName(new TranslatableComponent("fruittrees.forestbat"));
					bat.setCustomNameVisible(true);
					world.addFreshEntity(bat);
				}
			}
		}
	}

}
