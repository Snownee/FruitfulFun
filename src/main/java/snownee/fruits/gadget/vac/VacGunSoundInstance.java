package snownee.fruits.gadget.vac;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import snownee.fruits.gadget.GadgetModule;

public class VacGunSoundInstance extends AbstractTickableSoundInstance {
	private final Player player;

	public VacGunSoundInstance(Player player) {
		super(GadgetModule.GUN_WORKING.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
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
		if (player.isRemoved() || !GadgetModule.VAC_GUN.is(player.getUseItem())) {
			if (!isStopped()) {
				player.level().playLocalSound(x, y, z, GadgetModule.GUN_STOP.get(), SoundSource.PLAYERS, getVolume(), 1, false);
			}
			stop();
			return;
		}
		volume = Math.min(0.5F, volume + 0.025F);
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
