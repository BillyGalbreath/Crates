package net.pl3x.bukkit.crates;

import net.pl3x.bukkit.crates.nms.ItemNBTHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtil {
    public static void giveItem(Player player, ItemStack itemStack) {
        Logger.debug(player.getName() + " received item at their feet! " + itemStack.toString());
        player.getInventory().addItem(itemStack).forEach((index, overflow) -> {
            Item item = player.getWorld().dropItem(player.getLocation(), overflow);
            item.setOwner(player.getUniqueId());
            item.setPickupDelay(0);
        });
    }

    public static boolean takeItem(Player player, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            item = new ItemStack(Material.AIR);
        }
        player.getInventory().setItemInMainHand(item);
        return true;
    }

    public static ItemStack getItemStack(ConfigurationSection section) {
        return getItemStack(section, null, null);
    }

    public static ItemStack getItemStack(ConfigurationSection section, ItemStack itemStack, Integer chance) {
        if (section == null) {
            return null;
        }

        Material material = getMaterial(section.getString("material"));
        if (material == null && itemStack == null) {
            return null;
        }

        if (itemStack == null) {
            itemStack = new ItemStack(material);
        }

        if (itemStack.getAmount() == 1) {
            int amount = section.getInt("amount", 1);
            itemStack.setAmount(amount);
        }

        itemStack = ItemNBTHandler.setItemNBT(itemStack, section.getString("nbt"), section.getCurrentPath());

        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = section.getString("name");
        if (name != null && !name.isEmpty()) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        List<String> lore = section.getStringList("lore");
        if (lore != null && !lore.isEmpty()) {
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&',
                        lore.get(i).replace("{chance}", chance == null ? "" : chance.toString())));
            }
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static Material getMaterial(String materialName) {
        if (materialName == null) {
            return null;
        }

        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            return null;
        }

        return material;
    }
}
