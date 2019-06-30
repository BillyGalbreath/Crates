package net.pl3x.bukkit.crates.command;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.ItemUtil;
import net.pl3x.bukkit.crates.Logger;
import net.pl3x.bukkit.crates.configuration.Config;
import net.pl3x.bukkit.crates.configuration.DataConfig;
import net.pl3x.bukkit.crates.configuration.Lang;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.CrateManager;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdCrates implements TabExecutor {
    private final Crates plugin;

    public CmdCrates(Crates plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "set", "remove", "givekey", "blacklist")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("givekey"))) {
            return CrateManager.INSTANCE.getCrates().stream()
                    .filter(crate -> crate.getIdentifier().startsWith(args[1].toLowerCase()))
                    .map(Crate::getIdentifier).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("blacklist")) {
            return Stream.of("check", "add", "remove")
                    .filter(cmd -> cmd.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("givekey"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    .map(Player::getName).collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("blacklist")) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    .map(Player::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Lang.send(sender, Lang.VERSION
                    .replace("{version}", plugin.getDescription().getVersion())
                    .replace("{plugin}", plugin.getName()));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("crates.reload")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            Logger.debug("Unloading all crates...");
            CrateManager.INSTANCE.unloadAll();

            Logger.debug("Reloading config...");
            Config.reload();

            Logger.debug("Reloading language file...");
            Lang.reload();

            Logger.debug("Reloading data.yml...");
            DataConfig.reloadConfig();

            Logger.debug("Reloading all crates...");
            CrateManager.INSTANCE.loadAll();

            Logger.info("Reloaded crates and configs.");
            Lang.send(sender, Lang.RELOAD
                    .replace("{plugin}", plugin.getName())
                    .replace("{version}", plugin.getDescription().getVersion()));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("crates.set")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            if (!(sender instanceof Player)) {
                Lang.send(sender, Lang.PLAYER_COMMAND);
                return true;
            }
            Player player = (Player) sender;

            if (args.length < 2) {
                Lang.send(sender, Lang.MUST_SPECIFY_CRATE);
                return true;
            }

            Crate crate = CrateManager.INSTANCE.getCrate(args[1]);
            if (crate == null) {
                Lang.send(sender, Lang.CRATE_DOES_NOT_EXIST);
                return true;
            }

            Block block = player.getTargetBlockExact(5);
            if (block == null) {
                Lang.send(sender, Lang.NOT_LOOKING_AT_BLOCK);
                return true;
            }

            if (!crate.getAllowedBlocks().contains(block.getType())) {
                Lang.send(sender, Lang.INVALID_CRATE_BLOCK);
                return true;
            }

            Location blockLocation = block.getLocation();
            if (crate.getLocations().contains(blockLocation)) {
                Lang.send(sender, Lang.CRATE_ALREADY_AT_BLOCK);
                return true;
            }

            if (crate.addLocation(blockLocation)) {
                DataConfig.getConfig().addLocation(crate, blockLocation);
                Lang.send(sender, Lang.CRATE_SET);
            } else {
                Lang.send(sender, Lang.CRATE_SET_ERROR);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("crates.remove")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            if (!(sender instanceof Player)) {
                Lang.send(sender, Lang.PLAYER_COMMAND);
                return true;
            }
            Player player = (Player) sender;

            Block block = player.getTargetBlockExact(5);
            if (block == null) {
                Lang.send(sender, Lang.NOT_LOOKING_AT_BLOCK);
                return true;
            }

            Location blockLocation = block.getLocation();
            Crate crate = CrateManager.INSTANCE.getCrate(blockLocation);
            if (crate == null) {
                Lang.send(sender, Lang.CRATE_NOT_AT_BLOCK);
                return true;
            }

            if (crate.removeLocation(blockLocation)) {
                DataConfig.getConfig().removeLocation(crate, block.getLocation());
                Lang.send(sender, Lang.CRATE_REMOVED);
            } else {
                Lang.send(sender, Lang.CRATE_REMOVED_ERROR);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("givekey")) {
            if (!sender.hasPermission("crates.givekey")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            if (args.length < 2) {
                Lang.send(sender, Lang.MUST_SPECIFY_CRATE);
                return true;
            }

            Crate crate = CrateManager.INSTANCE.getCrate(args[1]);
            if (crate == null) {
                Lang.send(sender, Lang.CRATE_DOES_NOT_EXIST);
                return true;
            }

            if (args.length < 3) {
                Lang.send(sender, Lang.MUST_SPECIFY_PLAYER);
                return true;
            }

            Set<Player> target = new HashSet<>();
            if (args[2].equalsIgnoreCase("all")) {
                Collection<? extends Player> online = Bukkit.getOnlinePlayers();
                if (online.isEmpty()) {
                    Lang.send(sender, Lang.PLAYER_NOT_ONLINE);
                    return true;
                }
                target.addAll(online);
            } else {
                Player online = Bukkit.getPlayer(args[2]);
                if (online == null) {
                    Lang.send(sender, Lang.PLAYER_NOT_ONLINE);
                    return true;
                }
                target.add(online);
            }

            int amount = 1;
            if (args.length == 4) {
                try {
                    amount = Integer.valueOf(args[3]);
                } catch (NumberFormatException e) {
                    Lang.send(sender, Lang.NOT_A_NUMBER);
                    return true;
                }
            }

            ItemStack key = crate.getKey().clone();
            key.setAmount(amount);
            String received = Lang.RECEIVED_KEY
                    .replace("{count}", Integer.toString(amount))
                    .replace("{key}", key.hasItemMeta() && key.getItemMeta().hasDisplayName() ? key.getItemMeta().getDisplayName() : "key");
            for (Player online : target) {
                ItemUtil.giveItem(online, key);
                Lang.send(online, received);
            }

            if (!(sender instanceof Player) || !target.contains(sender)) {
                Lang.send(sender, Lang.GIVE_KEY
                        .replace("{count}", Integer.toString(amount))
                        .replace("{key}", key.hasItemMeta() && key.getItemMeta().hasDisplayName() ? key.getItemMeta().getDisplayName() : "key"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("blacklist")) {
            if (!sender.hasPermission("crates.blacklist")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            if (args.length < 3) {
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
            DataConfig data = DataConfig.getConfig();

            if (args[1].equalsIgnoreCase("check")) {
                Lang.send(sender, Lang.BLACKLIST_CHECK
                        .replace("{value}", BooleanUtils.toStringYesNo(data.isBlacklisted(target))));
                return true;
            }

            if (args[1].equalsIgnoreCase("add")) {
                data.addToBlacklist(target);
                Lang.send(sender, Lang.BLACKLIST_UPDATED);
                return true;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                data.removeFromBlacklist(target);
                Lang.send(sender, Lang.BLACKLIST_UPDATED);
                return true;
            }
            return false;
        }

        Lang.send(sender, Lang.UNKNOWN_COMMAND);
        return true;
    }
}
