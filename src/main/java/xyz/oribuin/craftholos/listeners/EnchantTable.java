package xyz.oribuin.craftholos.listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sun.tools.javac.jvm.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.craftholos.CraftHolograms;
import xyz.oribuin.craftholos.hooks.Placeholders;
import xyz.oribuin.craftholos.persist.Chat;

public class EnchantTable implements Listener {

    public CraftHolograms plugin;
    public EnchantTable(CraftHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {

        int duration = plugin.getConfig().getInt("enchant-table.duration") * 20;

        if (!plugin.getConfig().getBoolean("plugin-enabled", true)) return;

        if (!plugin.getConfig().getBoolean("enchant-table.enabled", true)) return;

        Player player = event.getEnchanter();
        if (!player.hasPermission("craftholo.use")) return;

        Location location = event.getEnchantBlock().getLocation();
        if (location == null) return;
        if (plugin.getConfig().getStringList("disabled-worlds").contains(location.getWorld().getName())) return;

        location.setY(location.getY() + plugin.getConfig().getInt("enchant-table.y-ais"));
        location.setX(location.getX() + 0.500);
        location.setZ(location.getZ() + 0.500);



        HologramsAPI.getHolograms(plugin).forEach(hologram -> {
           if (hologram.getLocation().equals(location) && hologram.size() > 1) {
               hologram.delete();
            }
        });

        Hologram holo = HologramsAPI.createHologram(plugin, location);

        for (String line : plugin.getConfig().getStringList("enchant-table.hologram-format")) {
            if (plugin.getConfig().getBoolean("crafting-table.text-enabled", true)) {
                holo.appendTextLine(Placeholders.apply(player, Chat.cl(line)));
            }
        }

        if (plugin.getConfig().getBoolean("enchant-table.item-settings.display")) {
            if (plugin.getConfig().getString("enchant-table.item-settings.item-value").equalsIgnoreCase("CHOSEN")) {
                ItemStack itemStack = new ItemStack(Material.valueOf(plugin.getConfig().getString("enchant-table.item-settings.item-type")));
                holo.appendItemLine(itemStack);
            } else if (plugin.getConfig().getString("enchant-table.item-settings.item-value").equalsIgnoreCase("ENCHANTED-ITEM")) {
                holo.appendItemLine(event.getItem());
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, holo::delete, duration);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType().equals(Material.ENCHANTMENT_TABLE)) {
            if (plugin.getConfig().getBoolean("enchant-table.sync-holograms", true)) {
                HologramsAPI.getHolograms(plugin).forEach(hologram -> {
                    Location blockLocation = block.getLocation();
                    Location holoLocation = hologram.getLocation();

                    if (holoLocation.getY() == blockLocation.getY() + plugin.getConfig().getInt("enchant-table.y-axis")
                            && holoLocation.getX() - 0.500 == block.getX()
                            && holoLocation.getZ() - 0.500 == block.getZ()) {
                        hologram.delete();
                    }
                });
            }
        }
    }
}
