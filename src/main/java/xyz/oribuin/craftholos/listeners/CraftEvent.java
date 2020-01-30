package xyz.oribuin.craftholos.listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import xyz.oribuin.craftholos.CraftHolograms;
import xyz.oribuin.craftholos.hooks.Placeholders;
import xyz.oribuin.craftholos.persist.Chat;

public class CraftEvent implements Listener {

    private CraftHolograms plugin;

    public CraftEvent(CraftHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        /**
         * Variable Defining
         * Variables:
         * Duration until deletion
         * Player who crafted item
         */
        int duration = plugin.getConfig().getInt("duration") * 20;
        Player player = (Player) event.getInventory().getViewers().get(0);

        /*
         * Should the plugin be enabled?
         */
        if (!plugin.getConfig().getBoolean("plugin-enabled", true)) return;

        /*
         * If a player does not have permission to use the plugin
         * do nothing
         */

        if (!player.hasPermission("craftholo.use")) return;


        if (event.getInventory().getType() != InventoryType.WORKBENCH) return;

        System.out.print(event.getInventory().getLocation());

        /*
         * Defining location settings and variables
         * if the location is null: do nothing
         * if the location = player location: do nothing
         * if the world is disabled: do nothing
         */
        Location location = event.getInventory().getLocation();
        if (location == null) return;

        if (plugin.getConfig().getStringList("disabled-worlds").contains(location.getWorld().getName())) return;

        /*
         * Center the hologram in the middle of the Crafting Table
         */

        location.setY(location.getY() + plugin.getConfig().getInt("y-axis"));
        location.setX(location.getX() + 0.500);
        location.setZ(location.getZ() + 0.500);

        /*
         * Create the hologram at the correct location
         * Define the result variable
         */
        Hologram holo = HologramsAPI.createHologram(plugin, location);
        Material result = event.getRecipe().getResult().getType();

        if (event.getInventory().getLocation().getWorld() != null) {
            if (event.getInventory().getLocation().getWorld().getBlockAt(event.getInventory().getLocation()).getType() != Material.CRAFTING_TABLE)
                return;
        }

        for (String line : plugin.getConfig().getStringList("hologram-format")) {
            holo.appendTextLine(Placeholders.apply(player, Chat.cl(line).replaceAll("\\{result}", result.name().replaceAll("_", " ").toLowerCase())));
        }

        if (plugin.getConfig().getBoolean("item-display", true))
            holo.appendItemLine(event.getRecipe().getResult());

        /*
         * Wait (duration) seconds before deleting hologram
         */
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, holo::delete, duration);

    }
}
