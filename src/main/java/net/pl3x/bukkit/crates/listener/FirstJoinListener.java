package net.pl3x.bukkit.crates.listener;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.configuration.Config;
import net.pl3x.bukkit.crates.configuration.Lang;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.CrateManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FirstJoinListener implements Listener {
    private final Crates plugin;

    private boolean alreadyGiven;

    public FirstJoinListener(Crates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (alreadyGiven) {
            return;
        }

        alreadyGiven = true;

        if (Config.KEY_ON_FIRST_JOIN == null || Config.KEY_ON_FIRST_JOIN.isEmpty()) {
            return;
        }

        Crate crate = CrateManager.INSTANCE.getCrate(Config.KEY_ON_FIRST_JOIN);
        if (crate == null) {
            plugin.getLogger().warning("No key was given on first join because the specified crate does not exist!");
            return;
        }

        ItemStack key = crate.getKey().clone();
        Player winner = event.getPlayer();

        new BukkitRunnable() {
            public void run() {
                winner.getInventory().addItem(key).forEach((count, key) -> {
                    Item drop = winner.getWorld().dropItem(winner.getLocation(), key);
                    drop.setOwner(winner.getUniqueId());
                    drop.setCanMobPickup(false);
                });

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say " + Lang.BROADCAST_FIRST_JOIN_KEY_WINNER
                        .replace("{key}", Config.KEY_ON_FIRST_JOIN)
                        .replace("{player}", winner.getName()));
            }
        }.runTaskLater(plugin, 20L);
    }
}
