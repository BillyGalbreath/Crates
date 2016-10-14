package net.pl3x.bukkit.pl3xcrates.task.crate;

import net.pl3x.bukkit.pl3xcrates.Pl3xCrates;
import net.pl3x.bukkit.pl3xcrates.crate.Crate;
import net.pl3x.bukkit.pl3xcrates.crate.CrateType;
import net.pl3x.bukkit.pl3xcrates.crate.Reward;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

public class Roulette extends Randomizer {
    private int counter = 0;

    public Roulette(Player player, Crate crate) {
        super(player, crate, 27);

        runTaskTimer(Pl3xCrates.getPlugin(), 1, 2);
    }

    @Override
    public void run() {
        // verify still running
        if (cancelled) {
            return;
        }

        // check if finished
        if (counter >= 50) {
            Bukkit.getScheduler().runTaskLater(Pl3xCrates.getPlugin(), () -> {
                giveReward(cycleIt(true));
            }, 3);
            cancel();
        }

        // update display
        cycleIt(false);

        // count up
        counter++;
    }

    Reward cycleIt(boolean done) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (crate.getType() == CrateType.ROULETTE_FANCY) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("...");
            glass.setItemMeta(meta);
            if (done) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, glass);
                }
            } else {
                for (int i = 0; i < inventory.getSize(); i++) {
                    glass.setDurability((short) random.nextInt(DyeColor.values().length));
                    inventory.setItem(i, glass);
                }
            }
        }

        Reward reward = rewards[random.nextInt(rewards.length)];
        inventory.setItem(13, reward.getDisplay());

        return reward;
    }
}
