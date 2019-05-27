package net.pl3x.bukkit.crates.crate;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reward {
    private final ItemStack display;
    private final ItemStack item;
    private final List<String> commands;
    private final int chance;

    public Reward(ItemStack display, ItemStack item, List<String> commands, int chance) {
        this.display = display;
        this.item = item;
        this.commands = commands;
        this.chance = chance;
    }

    public ItemStack getDisplay() {
        return display;
    }

    public ItemStack getItem() {
        return item;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getChance() {
        return chance;
    }
}
