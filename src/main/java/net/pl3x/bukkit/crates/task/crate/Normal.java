package net.pl3x.bukkit.crates.task.crate;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.CrateType;
import net.pl3x.bukkit.crates.crate.Reward;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class Normal extends Randomizer {
    public Normal(Player player, Crate crate) {
        super(player, crate, 9);

        runTask(Crates.getPlugin());
    }

    @Override
    public void run() {
        giveReward(cycleIt(true));
    }

    Reward cycleIt(boolean done) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Reward reward = rewards[random.nextInt(rewards.length)];

        inventory.setItem(4, reward.getDisplay());

        if (crate.getType() == CrateType.NORMAL_FANCY) {
            inventory.setItem(0, getGlass(14)); // red
            inventory.setItem(1, getGlass(6));  // pink
            inventory.setItem(2, getGlass(2));  // magenta
            inventory.setItem(3, getGlass(10)); // purple
            inventory.setItem(5, getGlass(10)); // purple
            inventory.setItem(6, getGlass(2));  // magenta
            inventory.setItem(7, getGlass(6));  // pink
            inventory.setItem(8, getGlass(14)); // red
        }

        return reward;
    }
}
