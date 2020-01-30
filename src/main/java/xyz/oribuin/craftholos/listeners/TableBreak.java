package xyz.oribuin.craftholos.listeners;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.oribuin.craftholos.CraftHolograms;

public class TableBreak implements Listener {
    public CraftHolograms plugin;

    public TableBreak(CraftHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType().equals(Material.CRAFTING_TABLE)) {
            if (plugin.getConfig().getBoolean("sync-holograms", true)) {
                HologramsAPI.getHolograms(plugin).forEach(hologram -> {
                    Location blockLocation = block.getLocation();
                    Location holoLocation = hologram.getLocation();

                    if (holoLocation.getY() == blockLocation.getY() + plugin.getConfig().getInt("y-axis")
                            && holoLocation.getX() - 0.500 == block.getX()
                            && holoLocation.getZ() - 0.500 == block.getZ()) {
                        hologram.delete();
                    }
                });
            }
        }
    }
}
