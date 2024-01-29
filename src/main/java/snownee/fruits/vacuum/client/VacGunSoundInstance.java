package snownee.fruits.vacuum.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.vacuum.VacModule;

public class VacGunSoundInstance extends AbstractTickableSoundInstance {
	private final Player player;

	public VacGunSoundInstance(Player player) {
		super(VacModule.GUN_WORKING.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
		this.player = player;
		attenuation = SoundInstance.Attenuation.NONE;
		looping = true;
		volume = 0;
		x = player.getX();
		y = player.getY();
		z = player.getZ();
	}

	@Override
	public void tick() {
		x = player.getX();
		y = player.getY();
		z = player.getZ();
		if (player.isRemoved() || !VacModule.VAC_GUN.is(player.getUseItem())) {
			if (!isStopped()) {
				player.level().playLocalSound(x, y, z, VacModule.GUN_STOP.get(), SoundSource.PLAYERS, getVolume(), 1, false);
			}
			stop();
			return;
		}
		volume = Math.min(1, volume + 0.05F);
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public boolean canPlaySound() {
		return !player.isSilent();
	}
}
