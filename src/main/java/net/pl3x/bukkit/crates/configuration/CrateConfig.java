package net.pl3x.bukkit.crates.configuration;

import net.pl3x.bukkit.crates.ItemUtil;
import net.pl3x.bukkit.crates.Logger;
import net.pl3x.bukkit.crates.crate.CrateType;
import net.pl3x.bukkit.crates.crate.Reward;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrateConfig extends YamlConfiguration {
    private final File file;

    public CrateConfig(File file) {
        this.file = file;
    }

    public void load() throws IOException, InvalidConfigurationException {
        load(file);
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return getString("name", file.getName().split("\\.(?=[^.]+$)")[0]);
    }

    public CrateType getType() {
        return CrateType.valueOf(getString("type", "SLIDE_FANCY").toUpperCase());
    }

    public boolean isDisabled() {
        return !getBoolean("enabled", false);
    }

    public boolean isOutOfOrder() {
        return getBoolean("out-of-order", false);
    }

    public ItemStack getKey() {
        return ItemUtil.getItemStack(getConfigurationSection("key-item"));
    }

    public Set<Material> getAllowedBlocks() {
        Set<Material> allowedBlocks = new HashSet<>();
        for (String materialName : getStringList("allowed-blocks")) {
            Material material = ItemUtil.getMaterial(materialName);
            if (material != null) {
                allowedBlocks.add(material);
            }
        }
        return allowedBlocks;
    }

    public List<String> getHologramText() {
        List<String> text = getStringList("hologram.text");
        if (text.isEmpty()) {
            text.addAll(Lang.DEFAULT_HOLOGRAM_TEXT);
        }
        for (int i = 0; i < text.size(); i++) {
            text.set(i, ChatColor.translateAlternateColorCodes('&', text.get(i)));
        }
        return text;
    }

    public Vector getHologramOffset() {
        Vector offset = new Vector(0.5, 2.0, 0.5);
        offset.setX(offset.getX() + getDouble("hologram.offset.x", 0));
        offset.setY(offset.getY() + getDouble("hologram.offset.y", 0));
        offset.setZ(offset.getZ() + getDouble("hologram.offset.z", 0));
        return offset;
    }

    public void getEffects() {
        // idk yet...
    }

    public Vector getKnockback() {
        if (isSet("effects.knockback")) {
            try {
                double x = getDouble("effects.knockback.x");
                double y = getDouble("effects.knockback.y");
                double z = getDouble("effects.knockback.z");
                return new Vector(x, y, z);
            } catch (NumberFormatException e) {
                Logger.warn("Error parsing knockback effect values: " + file.getAbsolutePath());
            }
        }
        return null;
    }

    public List<Reward> getRewards() {
        List<Reward> rewards = new ArrayList<>();

        ConfigurationSection rewardSections = getConfigurationSection("rewards");
        if (rewardSections == null) {
            return rewards;
        }

        Set<String> rewardKeys = rewardSections.getKeys(false);
        for (String rewardKey : rewardKeys) {
            ConfigurationSection rewardSection = rewardSections.getConfigurationSection(rewardKey);
            if (rewardSection == null) {
                continue;
            }

            ItemStack item = ItemUtil.getItemStack(rewardSection.getConfigurationSection("item"));
            List<String> commands = rewardSection.getStringList("commands");
            int chance = rewardSection.getInt("chance", 0);
            ItemStack display = ItemUtil.getItemStack(rewardSection.getConfigurationSection("display"), item == null ? null : item.clone(), chance);

            rewards.add(new Reward(display, item, commands, chance));
        }

        return rewards;
    }
}
