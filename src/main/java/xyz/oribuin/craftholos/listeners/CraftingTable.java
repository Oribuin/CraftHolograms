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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.craftholos.CraftHolograms;
import xyz.oribuin.craftholos.hooks.Placeholders;
import xyz.oribuin.craftholos.persist.Chat;

public class CraftingTable implements Listener {

    private CraftHolograms plugin;

    public CraftingTable(CraftHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        /*
         * Variable Defining
         * Variables:
         * Duration until deletion
         * Player who crafted item
         */
        int duration = plugin.getConfig().getInt("crafting-table.duration") * 20;
        Player player = (Player) event.getInventory().getViewers().get(0);

        /*
         * Should the plugin be enabled?
         */
        if (!plugin.getConfig().getBoolean("plugin-enabled", true)) return;

        if (!plugin.getConfig().getBoolean("crafting-table.enabled", true)) return;

        if (!player.hasPermission("craftholo.use")) return;
        /*
         * If a player does not have permission to use the plugin
         * do nothing
         */


        if (event.getInventory().getType() == InventoryType.WORKBENCH) {

            int possibleCreations = 1;
            int amountCrafted;
            ItemStack result = event.getRecipe().getResult();
            Material resultType = result.getType();
            int resultMaxStackSize = resultType.getMaxStackSize();
            int resultAmount = result.getAmount();

            if (event.isShiftClick()) {
                possibleCreations = -1;
                for (ItemStack item : event.getInventory().getMatrix()) {
                    if (item == null || item.getType() == Material.AIR)
                        continue;

                    if (possibleCreations == -1) {
                        possibleCreations = item.getAmount();
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }

                int amountThatCanFit = 0;
                for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                    if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getType() == resultType) {

                        int amount;
                        if (itemStack == null || itemStack.getType() == Material.AIR) {
                            amount = resultMaxStackSize;
                        } else {
                            amount = resultMaxStackSize - itemStack.getAmount();
                        }

                        amountThatCanFit += amount;
                    }
                }

                amountThatCanFit += resultAmount - amountThatCanFit % resultAmount;
                amountCrafted = Math.min(resultAmount * possibleCreations, amountThatCanFit);
            } else {
                amountCrafted = resultAmount;
            }

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

            location.setY(location.getY() + plugin.getConfig().getInt("crafting-table.y-axis"));
            location.setX(location.getX() + 0.500);
            location.setZ(location.getZ() + 0.500);


            HologramsAPI.getHolograms(plugin).forEach(hologram -> {
                if (hologram.getLocation().equals(location) && hologram.size() > 1) {
                    hologram.delete();
                }
            });
            /*
             * Create the hologram at the correct location
             * Define the result variable
             */
            Hologram holo = HologramsAPI.createHologram(plugin, location);

            if (event.getInventory().getLocation().getWorld() != null) {
                if (event.getInventory().getLocation().getWorld().getBlockAt(event.getInventory().getLocation()).getType() != Material.CRAFTING_TABLE)
                    return;
            }

            for (String line : plugin.getConfig().getStringList("crafting-table.hologram-format")) {
                if (plugin.getConfig().getBoolean("crafting-table.text-enabled", true)) {
                    holo.appendTextLine(Placeholders.apply(player, Chat.cl(line).replaceAll("\\{result}", result.getType().name().replaceAll("_", " ").toLowerCase())
                    .replaceAll("\\{amount}", "" + amountCrafted)));
                }
            }

            if (plugin.getConfig().getBoolean("crafting-table.item-display", true)) {
                holo.appendItemLine(event.getRecipe().getResult());
            }

            /*
             * Wait (duration) seconds before deleting hologram
             */
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, holo::delete, duration);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType().equals(Material.CRAFTING_TABLE)) {
            if (plugin.getConfig().getBoolean("crafting-table.sync-holograms", true)) {
                HologramsAPI.getHolograms(plugin).forEach(hologram -> {
                    Location blockLocation = block.getLocation();
                    Location holoLocation = hologram.getLocation();

                    if (holoLocation.getY() == blockLocation.getY() + plugin.getConfig().getInt("crafting-table.y-axis")
                            && holoLocation.getX() - 0.500 == block.getX()
                            && holoLocation.getZ() - 0.500 == block.getZ()) {
                        hologram.delete();
                    }
                });
            }
        }
    }
}
