package net.pl3x.bukkit.pl3xcrates.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface ItemNBT {
    ItemStack setItemNBT(ItemStack bukkitItem, String nbt, String path);

    EntityType getEntityType(ItemStack bukkitItem);
}
