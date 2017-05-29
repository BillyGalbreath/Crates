package net.pl3x.bukkit.pl3xcrates.nms;

import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.pl3x.bukkit.pl3xcrates.Logger;
import net.pl3x.bukkit.pl3xcrates.api.ItemNBT;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemNBTHandler implements ItemNBT {
    public ItemStack setItemNBT(ItemStack bukkitItem, String nbt, String path) {
        if (nbt == null || nbt.isEmpty()) {
            return bukkitItem; // nothing to parse
        }

        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        try {
            nmsItem.setTag(MojangsonParser.parse(nbt));
        } catch (MojangsonParseException e) {
            Logger.error("Error parsing NBT: " + path + ".nbt");
            e.printStackTrace();
        }

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
}
