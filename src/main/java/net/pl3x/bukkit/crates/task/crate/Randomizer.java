package net.pl3x.bukkit.crates.task.crate;

import net.pl3x.bukkit.crates.ItemUtil;
import net.pl3x.bukkit.crates.Logger;
import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                        .replace("{player}", player.getName()));
            }
        }

        Bukkit.getScheduler().runTaskLater(Crates.getPlugin(), () -> {
            List<HumanEntity> viewers = inventory.getViewers();
            if (viewers != null && !viewers.isEmpty()) {
                viewers.get(0).closeInventory();
                Bukkit.getScheduler().runTaskLater(Crates.getPlugin(),
                        () -> crate.removeOpenInventory(inventory), 10);
            }
        }, 40);
    }

    abstract Reward cycleIt(boolean done);
}
