package net.pl3x.bukkit.crates.hook;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

public class HologramHook {
    public static void addHologram(Location loc, Crate crate, List<String> lines) {
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays") && lines != null && !lines.isEmpty()) {
            com.gmail.filoghost.holographicdisplays.api.Hologram hologram =
                    com.gmail.filoghost.holographicdisplays.api.HologramsAPI
                            .createHologram(Crates.getPlugin(), loc.clone().add(crate.getHologramOffset()));
            for (String line : lines) {
                hologram.appendTextLine(line.replace("{crate}", crate.getName()));
            }
            Cache.holograms.put(key(loc), hologram);
        }
    }

    public static void removeHologram(Location loc) {
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            com.gmail.filoghost.holographicdisplays.api.Hologram hologram = Cache.holograms.remove(key(loc));
            if (hologram != null) {
                hologram.delete();
            }
        }
    }

    private static String key(Location loc) {
        return loc.getWorld().getUID() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private static class Cache {
        private static final HashMap<String, com.gmail.filoghost.holographicdisplays.api.Hologram> holograms = new HashMap<>();
    }
}
