package net.pl3x.bukkit.pl3xcrates.task;

import net.pl3x.bukkit.pl3xcrates.effects.ParticleData;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleTask extends BukkitRunnable {
    private final Location location;
    private final ParticleData particles;

    public ParticleTask(Location location, ParticleData particles) {
        this.location = location.clone().add(0.5, 0, 0.5);
        this.particles = particles;
    }

    @Override
    public void run() {
        location.getWorld().spigot().playEffect(
                location,
                particles.getEffect(),
                particles.getId(),
                particles.getData(),
                particles.getOffsetX(),
                particles.getOffsetY(),
                particles.getOffsetZ(),
                particles.getSpeed(),
                particles.getAmount(),
                particles.getRadius()
        );
    }
}
