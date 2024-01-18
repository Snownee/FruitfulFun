package snownee.fruits.bee.genetics;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import snownee.fruits.FFCommonConfig;
import snownee.fruits.bee.BeeAttributes;
import snownee.fruits.bee.BeeModule;
import snownee.fruits.bee.FFPlayer;
import snownee.kiwi.item.ModItem;
import snownee.kiwi.loader.Platform;

public class MutagenItem extends ModItem {
	public static final Item BREWING_ITEM = Items.PITCHER_PLANT;
	public static final RandomSource RANDOM = RandomSource.create();

	public MutagenItem() {
		super(new Item.Properties());
	}

	@Override
	public @NotNull Component getName(ItemStack stack) {
		return getCodename(stack)
				.map(MutagenItem::getClientName)
				.map(s -> (Component) Component.translatable("item.fruitfulfun.mutagen.stable", s))
				.orElseGet(() -> {
					if (isImperfect(stack)) {
						return Component.translatable("item.fruitfulfun.mutagen.imperfect");
					}
					return super.getName(stack);
				});
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
		String code = getCodename(stack).orElse(null);
		if (code == null) {
			return InteractionResult.FAIL;
		}
		if (!player.level().isClientSide) {
			Allele allele = Allele.byCode(code.charAt(0));
			if (allele == null) {
				player.displayClientMessage(Component.translatable("tip.fruitfulfun.invalidMutagen"), true);
				return InteractionResult.FAIL;
			}
			stack.shrink(1);
			ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
			if (!player.addItem(bottle)) {
				bee.spawnAtLocation(bottle);
			}
//				bee.level().playSound(null, bee, TODO, SoundSource.NEUTRAL, 1, 1);
			bee.gameEvent(GameEvent.DRINK, player);
			bee.addEffect(new MobEffectInstance(BeeModule.MUTAGEN_EFFECT.get(), 1200, allele.index, true, true, false));
			player.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.sidedSuccess(player.level().isClientSide);
	}

	public static Optional<String> getCodename(ItemStack stack) {
		return Optional.ofNullable(stack.getTag())
				.filter(nbt -> nbt.contains("Type", Tag.TAG_STRING))
				.map(nbt -> nbt.getString("Type"));
	}

	public static String getClientName(String codename) {
		if (Platform.isPhysicalClient() && Minecraft.getInstance().player != null) {
			return FFPlayer.of(Minecraft.getInstance().player).fruits$getGeneName(codename);
		}
		return codename;
	}

	public ItemStack randomMutagen(boolean containsImperfect, @Nullable RandomSource random) {
		if (random == null) {
			random = RANDOM;
		}
		if (containsImperfect && random.nextFloat() < FFCommonConfig.imperfectMutagenChance) {
			return imperfectMutagen();
		}
		ItemStack stack = new ItemStack(this);
		Allele allele = Util.getRandom(List.copyOf(Allele.values()), random);
		CompoundTag tag = stack.getOrCreateTag();
		tag.putString("Type", String.valueOf(allele.codename));
		tag.putInt("Color", allele.color);
		return stack;
	}

	public ItemStack imperfectMutagen() {
		ItemStack stack = new ItemStack(this);
		stack.getOrCreateTag().putBoolean("Imperfect", true);
		return stack;
	}

	public boolean isImperfect(ItemStack stack) {
		return stack.getTag() != null && stack.getTag().getBoolean("Imperfect");
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean bl) {
		if (entity instanceof ServerPlayer player && !isImperfect(stack) && getCodename(stack).isEmpty()) {
			stack.shrink(1);
			player.addItem(randomMutagen(false, player.getRandom()));
		}
	}
}
