package net.pl3x.bukkit.crates;

import net.pl3x.bukkit.crates.api.ItemNBT;
import net.pl3x.bukkit.crates.command.CmdPl3xCrates;
import net.pl3x.bukkit.crates.configuration.Config;
import net.pl3x.bukkit.crates.configuration.Lang;
import net.pl3x.bukkit.crates.crate.CrateManager;
import net.pl3x.bukkit.crates.listener.CrateListener;
import net.pl3x.bukkit.crates.nms.ItemNBTHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Crates extends JavaPlugin {
    private ItemNBT nbtHandler;
    private CrateManager crateManager;

    public Crates() {
        crateManager = new CrateManager(this);
        nbtHandler = new ItemNBTHandler();
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        crateManager.loadAll();

        Bukkit.getPluginManager().registerEvents(new CrateListener(this), this);

        getCommand("crates").setExecutor(new CmdPl3xCrates(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        crateManager.unloadAll();

        Logger.info(getName() + " disabled.");
    }

    public static Crates getPlugin() {
        return Crates.getPlugin(Crates.class);
    }

    public ItemNBT getNBTHandler() {
        return nbtHandler;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }
}
