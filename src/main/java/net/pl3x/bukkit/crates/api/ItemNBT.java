package net.pl3x.bukkit.crates.api;

import org.bukkit.inventory.ItemStack;

public interface ItemNBT {
    ItemStack setItemNBT(ItemStack bukkitItem, String nbt, String path);
}
