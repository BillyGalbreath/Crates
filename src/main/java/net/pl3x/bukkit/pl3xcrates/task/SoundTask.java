package net.pl3x.bukkit.pl3xcrates.task;

import net.pl3x.bukkit.pl3xcrates.effects.SoundData;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class SoundTask extends BukkitRunnable {
    private final Location location;
    private final SoundData sound;

    public SoundTask(Location location, SoundData sound) {
        this.location = location.clone().add(0.5, 0, 0.5);
        this.sound = sound;
    }

    @Override
    public void run() {
        location.getWorld().playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
    }
}
