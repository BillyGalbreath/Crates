package net.pl3x.bukkit.crates.task.crate;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.CrateType;
import net.pl3x.bukkit.crates.crate.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class Slide extends Randomizer {
    private ThreadLocalRandom random = ThreadLocalRandom.current();
    private boolean fancy;
    private int counter = 0;
    private int index;

    public Slide(Player player, Crate crate) {
        super(player, crate, crate.getType() == CrateType.SLIDE_FANCY ? 27 : 9);

        shuffle(rewards);

        index = random.nextInt(rewards.length);

        runTaskTimer(Crates.getPlugin(), 1, 1);
    }

    @Override
    public void run() {
        // verify still running
        if (cancelled) {
            return;
        }

        // check if finished
        if (counter >= 100) {
            Bukkit.getScheduler().runTaskLater(Crates.getPlugin(),
                    () -> giveReward(cycleIt(true)), 3);
            cancel();
        }

        // update display
        if (counter < 63) {
            cycleIt(false);
        } else if (counter < 71 && counter % 2 == 0) {
            cycleIt(false);
        } else if (counter < 83 && counter % 4 == 0) {
            cycleIt(false);
        } else if (counter % 6 == 0) {
            cycleIt(false);
        }

        // count up
        counter++;
    }

    Reward cycleIt(boolean done) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1F, 1F);

        index++;
        if (index >= rewards.length) {
            index = 0;
        }

        int slotOffset = 0;
        if (crate.getType() == CrateType.SLIDE_FANCY) {
            slotOffset = 9;
            if (done) {
                ItemStack glass = getGlass(5);
                for (int i = 0; i < 27; i++) {
                    inventory.setItem(i, glass);
                }
            } else {
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, getGlass(-1));
                }
            }

            ItemStack marker = new ItemStack(Material.REDSTONE_TORCH);
            if (!done && counter % 2 == 0) {
                marker = new ItemStack(Material.TORCH);
            }
            inventory.setItem(4, marker);
            inventory.setItem(22, marker);
        } else {
            inventory.setItem(0, rewards[(index - 4 < 0 ? rewards.length - 1 : index) - 4].getDisplay());
            inventory.setItem(8, rewards[(index + 4 >= rewards.length ? -rewards.length : 0) + index + 4].getDisplay());
        }

        inventory.setItem(1 + slotOffset, rewards[(index - 3 < 0 ? rewards.length - 1 : index) - 3].getDisplay());
        inventory.setItem(2 + slotOffset, rewards[(index - 2 < 0 ? rewards.length - 1 : index) - 2].getDisplay());
        inventory.setItem(3 + slotOffset, rewards[(index - 1 < 0 ? rewards.length - 1 : index) - 1].getDisplay());
        inventory.setItem(4 + slotOffset, rewards[index].getDisplay()); // the reward
        inventory.setItem(5 + slotOffset, rewards[(index + 1 >= rewards.length ? -rewards.length : 0) + index + 1].getDisplay());
        inventory.setItem(6 + slotOffset, rewards[(index + 2 >= rewards.length ? -rewards.length : 0) + index + 2].getDisplay());
        inventory.setItem(7 + slotOffset, rewards[(index + 3 >= rewards.length ? -rewards.length : 0) + index + 3].getDisplay());

        return rewards[index];
    }

    private void shuffle(Reward[] array) {
        // lets shuffle it 1000 times for good measure
        for (int j = 0; j < 1000; j++) {
            for (int i = array.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                Reward tmp = array[index];
                array[index] = array[i];
                array[i] = tmp;
            }
        }
    }
}
