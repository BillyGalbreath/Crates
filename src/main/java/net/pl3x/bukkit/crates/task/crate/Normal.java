package net.pl3x.bukkit.crates.task.crate;

import net.pl3x.bukkit.crates.Crates;
import net.pl3x.bukkit.crates.crate.Crate;
import net.pl3x.bukkit.crates.crate.Reward;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class Normal extends Randomizer {
    public Normal(Player player, Crate crate) {
        super(player, crate, 27);

        runTask(Crates.getPlugin());
    }

    @Override
    public void run() {
        giveReward(cycleIt(true));
    }

    Reward cycleIt(boolean done) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Reward reward = rewards[random.nextInt(rewards.length)];
        inventory.setItem(13, reward.getDisplay());

        return reward;
    }
}
