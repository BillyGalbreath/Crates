package net.pl3x.bukkit.pl3xcrates.effects;

import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class ParticleData {
    private final Effect effect;
    private final Vector offsetMin;
    private final Vector offsetMax;
    private final int amount;
    private final int id;
    private final int data;
    private final float speed;
    private final int radius;
    private final int duration;

    public ParticleData(ConfigurationSection particlesSection) {
        this.effect = Effect.valueOf(particlesSection.getString("effect", "MOBSPAWNER_FLAMES"));
        this.offsetMin = new Vector(particlesSection.getDouble("offset-min.x", 0D),
                particlesSection.getDouble("offset-min.y", 0D),
                particlesSection.getDouble("offset-min.z", 0D));
        this.offsetMax = new Vector(particlesSection.getDouble("offset-max.x", 0D),
                particlesSection.getDouble("offset-max.y", 0D),
                particlesSection.getDouble("offset-max.z", 0D));
        this.amount = particlesSection.getInt("amount", 1);
        this.id = particlesSection.getInt("id", 0);
        this.data = particlesSection.getInt("data", 0);
        this.speed = (float) particlesSection.getDouble("speed", 0D);
        this.radius = particlesSection.getInt("radius", 16);
        this.duration = particlesSection.getInt("duration", 0);
    }

    public Effect getEffect() {
        return effect;
    }

    public Vector getOffsetMin() {
        return offsetMin;
    }

    public Vector getOffsetMax() {
        return offsetMax;
    }

    public float getOffsetX() {
        if (offsetMin.getX() >= offsetMax.getX()) {
            return (float) offsetMin.getX();
        }
        return (float) ThreadLocalRandom.current().nextDouble(offsetMin.getX(), offsetMax.getX());
    }

    public float getOffsetY() {
        if (offsetMin.getY() >= offsetMax.getY()) {
            return (float) offsetMin.getY();
        }
        return (float) ThreadLocalRandom.current().nextDouble(offsetMin.getY(), offsetMax.getY());
    }

    public float getOffsetZ() {
        if (offsetMin.getZ() >= offsetMax.getZ()) {
            return (float) offsetMin.getZ();
        }
        return (float) ThreadLocalRandom.current().nextDouble(offsetMin.getZ(), offsetMax.getZ());
    }

    public int getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return data;
    }

    public float getSpeed() {
        return speed;
    }

    public int getRadius() {
        return radius;
    }

    public int getDuration() {
        return duration;
    }
}
