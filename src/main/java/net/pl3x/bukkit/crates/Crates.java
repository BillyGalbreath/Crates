package net.pl3x.bukkit.crates;

import net.pl3x.bukkit.crates.command.CmdCrates;
import net.pl3x.bukkit.crates.configuration.Config;
import net.pl3x.bukkit.crates.configuration.Lang;
import net.pl3x.bukkit.crates.crate.CrateManager;
import net.pl3x.bukkit.crates.listener.CrateListener;
import net.pl3x.bukkit.crates.listener.FirstJoinListener;
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

        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        getServer().getPluginManager().registerEvents(new FirstJoinListener(this), this);

        getCommand("crates").setExecutor(new CmdCrates(this));

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
