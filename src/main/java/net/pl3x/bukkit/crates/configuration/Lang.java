package net.pl3x.bukkit.crates.configuration;

import net.pl3x.bukkit.crates.Crates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String UNKNOWN_COMMAND;
    public static String PLAYER_COMMAND;
    public static String MUST_SPECIFY_PLAYER;
    public static String PLAYER_NOT_ONLINE;
    public static String NOT_A_NUMBER;
    public static String MUST_SPECIFY_CRATE;
    public static String CRATE_DOES_NOT_EXIST;
    public static String NOT_LOOKING_AT_BLOCK;
    public static String INVALID_CRATE_BLOCK;
    public static String CRATE_ALREADY_AT_BLOCK;
    public static String MUST_HAVE_KEY;
    public static String CRATE_NOT_AT_BLOCK;
    public static String CRATE_SET_ERROR;
    public static String CRATE_SET;
    public static String CRATE_REMOVED_ERROR;
    public static String CRATE_REMOVED;
    public static String OUT_OF_ORDER;
    public static String GIVE_KEY;
    public static String RECEIVED_KEY;
    public static String VERSION;
    public static String RELOAD;

    public static void reload() {
        Crates plugin = Crates.getPlugin();
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&4You do not have permission for that command!");
        UNKNOWN_COMMAND = config.getString("unknown-command", "&4Unknown command!");
        PLAYER_COMMAND = config.getString("player-command", "&4Player only command!");
        MUST_SPECIFY_PLAYER = config.getString("must-specify-player", "&4Must specify player!");
        PLAYER_NOT_ONLINE = config.getString("player-not-online", "&4Player not online!");
        NOT_A_NUMBER = config.getString("not-a-number", "&4Invalid number specified!");
        MUST_SPECIFY_CRATE = config.getString("must-specify-crate", "&4Must specify a crate!");
        CRATE_DOES_NOT_EXIST = config.getString("crate-does-not-exist", "&4Crate does not exist!");
        NOT_LOOKING_AT_BLOCK = config.getString("not-looking-at-block", "&4Not looking at a block!");
        INVALID_CRATE_BLOCK = config.getString("invalid-crate-block", "&4Not looking at a valid crate block!");
        CRATE_ALREADY_AT_BLOCK = config.getString("crate-already-at-block", "&4Crate already exists on this block!");
        MUST_HAVE_KEY = config.getString("must-have-key", "&4Must have key to open this crate!");
        CRATE_NOT_AT_BLOCK = config.getString("crate-not-at-block", "&4This block is not a crate!");
        CRATE_SET_ERROR = config.getString("crate-set-error", "&4There was a problem setting the crate here! Please check console for any errors.");
        CRATE_SET = config.getString("crate-set", "&dCrate set.");
        CRATE_REMOVED_ERROR = config.getString("crate-removed-error", "&4There was a problem removing this crate! Please check console for any errors.");
        CRATE_REMOVED = config.getString("crate-removed", "&dCrate removed.");
        OUT_OF_ORDER = config.getString("out-of-order", "&4This crate is temporarily out of order!");
        GIVE_KEY = config.getString("give-key", "&dx{count} {key} &dgiven to player(s).");
        RECEIVED_KEY = config.getString("received-key", "&dYou have received x{count} {key}&d!");
        VERSION = config.getString("version", "&d{plugin} v{version}.");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded.");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
