package snownee.fruits.gadget.scent;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import snownee.fruits.FFRegistries;
import snownee.fruits.gadget.BuzzyPowerReceiver;
import snownee.fruits.gadget.BuzzyPowerStorage;
import snownee.fruits.gadget.BuzzyPowerType;
import snownee.fruits.gadget.GadgetModule;
import snownee.fruits.gadget.crafter.BuzzyCrafterBlockEntity;
import snownee.kiwi.block.IKiwiBlock;

public class ScentedCandleBlock extends CandleBlock implements EntityBlock, IKiwiBlock {
	public static final MapCodec<ScentedCandleBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
					FFRegistries.SCENT_TYPE.byNameCodec()
							.fieldOf("scent")
							.forGetter(e -> e.type), propertiesCodec())
			.apply(i, ScentedCandleBlock::new));
	public final ScentType type;

	public ScentedCandleBlock(ScentType type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MapCodec<CandleBlock> codec() {
		return (MapCodec<CandleBlock>) (Object) CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ScentedCandleBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level,
			BlockState state,
			BlockEntityType<T> blockEntityType) {
		if (!state.getValue(LIT)) {
			return null;
		}
		return level.isClientSide() ? null : createTickerHelper(
				blockEntityType,
				GadgetModule.SCENTED_CANDLE_ENTITY.get(),
				ScentedCandleBlockEntity::serverTick);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> serverType,
			BlockEntityType<E> clientType,
			BlockEntityTicker<? super E> ticker) {
		return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
	}

	@Override
	protected InteractionResult useItemOn(
			ItemStack itemStack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hitResult) {
		if (!(level.getBlockEntity(pos) instanceof ScentedCandleBlockEntity be)) {
			return InteractionResult.FAIL;
		}
		ItemStack itemInHand = player.getItemInHand(hand);
		if (itemInHand.is(Items.COMMAND_BLOCK)) {
			if (!level.isClientSide()) {
				be.setCreative(!be.isCreative());
				player.sendOverlayMessage(Component.translatable("tip.fruitfulfun.candleCreative." + (be.isCreative() ? "on" : "off")));
			}
			return InteractionResult.SUCCESS_SERVER;
		}
		if (!level.isClientSide() && !state.getValue(LIT) && !itemInHand.isEmpty() && !itemInHand.is(asItem()) && !be.power().hasLife()) {
			player.sendOverlayMessage(Component.translatable("tip.fruitfulfun.notEnoughPower"));
		}
//		if (!level.isClientSide() && player.isHolding(Items.DIAMOND)) {
//			ScentedCandleBlockEntity.getChunksAtExactChessboardDistance(level, pos, state.getValue(CANDLES) - 1).forEach(chunk -> {
//				int x = chunk.getPos().getMiddleBlockX();
//				int y = pos.getY() + 1;
//				int z = chunk.getPos().getMiddleBlockZ();
//				level.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.DIAMOND_BLOCK.defaultBlockState());
//			});
//		}
		return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (!level.isClientSide() && !stack.isEmpty() && level.getBlockEntity(pos) instanceof ScentedCandleBlockEntity be) {
			be.addCandle(stack);
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (player.isCreative() && level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.BLOCK_DROPS) &&
				level.getBlockEntity(pos) instanceof ScentedCandleBlockEntity be && !be.power().isEmpty()) {
			dropResources(state, level, pos, be);
		}
		super.playerWillDestroy(level, pos, state, player);
		return state;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT)) {
			this.getParticleOffsets(state).forEach(particlePos -> addParticlesAndSound(
					level,
					particlePos.add(pos.getX(), pos.getY(), pos.getZ()),
					random));
		}
	}

	private void addParticlesAndSound(final Level level, final Vec3 pos, final RandomSource random) {
		float chance = random.nextFloat();
		if (chance < 0.3F) {
			ColorParticleOption particle = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.color(0.5F, type.color()));
			level.addParticle(particle, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
			if (chance < 0.17F) {
				level.playLocalSound(
						pos.x + 0.5,
						pos.y + 0.5,
						pos.z + 0.5,
						SoundEvents.CANDLE_AMBIENT,
						SoundSource.BLOCKS,
						1.0F + random.nextFloat(),
						random.nextFloat() * 0.7F + 0.3F,
						false);
			}
		}

		level.addParticle(ParticleTypes.SMALL_FLAME, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
	}

	@Override
	public BlockItem createItem(Item.Properties builder) {
		return new ScentedCandleItem(this, builder);
	}

	@Nullable
	public static BuzzyPowerReceiver getPowerReceiver(BuzzyCrafterBlockEntity crafter) {
		if (!(
				Objects.requireNonNull(crafter.getLevel()).getBlockEntity(crafter.getBlockPos()
						.above()) instanceof ScentedCandleBlockEntity be)) {
			return null;
		}
		return new BuzzyPowerReceiver() {
			@Override
			public float addPower(BuzzyPowerType type, float amount) {
				return be.isRemoved() ? amount : be.power().addPower(type, amount);
			}

			@Override
			public @Nullable BuzzyPowerStorage view() {
				return be.isRemoved() ? null : be.power();
			}
		};
	}
}
