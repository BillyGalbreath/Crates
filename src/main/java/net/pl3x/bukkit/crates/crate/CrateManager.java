package net.pl3x.bukkit.crates.crate;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.ItemUtil;
import net.pl3x.bukkit.crates.Logger;
import net.pl3x.bukkit.crates.configuration.CrateConfig;
import net.pl3x.bukkit.crates.configuration.DataConfig;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class CrateManager {
    public static final CrateManager INSTANCE = new CrateManager(Crates.getPlugin());

    private final Crates plugin;
    private final Set<Crate> crates = new HashSet<>();

    private CrateManager(Crates plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        File cratesDir = new File(plugin.getDataFolder(), "crates");

        // create directory and default crate if doesnt exist
        if (!cratesDir.exists()) {
            if (!cratesDir.mkdirs()) {
                Logger.error("Could not create crates directory! " + cratesDir.getAbsolutePath());
                return;
            }
            if (!saveResource("example.yml", cratesDir)) {
                Logger.error("Could not save example.yml crate file!");
                return;
            }
        }

        // cycle files
        File[] crateFilesList = cratesDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (crateFilesList == null) {
            Logger.error("Could not read from crates directory! " + cratesDir.getAbsolutePath());
            return;
        }
        for (File file : crateFilesList) {
            Logger.debug("Loading crate: " + file.getName());

            CrateConfig crateConfig = new CrateConfig(file);
            try {
                crateConfig.load();
            } catch (IOException e) {
                Logger.error("Could not load crate file: " + file.getName());
                e.printStackTrace();
                continue;
            } catch (InvalidConfigurationException e) {
                Logger.error("Crate file contains invalid YAML structure: " + file.getName());
                e.printStackTrace();
                continue;
            }

            if (crateConfig.isDisabled()) {
                Logger.warn("Crate is disabled in config: " + file.getName());
                continue;
            }

            Crate crate = new Crate(crateConfig);
            DataConfig.getConfig().getLocations(crate).forEach(crate::addLocation);

            crates.add(crate);
        }
    }

    public void unloadAll() {
        for (Crate crate : crates) {
            crate.stopAllDormant();
            crate.closeInventories();
            crate.removeHolograms();
        }
        crates.clear();
    }

    public Set<Crate> getCrates() {
        return crates;
    }

    public Crate getCrate(Location location) {
        for (Crate crate : crates) {
            if (crate.getLocations().contains(location)) {
                return crate;
            }
        }
        return null;
    }

    public Crate getCrate(String identifier) {
        identifier = identifier.toLowerCase();
        for (Crate crate : crates) {
            if (crate.getIdentifier().equals(identifier)) {
                return crate;
            }
        }
        return null;
    }

    public Crate getCrate(Inventory inventory) {
        for (Crate crate : crates) {
            if (crate.getInventory().equals(inventory)) {
                return crate;
            }
            for (Inventory crateInv : crate.getOpenInventories()) {
                if (crateInv.equals(inventory)) {
                    return crate;
                }
            }
        }
        return null;
    }

    public Crate getCrate(ItemStack key) {
        for (Crate crate : crates) {
            if (ItemUtil.equals(crate.getKey(), key)) {
                return crate;
            }
        }
        return null;
    }

    private boolean saveResource(String resourcePath, File directory) {
        InputStream in = plugin.getResource(resourcePath);
        File outFile = new File(directory, resourcePath);
        try {
            FileOutputStream out = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
