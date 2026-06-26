package snownee.fruits.gadget;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class BuzzyPowerStorage implements BuzzyPowerReceiver {
	public static final BuzzyPowerStorage EMPTY = new BuzzyPowerStorage(0, 0, 0, 0, 0);

	public static final Codec<BuzzyPowerStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("max_life", 50000f).forGetter(BuzzyPowerStorage::maxLife),
			Codec.FLOAT.fieldOf("life").forGetter(BuzzyPowerStorage::life),
			Codec.FLOAT.fieldOf("red").forGetter(BuzzyPowerStorage::red),
			Codec.FLOAT.fieldOf("green").forGetter(BuzzyPowerStorage::green),
			Codec.FLOAT.fieldOf("blue").forGetter(BuzzyPowerStorage::blue)
	).apply(instance, BuzzyPowerStorage::new));

	public static final StreamCodec<ByteBuf, BuzzyPowerStorage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			BuzzyPowerStorage::maxLife,
			ByteBufCodecs.FLOAT,
			BuzzyPowerStorage::life,
			ByteBufCodecs.FLOAT,
			BuzzyPowerStorage::red,
			ByteBufCodecs.FLOAT,
			BuzzyPowerStorage::green,
			ByteBufCodecs.FLOAT,
			BuzzyPowerStorage::blue,
			BuzzyPowerStorage::new);

	public static DataResult<BuzzyPowerStorage> read(CompoundTag data) {
		return CODEC.parse(NbtOps.INSTANCE, data);
	}

	public static Optional<BuzzyPowerStorage> of(DataComponentHolder holder) {
		return Optional.ofNullable(holder.get(GadgetModule.BUZZY_POWER_STORAGE.get()));
	}

	private float maxLife;
	private float life;
	private float red;
	private float green;
	private float blue;

	public BuzzyPowerStorage(float maxLife) {
		this.maxLife = maxLife;
	}

	public BuzzyPowerStorage(float maxLife, float life, float red, float green, float blue) {
		this.maxLife = maxLife;
		this.life = life;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static boolean isBarVisible(ItemStack stack) {
		return of(stack).map(BuzzyPowerStorage::hasLife).orElse(false);
	}

	public static int getBarColor(ItemStack stack) {
		return of(stack).map($ -> {
			float red = $.red();
			float green = $.green();
			float blue = $.blue();
			if (red == 0 && green == 0 && blue == 0) {
				red = green = blue = 1;
			}
//			Vector3f hsv = KUtil.RGBtoHSV(new Color(red, green, blue, 1).getRGB());
//			return Float.isNaN(hsv.x) ? 0xCCCCCC : Mth.hsvToRgb(hsv.x, hsv.y, 0.85f);
			return 0xCCCCCC;
		}).orElse(0xCCCCCC);
	}

	public static int getBarWidth(ItemStack stack) {
		return of(stack).map($ -> Math.round($.life() / $.maxLife() * 13f)).orElse(0);
	}

	public float maxLife() {
		return maxLife;
	}

	public float life() {
		return life;
	}

	public float red() {
		return red;
	}

	public float green() {
		return green;
	}

	public float blue() {
		return blue;
	}

	public boolean hasLife() {
		return life > 0;
	}

	@Override
	public float addPower(BuzzyPowerType type, float amount) {
//		switch (type) {
//			case RED -> red += amount;
//			case GREEN -> green += amount;
//			case BLUE -> blue += amount;
//		}
		addLife(amount * 10000);
		return 0;
	}

	@Override
	public BuzzyPowerStorage view() {
		return this;
	}

	public void addLife(float amount) {
		life = Mth.clamp(life + amount, 0, maxLife);
	}

	public void addMaxLife(float amount) {
		maxLife += amount;
	}

	public void merge(BuzzyPowerStorage other) {
		this.maxLife += other.maxLife;
		addLife(other.life);
//		this.red += other.red;
//		this.green += other.green;
//		this.blue += other.blue;
	}

	public CompoundTag save() {
		return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseThrow();
	}

	public BuzzyPowerStorage copy() {
		return new BuzzyPowerStorage(maxLife, life, red, green, blue);
	}

	public void useLife(float v) {
		life = Math.max(life - v, 0);
	}

	public boolean isEmpty() {
		return life == 0 && red == 0 && green == 0 && blue == 0;
	}
}
