package net.pl3x.bukkit.pl3xcrates.task.crate;

import net.pl3x.bukkit.pl3xcrates.Pl3xCrates;
import net.pl3x.bukkit.pl3xcrates.crate.Crate;
import net.pl3x.bukkit.pl3xcrates.crate.Reward;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class Normal extends Randomizer {
    public Normal(Player player, Crate crate) {
        super(player, crate, 27);

        runTask(Pl3xCrates.getPlugin());
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
