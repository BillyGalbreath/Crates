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

public class Slide extends Randomizer {
    private ThreadLocalRandom random = ThreadLocalRandom.current();
    private int counter = 0;
    private int index;

    public Slide(Player player, Crate crate) {
        super(player, crate, 27);

        shuffle(rewards);

        index = random.nextInt(rewards.length);

        runTaskTimer(Pl3xCrates.getPlugin(), 1, 2);
    }

    @Override
    public void run() {
        // verify still running
        if (cancelled) {
            return;
        }

        // check if finished
        if (counter >= 100 / 2) {
            Bukkit.getScheduler().runTaskLater(Pl3xCrates.getPlugin(),
                    () -> giveReward(cycleIt(true)), 3);
            cancel();
        }

        // update display
        cycleIt(false);

        // count up
        counter++;
    }

    Reward cycleIt(boolean done) {
        index++;
        if (index >= rewards.length) {
            index = 0;
        }

        if (crate.getType() == CrateType.SLIDE_FANCY) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("...");
            glass.setItemMeta(meta);
            if (done) {
                for (int i = 0; i < 27; i++) {
                    inventory.setItem(i, glass);
                }
            } else {
                for (int i = 0; i < inventory.getSize(); i++) {
                    glass.setDurability((short) random.nextInt(DyeColor.values().length));
                    inventory.setItem(i, glass);
                }
            }
        }

        ItemStack marker = new ItemStack(Material.REDSTONE_TORCH_ON);
        if (!done && counter % 2 == 0) {
            marker = new ItemStack(Material.TORCH);
        }
        inventory.setItem(4, marker);
        inventory.setItem(22, marker);

        inventory.setItem(10, rewards[(index - 3 < 0 ? rewards.length - 1 : index) - 3].getDisplay());
        inventory.setItem(11, rewards[(index - 2 < 0 ? rewards.length - 1 : index) - 2].getDisplay());
        inventory.setItem(12, rewards[(index - 1 < 0 ? rewards.length - 1 : index) - 1].getDisplay());
        inventory.setItem(13, rewards[index].getDisplay()); // the reward
        inventory.setItem(14, rewards[(index + 1 >= rewards.length ? -rewards.length : 0) + index + 1].getDisplay());
        inventory.setItem(15, rewards[(index + 2 >= rewards.length ? -rewards.length : 0) + index + 2].getDisplay());
        inventory.setItem(16, rewards[(index + 3 >= rewards.length ? -rewards.length : 0) + index + 3].getDisplay());

        return rewards[index];
    }

    private void shuffle(Reward[] array) {
        // lets shuffle it 10 times for good measure
        for (int j = 0; j < 10; j++) {
            for (int i = array.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                Reward tmp = array[index];
                array[index] = array[i];
                array[i] = tmp;
            }
        }
    }
}
