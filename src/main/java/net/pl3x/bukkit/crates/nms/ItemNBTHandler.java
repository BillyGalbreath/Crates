package net.pl3x.bukkit.crates.nms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.pl3x.bukkit.crates.Logger;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemNBTHandler {
    public static ItemStack setItemNBT(ItemStack bukkitItem, String nbt, String path) {
        if (nbt == null || nbt.isEmpty()) {
            return bukkitItem; // nothing to parse
        }

        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        try {
            nmsItem.setTag(MojangsonParser.parse(nbt));
        } catch (CommandSyntaxException e) {
            Logger.error("Error parsing NBT: " + path + ".nbt");
            e.printStackTrace();
        }

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
}
