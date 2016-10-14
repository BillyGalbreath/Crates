package net.pl3x.bukkit.pl3xcrates.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface LineOfSight {
    Block getTargetBlock(Player player, int distance, double accuracy);
}
