package net.pl3x.bukkit.crates;

import net.pl3x.bukkit.crates.command.CmdPl3xCrates;
import net.pl3x.bukkit.crates.configuration.Config;
import net.pl3x.bukkit.crates.configuration.Lang;
import net.pl3x.bukkit.crates.crate.CrateManager;
import net.pl3x.bukkit.crates.listener.CrateListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Crates extends JavaPlugin {
    private static Crates instance;

    public Crates() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        CrateManager.INSTANCE.loadAll();

        Bukkit.getPluginManager().registerEvents(new CrateListener(this), this);

        getCommand("crates").setExecutor(new CmdPl3xCrates(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        CrateManager.INSTANCE.unloadAll();

        Logger.info(getName() + " disabled.");
    }

    public static Crates getPlugin() {
        return instance;
    }
}
