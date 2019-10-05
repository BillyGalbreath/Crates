package net.pl3x.bukkit.crates.task;

import net.pl3x.bukkit.crates.effects.ParticleData;
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
        location.getWorld().spawnParticle(
                particles.getParticle(),
                location.getX() + particles.getOffsetX(),
                location.getY() + particles.getOffsetY(),
                location.getZ() + particles.getOffsetZ(),
                particles.getAmount(),
                particles.getOffsetX(),
                particles.getOffsetY(),
                particles.getOffsetZ(),
                particles.getSpeed()
        );
    }
}
