package net.pl3x.bukkit.pl3xcrates.effects;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class SoundData {
    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final int duration;

    public SoundData(ConfigurationSection soundSection) {
        this.sound = Sound.valueOf(soundSection.getString("type"));
        this.volume = (float) soundSection.getDouble("volume");
        this.pitch = (float) soundSection.getDouble("pitch");
        this.duration = soundSection.getInt("duration");
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getDuration() {
        return duration;
    }
}
