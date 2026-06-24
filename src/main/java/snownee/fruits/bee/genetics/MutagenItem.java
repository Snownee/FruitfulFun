package snownee.fruits.bee.genetics;

import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.kiwi.item.ItemCategoryFiller;
import snownee.kiwi.item.ModItem;

public class MutagenItem extends ModItem implements ItemCategoryFiller {
	public static final Item BREWING_ITEM = Items.PITCHER_PLANT;
	public static final RandomSource RANDOM = RandomSource.create();

	public MutagenItem() {
		super(new Item.Properties());
	}

	@Override
	public Component getName(ItemStack stack) {
		Mutagen mutagen = stack.get(BeeModule.MUTAGEN_CONTENT.get());
		if (mutagen == null) {
			return super.getName(stack);
		}
		if (mutagen.isImperfect()) {
			return Component.translatable("item.fruitfulfun.mutagen.imperfect");
		}
		return Component.translatable("item.fruitfulfun.mutagen.stable", mutagen.getClientName());
	}

	@Override
	public ItemStack getDefaultInstance() {
		return imperfectMutagen();
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (!(entity instanceof Bee bee) || bee.isBaby()) {
			return InteractionResult.PASS;
		}
		if (BeeAttributes.of(bee).getMutagenEndsIn() > player.level().getGameTime()) {
			return InteractionResult.FAIL;
		}
		Mutagen mutagen = stack.get(BeeModule.MUTAGEN_CONTENT.get());
		if (mutagen == null) {
			return InteractionResult.FAIL;
		}
		if (player.level() instanceof ServerLevel level) {
			Allele allele = Allele.byCode(mutagen.type());
			if (allele == null) {
				player.sendOverlayMessage(Component.translatable("tip.fruitfulfun.invalidMutagen"));
				return InteractionResult.FAIL;
			}
			stack.shrink(1);
			ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
			if (!player.addItem(bottle)) {
				bee.spawnAtLocation(level, bottle);
			}
//				bee.level().playSound(null, bee, TODO, SoundSource.NEUTRAL, 1, 1);
			bee.gameEvent(GameEvent.DRINK, player);
			bee.addEffect(new MobEffectInstance(BeeModule.MUTAGEN_EFFECT.holderOrThrow(), 1200, allele.index, true, true, false));
			player.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS_SERVER;
	}

	public ItemStack randomMutagen(boolean containsImperfect, @Nullable RandomSource random) {
		if (random == null) {
			random = RANDOM;
		}
		if (containsImperfect && random.nextFloat() < FFCommonConfig.imperfectMutagenChance) {
			return imperfectMutagen();
		}
		Allele allele = Util.getRandom(List.copyOf(Allele.values()), random);
		ItemStack stack = new ItemStack(this);
		stack.set(BeeModule.MUTAGEN_CONTENT.get(), new Mutagen(allele.codename, allele.color));
		return stack;
	}

	public ItemStack imperfectMutagen() {
		ItemStack stack = new ItemStack(this);
		stack.set(BeeModule.MUTAGEN_CONTENT.get(), Mutagen.IMPERFECT);
		return stack;
	}

	@Override
	public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
		if (owner instanceof ServerPlayer player && !itemStack.has(BeeModule.MUTAGEN_CONTENT.get())) {
			itemStack.shrink(1);
			player.addItem(randomMutagen(false, player.getRandom()));
		}
	}

	@Override
	public void fillItemCategory(CreativeModeTab creativeModeTab, FeatureFlagSet featureFlagSet, boolean b, List<ItemStack> list) {
		list.add(new ItemStack(this));
		list.add(getDefaultInstance());
	}
}
