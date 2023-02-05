package snownee.fruits;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import snownee.fruits.block.FruitLeavesBlock;
import snownee.kiwi.Kiwi;

@EventBusSubscriber
public final class FruitsEvents {

	public static void addPackFinder(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			event.addRepositorySource((Consumer<Pack> consumer, Pack.PackConstructor constructor) -> {
				PackMetadataSection section = new PackMetadataSection(Component.literal("Fruit Trees Conditional Resources"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
				consumer.accept(constructor.create("mod:fruittrees:conditional", Component.literal(FruitsMod.NAME), true, () -> new FruitsConditionalPackResources(section), section, Position.TOP, PackSource.DEFAULT, true));
			});
		}
	}

	public static void newRegistry(NewRegistryEvent event) {
		event.create(new RegistryBuilder<FruitType>()
				.setName(new ResourceLocation(FruitsMod.ID, "fruit_type"))
				.setDefaultKey(new ResourceLocation(FruitsMod.ID, "citron")), v -> {
			FruitType.REGISTRY = v;
			Kiwi.registerRegistry(v, FruitType.class);
		});
	}

	@SubscribeEvent
	public static void onLightningBolt(EntityJoinLevelEvent event) {
		Level world = event.getLevel();
		Entity entity = event.getEntity();
		if (world.isClientSide || !(entity instanceof LightningBolt)) {
			return;
		}
		LightningBolt entityIn = (LightningBolt) entity;
		BlockPos pos = entityIn.blockPosition();
		for (BlockPos pos2 : BlockPos.betweenClosed(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2)) {
			BlockState state2 = world.getBlockState(pos2);
			if (CoreModule.CITRON_LEAVES.is(state2) && state2.getValue(FruitLeavesBlock.AGE) == 3) {
				world.setBlockAndUpdate(pos2, state2.setValue(FruitLeavesBlock.AGE, 1));
				if (world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
				{
					ItemStack stack = CoreModule.EMPOWERED_CITRON.itemStack();
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
					bat.setCustomName(Component.translatable("fruittrees.forestbat"));
					bat.setCustomNameVisible(true);
					world.addFreshEntity(bat);
				}
			}
		}
	}

	@SubscribeEvent
	public static void addWandererTrades(WandererTradesEvent event) {
		event.getGenericTrades().add((entity, random) -> {
			/* off */
			List<Block> saplings = FruitType.REGISTRY.getValues().stream()
					.filter($ -> $.tier == 0)
					.map($ -> $.sapling.get())
					.filter(Objects::nonNull)
					.map(Block.class::cast)
					.toList();
			/* on */
			ItemStack sapling = new ItemStack(saplings.get(random.nextInt(saplings.size())));
			return new MerchantOffer(new ItemStack(Items.EMERALD, 10), sapling, 5, 1, 1);
		});
	}

}
