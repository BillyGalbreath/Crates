package net.pl3x.bukkit.pl3xcrates;

import net.pl3x.bukkit.pl3xcrates.api.ItemNBT;
import net.pl3x.bukkit.pl3xcrates.api.LineOfSight;
import net.pl3x.bukkit.pl3xcrates.command.CmdPl3xCrates;
import net.pl3x.bukkit.pl3xcrates.configuration.Config;
import net.pl3x.bukkit.pl3xcrates.configuration.Lang;
import net.pl3x.bukkit.pl3xcrates.crate.CrateManager;
import net.pl3x.bukkit.pl3xcrates.listener.CrateListener;
import net.pl3x.bukkit.pl3xcrates.nms.ItemNBTHandler;
import net.pl3x.bukkit.pl3xcrates.nms.LineOfSightHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xCrates extends JavaPlugin {
    private ItemNBT nbtHandler;
    private LineOfSight lineOfSightHandler;
    private CrateManager crateManager;

    public Pl3xCrates() {
        crateManager = new CrateManager(this);
        nbtHandler = new ItemNBTHandler();
        lineOfSightHandler = new LineOfSightHandler();
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        crateManager.loadAll();

        Bukkit.getPluginManager().registerEvents(new CrateListener(this), this);

        getCommand("pl3xcrates").setExecutor(new CmdPl3xCrates(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        crateManager.unloadAll();

        Logger.info(getName() + " disabled.");
    }

    public static Pl3xCrates getPlugin() {
        return Pl3xCrates.getPlugin(Pl3xCrates.class);
    }

    public ItemNBT getNBTHandler() {
        return nbtHandler;
    }

    public LineOfSight getLineOfSightHandler() {
        return lineOfSightHandler;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }
}
