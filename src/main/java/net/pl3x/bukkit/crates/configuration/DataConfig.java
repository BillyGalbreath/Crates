package net.pl3x.bukkit.crates.configuration;

import net.pl3x.bukkit.crates.Logger;
import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataConfig extends YamlConfiguration {
    private static DataConfig config;

    public static DataConfig getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public static void reloadConfig() {
        config = new DataConfig();
    }

    private final File file;
    private final Object saveLock = new Object();

    private DataConfig() {
        super();
        this.file = new File(Crates.getPlugin().getDataFolder(), "data.yml");
        load();
    }

    private void load() {
        synchronized (saveLock) {
            try {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        Logger.error("Could not create new crate data file! (data.yml)");
                        return;
                    }
                }
                this.load(file);
            } catch (IOException e) {
                Logger.error("Could not read crate data file! (data.yml)");
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                Logger.error("Invalid YAML structure in crate data file! (data.yml)");
                e.printStackTrace();
            }
        }
    }

    private void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (IOException e) {
                Logger.error("Could not save to crate data file! (data.yml)");
                e.printStackTrace();
            }

        }
    }

    public void addLocation(Crate crate, Location location) {
        String locStr = location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
        List<String> list = getStringList(crate.getIdentifier());
        list.add(locStr);
        set(crate.getIdentifier(), list);
        save();
    }

    public void removeLocation(Crate crate, Location location) {
        String locStr = location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
        List<String> list = getStringList(crate.getIdentifier());
        list.remove(locStr);
        set(crate.getIdentifier(), list);
        save();
    }

    public Set<Location> getLocations(Crate crate) {
        Set<Location> locationSet = new HashSet<>();
        for (String locStr : getStringList(crate.getIdentifier())) {
            String[] split = locStr.split(",");
            World world = Bukkit.getWorld(split[0]);
            if (world == null) {
                Logger.warn("World not found: " + split[0]);
                continue;
            }
            try {
                int x = Integer.valueOf(split[1]);
                int y = Integer.valueOf(split[2]);
                int z = Integer.valueOf(split[3]);
                locationSet.add(new Location(world, x, y, z));
            } catch (NumberFormatException e) {
                Logger.warn("Invalid location data: " + locStr);
            }
        }
        return locationSet;
    }
}
