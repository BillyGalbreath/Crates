package net.pl3x.bukkit.pl3xcrates.nms;

import net.minecraft.server.v1_10_R1.ChatComponentText;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.MojangsonParseException;
import net.minecraft.server.v1_10_R1.MojangsonParser;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import net.pl3x.bukkit.pl3xcrates.Logger;
import net.pl3x.bukkit.pl3xcrates.api.ItemNBT;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ItemNBTHandler implements ItemNBT {
    public ItemStack setItemNBT(ItemStack bukkitItem, String nbt, String path) {
        if (nbt == null || nbt.isEmpty()) {
            return bukkitItem; // nothing to parse
        }

        net.minecraft.server.v1_10_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        try {
            nmsItem.setTag(MojangsonParser.parse(parseNBT(nbt.split(" ")).toPlainText()));
        } catch (MojangsonParseException e) {
            Logger.error("Error parsing NBT: " + path + ".nbt");
            e.printStackTrace();
        }

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    private IChatBaseComponent parseNBT(String[] nbt) {
        ChatComponentText component = new ChatComponentText("");
        for (int i = 0; i < nbt.length; i++) {
            if (i > 0) {
                component.a(" ");
            }
            component.addSibling(new ChatComponentText(nbt[i]));
        }
        return component;
    }

    public EntityType getEntityType(ItemStack bukkitItem) {
        net.minecraft.server.v1_10_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        if (!nmsItem.hasTag()) {
            return null;
        }

        NBTTagCompound nbt = nmsItem.getTag();
        if (nbt == null) {
            return null;
        }

        NBTTagCompound entityTag = nbt.getCompound("BlockEntityTag");
        if (entityTag == null) {
            return null;
        }

        NBTTagCompound spawnData = entityTag.getCompound("SpawnData");
        if (spawnData == null) {
            return getEntityTypeFromPotentials(entityTag);
        } else {
            EntityType type = getEntityTypeFromId(spawnData);
            if (type == null) {
                return getEntityTypeFromPotentials(entityTag);
            }
            return type;
        }
    }

    private EntityType getEntityTypeFromPotentials(NBTTagCompound nbt) {
        NBTTagList potentials = nbt.getList("SpawnPotentials", 10);
        if (potentials == null || potentials.isEmpty()) {
            return null;
        }

        for (int i = 0; i < potentials.size(); i++) {
            NBTTagCompound entityTag = potentials.get(i).getCompound("Entity");
            if (entityTag == null) {
                continue;
            }

            return getEntityTypeFromId(entityTag);
        }

        return null;
    }

    private EntityType getEntityTypeFromId(NBTTagCompound tag) {
        String entityId = tag.getString("id");
        if (entityId == null || entityId.isEmpty()) {
            return null;
        }

        try {
            return EntityType.valueOf(entityId.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
