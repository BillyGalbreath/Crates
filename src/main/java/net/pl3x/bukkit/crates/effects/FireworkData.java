package net.pl3x.bukkit.crates.effects;

import net.pl3x.bukkit.crates.Crates;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkData {
    private final FireworkMeta fireworkMeta;
    private final int duration;

    public FireworkData(ConfigurationSection fireworksSection) {
        ItemStack item = Crates.getPlugin().getNBTHandler().setItemNBT(
                new ItemStack(Material.FIREWORK_ROCKET),
                fireworksSection.getString("nbt"),
                fireworksSection.getCurrentPath());

        this.fireworkMeta = (FireworkMeta) item.getItemMeta();
        this.duration = fireworksSection.getInt("duration", 0);
    }

    public FireworkMeta getFireworkMeta() {
        return fireworkMeta;
    }

    public int getDuration() {
        return duration;
    }
}
