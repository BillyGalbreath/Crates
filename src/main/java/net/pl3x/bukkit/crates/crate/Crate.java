package net.pl3x.bukkit.crates.crate;

import net.pl3x.bukkit.crates.ItemUtil;
import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.configuration.CrateConfig;
import net.pl3x.bukkit.crates.effects.FireworkData;
import net.pl3x.bukkit.crates.effects.ParticleData;
import net.pl3x.bukkit.crates.effects.SoundData;
import net.pl3x.bukkit.crates.task.FireworkTask;
import net.pl3x.bukkit.crates.task.ParticleTask;
import net.pl3x.bukkit.crates.task.SoundTask;
import net.pl3x.bukkit.crates.task.crate.Normal;
import net.pl3x.bukkit.crates.task.crate.Roulette;
import net.pl3x.bukkit.crates.task.crate.Slide;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Crate {
    private final String name;
    private final String identifier;
    private final CrateType type;
    private final boolean outOfOrder;
    private final ItemStack key;
    private final Set<Material> allowedBlocks;
    private final Vector knockback;
    private final List<Reward> rewards;
    private final Inventory inventory;

    private final List<String> openCommands;
    private final ParticleData openParticles;
    private final FireworkData openFireworks;
    private final SoundData openSound;
    private final ParticleData dormantParticles;
    private final FireworkData dormantFireworks;
    private final SoundData dormantSound;

    private final Set<Location> locations;
    private final Set<Inventory> openInventories;
    private final Map<Location, ParticleTask> dormantParticleTasks;
    private final Map<Location, FireworkTask> dormantFireworkTasks;
    private final Map<Location, SoundTask> dormantSoundTasks;

    public Crate(CrateConfig crateConfig) {
        this.name = ChatColor.translateAlternateColorCodes('&', crateConfig.getName());
        this.identifier = crateConfig.getFile().getName().split("\\.(?=[^.]+$)")[0].toLowerCase();
        this.type = crateConfig.getType();
        this.outOfOrder = crateConfig.isOutOfOrder();
        this.key = crateConfig.getKey();
        this.allowedBlocks = crateConfig.getAllowedBlocks();
        this.knockback = crateConfig.getKnockback();
        this.rewards = crateConfig.getRewards();
        this.inventory = Bukkit.createInventory(null, (int) Math.floor(rewards.size() / 9) + 1 <= 27 ? 27 : 54, name);
        this.openCommands = crateConfig.getStringList("effects.on-open.commands");

        ConfigurationSection configOpenParticles = crateConfig.getConfigurationSection("effects.on-open.particles");
        this.openParticles = configOpenParticles == null ? null : new ParticleData(configOpenParticles);
        ConfigurationSection configOpenFireworks = crateConfig.getConfigurationSection("effects.on-open.fireworks");
        this.openFireworks = configOpenFireworks == null ? null : new FireworkData(configOpenFireworks);
        ConfigurationSection configOpenSound = crateConfig.getConfigurationSection("effects.on-open.sound");
        this.openSound = configOpenSound == null ? null : new SoundData(configOpenSound);

        ConfigurationSection configDormantParticles = crateConfig.getConfigurationSection("effects.dormant.particles");
        this.dormantParticles = configDormantParticles == null ? null : new ParticleData(configDormantParticles);
        ConfigurationSection configDormantFireworks = crateConfig.getConfigurationSection("effects.dormant.fireworks");
        this.dormantFireworks = configDormantFireworks == null ? null : new FireworkData(configDormantFireworks);
        ConfigurationSection configDormantSound = crateConfig.getConfigurationSection("effects.dormant.sound");
        this.dormantSound = configDormantSound == null ? null : new SoundData(configDormantSound);

        this.locations = new HashSet<>();
        this.openInventories = new HashSet<>();
        this.dormantParticleTasks = new HashMap<>();
        this.dormantFireworkTasks = new HashMap<>();
        this.dormantSoundTasks = new HashMap<>();

        for (Reward reward : rewards) {
            inventory.addItem(reward.getDisplay());
        }
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public CrateType getType() {
        return type;
    }

    public boolean isOutOfOrder() {
        return outOfOrder || rewards == null || rewards.isEmpty();
    }

    public ItemStack getKey() {
        return key;
    }

    public boolean isKey(ItemStack item) {
        return ItemUtil.equals(item, key);
    }

    public Set<Material> getAllowedBlocks() {
        return allowedBlocks;
    }

    public Vector getKnockback() {
        return knockback;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void closeInventories() {
        new ArrayList<>(inventory.getViewers()).forEach(HumanEntity::closeInventory);
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public boolean addLocation(Location location) {
        if (locations.add(location)) {
            startDormant(location);
            return true;
        }
        return false;
    }

    public boolean removeLocation(Location location) {
        if (locations.remove(location)) {
            stopDormant(location);
            return true;
        }
        return false;
    }

    public void addOpenInventory(Inventory inventory) {
        openInventories.add(inventory);
    }

    public void removeOpenInventory(Inventory inventory) {
        openInventories.remove(inventory);
    }

    public Set<Inventory> getOpenInventories() {
        return openInventories;
    }

    private void startDormant(Location location) {
        Crates plugin = Crates.getPlugin();

        // dormant particles
        if (dormantParticles != null) {
            ParticleTask particleTask = new ParticleTask(location, dormantParticles);
            particleTask.runTaskTimer(plugin, 0, dormantParticles.getDuration() / dormantParticles.getAmount());
            dormantParticleTasks.put(location, particleTask);
        }

        // dormant fireworks
        if (dormantFireworks != null && dormantFireworks.getDuration() >= 0) {
            FireworkTask fireworkTask = new FireworkTask(location, dormantFireworks);
            fireworkTask.runTaskTimer(plugin, 0, dormantFireworks.getDuration());
            dormantFireworkTasks.put(location, fireworkTask);
        }

        // dormant sound
        if (dormantSound != null) {
            SoundTask soundTask = new SoundTask(location, dormantSound);
            if (dormantSound.getDuration() > 0) {
                soundTask.runTaskTimer(plugin, 0, dormantSound.getDuration());
                dormantSoundTasks.put(location, soundTask);
            } else {
                soundTask.runTask(plugin);
            }
        }
    }

    private void stopDormant(Location location) {
        // dormant particles
        ParticleTask particleTask = dormantParticleTasks.remove(location);
        if (particleTask != null) {
            particleTask.cancel();
        }

        // dormant fireworks
        FireworkTask fireworkTask = dormantFireworkTasks.remove(location);
        if (fireworkTask != null) {
            fireworkTask.cancel();
        }

        // dormant sound
        SoundTask soundTask = dormantSoundTasks.remove(location);
        if (soundTask != null) {
            soundTask.cancel();
        }
    }

    public void stopAllDormant() {
        dormantParticleTasks.values().forEach(BukkitRunnable::cancel);
        dormantFireworkTasks.values().forEach(BukkitRunnable::cancel);
        dormantSoundTasks.values().forEach(BukkitRunnable::cancel);
    }

    public void doOpen(Location location, Player player) {
        Crates plugin = Crates.getPlugin();

        // run the open commands
        for (String command : openCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("{player}", player.getName()));
        }

        // run the open tasks
        if (openParticles != null) {
            new ParticleTask(location, openParticles).runTask(plugin);
        }
        if (openFireworks != null) {
            new FireworkTask(location, openFireworks).runTask(plugin);
        }
        if (openSound != null) {
            new SoundTask(location, openSound).runTask(plugin);
        }

        // open crate type for player
        switch (type) {
            case NORMAL:
                new Normal(player, this);
                break;
            case ROULETTE:
            case ROULETTE_FANCY:
                new Roulette(player, this);
                break;
            case SLIDE:
            case SLIDE_FANCY:
                new Slide(player, this);
                break;
        }
    }
}
