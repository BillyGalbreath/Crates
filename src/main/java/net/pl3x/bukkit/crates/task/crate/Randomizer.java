package net.pl3x.bukkit.crates.task.crate;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.ItemUtil;
import net.pl3x.bukkit.crates.Logger;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Randomizer extends BukkitRunnable {
    final Player player;
    final Crate crate;
    final Inventory inventory;
    final Reward[] rewards;

    boolean cancelled = false;

    public Inventory getInventory() {
        return inventory;
    }

    public Randomizer(Player player, Crate crate, int inventorySize) {
        this.player = player;
        this.crate = crate;
        this.inventory = Bukkit.createInventory(player, inventorySize, crate.getName());

        crate.addOpenInventory(inventory);

        int total = 0;
        for (Reward reward : crate.getRewards()) {
            total += reward.getChance();
        }
        this.rewards = new Reward[total];
        int i = 0;
        for (Reward reward : crate.getRewards()) {
            for (int j = 0; j < reward.getChance(); j++) {
                rewards[i] = reward;
                i++;
            }
        }

        player.openInventory(inventory);
    }

    @Override
    public void cancel() {
        super.cancel();
        cancelled = true;
    }

    void giveReward(Reward reward) {
        ItemStack display = reward.getDisplay();
        Logger.debug(player.getName() + " won " + display.getType() +
                (display.hasItemMeta() && display.getItemMeta().hasDisplayName() ?
                        " -> " + display.getItemMeta().getDisplayName() : ""));

        ItemStack item = reward.getItem();
        if (item != null) {
            ItemUtil.giveItem(player, item);
        }
        List<String> commands = reward.getCommands();
        if (commands != null && !commands.isEmpty()) {
            for (String command : commands) {
                if (command != null && !command.isEmpty()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                            .replace("{player}", player.getName()));
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(Crates.getPlugin(),
                () -> crate.removeOpenInventory(inventory), 200);
    }

    abstract Reward cycleIt(boolean done);

    public ItemStack getGlass(int index) {
        ItemStack glass = index >= 0 ? PANES.get(index) : PANES.get(ThreadLocalRandom.current().nextInt(PANES.size()));
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(index < 0 ? "Rolling..." : "Winner!");
        glass.setItemMeta(meta);
        return glass;
    }

    private static final List<ItemStack> PANES = Arrays.asList(
            new ItemStack(Material.WHITE_STAINED_GLASS_PANE),
            new ItemStack(Material.ORANGE_STAINED_GLASS_PANE),
            new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE),
            new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
            new ItemStack(Material.YELLOW_STAINED_GLASS_PANE),
            new ItemStack(Material.LIME_STAINED_GLASS_PANE),
            new ItemStack(Material.PINK_STAINED_GLASS_PANE),
            new ItemStack(Material.GRAY_STAINED_GLASS_PANE),
            new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE),
            new ItemStack(Material.CYAN_STAINED_GLASS_PANE),
            new ItemStack(Material.PURPLE_STAINED_GLASS_PANE),
            new ItemStack(Material.BLUE_STAINED_GLASS_PANE),
            new ItemStack(Material.BROWN_STAINED_GLASS_PANE),
            new ItemStack(Material.GREEN_STAINED_GLASS_PANE),
            new ItemStack(Material.RED_STAINED_GLASS_PANE),
            new ItemStack(Material.BLACK_STAINED_GLASS_PANE)
    );
}
