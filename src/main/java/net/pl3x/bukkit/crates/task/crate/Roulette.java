package net.pl3x.bukkit.crates.task.crate;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.CrateType;
import net.pl3x.bukkit.crates.crate.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class Roulette extends Randomizer {
    private int counter = 0;

    public Roulette(Player player, Crate crate) {
        super(player, crate, 27);

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
            Bukkit.getScheduler().runTaskLater(Crates.getPlugin(), () -> giveReward(cycleIt(true)), 3);
            cancel();
            return;
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

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (crate.getType() == CrateType.ROULETTE_FANCY) {
            if (done) {
                ItemStack glass = getGlass(5);
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, glass);
                }
            } else {
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, getGlass(-1));
                }
            }
        }

        Reward reward = rewards[random.nextInt(rewards.length)];
        inventory.setItem(13, reward.getDisplay());

        return reward;
    }
}
