package net.pl3x.bukkit.crates.task;

import net.pl3x.bukkit.crates.effects.FireworkData;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.scheduler.BukkitRunnable;

public class FireworkTask extends BukkitRunnable {
    private final Location location;
    private final FireworkData fireworkData;

    public FireworkTask(Location location, FireworkData fireworkData) {
        this.location = location.clone().add(0.5, 0, 0.5);
        this.fireworkData = fireworkData;
    }

    @Override
    public void run() {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        firework.setFireworkMeta(fireworkData.getFireworkMeta());
    }
}
