package net.pl3x.bukkit.pl3xcrates.listener;

import net.pl3x.bukkit.pl3xcrates.ItemUtil;
import net.pl3x.bukkit.pl3xcrates.Pl3xCrates;
import net.pl3x.bukkit.pl3xcrates.configuration.Lang;
import net.pl3x.bukkit.pl3xcrates.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {
    private Pl3xCrates plugin;

    public CrateListener(Pl3xCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClickCrate(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only listen to main hand
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return; // only listen to left click blocks
        }

        Block block = event.getClickedBlock();
        Crate crate = plugin.getCrateManager().getCrate(block.getLocation());
        if (crate == null) {
            return; // not a crate
        }

        // cancel interact
        event.setCancelled(true);

        Player player = event.getPlayer();
        if (crate.isOutOfOrder()) {
            Lang.send(player, Lang.OUT_OF_ORDER);
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!crate.isKey(hand) || !ItemUtil.takeItem(player, hand)) {
            if (crate.getKnockback() != null) {
                player.setVelocity(crate.getKnockback().clone().normalize().multiply(-1)
                        .multiply(block.getLocation().add(0.5, 0, 0.5).toVector().subtract(player.getLocation().toVector()))
                        .setY(crate.getKnockback().getY()));
            }
            Lang.send(player, Lang.MUST_HAVE_KEY);
            return;
        }

        crate.doOpen(block.getLocation(), player);
    }

    @EventHandler
    public void onLeftClickCrate(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // only listen to main hand
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return; // only listen to left click blocks
        }

        Block block = event.getClickedBlock();
        Crate crate = plugin.getCrateManager().getCrate(block.getLocation());
        if (crate == null) {
            return; // not a crate
        }

        event.getPlayer().openInventory(crate.getInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Crate crate = plugin.getCrateManager().getCrate(event.getInventory());
        if (crate == null) {
            return; // not a crate
        }

        // cancel gui interactions
        event.setCancelled(true);

        // close gui on attempt to shift click
        if (event.isShiftClick()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> event.getWhoClicked().closeInventory(), 1);
        }
    }

    @EventHandler
    public void onPistonPushCrate(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Crate crate = plugin.getCrateManager().getCrate(block.getLocation());
            if (crate != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonPullCrate(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            Crate crate = plugin.getCrateManager().getCrate(block.getLocation());
            if (crate != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCrateBreak(BlockBreakEvent event) {
        Crate crate = plugin.getCrateManager().getCrate(event.getBlock().getLocation());
        if (crate == null) {
            return; // not a crate
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onKeyPlace(BlockPlaceEvent event) {
        Crate crate = plugin.getCrateManager().getCrate(event.getItemInHand());
        if (crate == null) {
            return; // not a key
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Item item = event.getItem();

        String owner = ItemUtil.getLockedOwner(item);
        if (owner == null || owner.isEmpty() || owner.equalsIgnoreCase("none")) {
            return; // no owner
        }

        if (owner.equals(event.getPlayer().getUniqueId().toString())) {
            return; // owner picked up
        }

        // not owner, cancel pickup event
        event.setCancelled(true);
    }
}
