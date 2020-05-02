package xyz.oribuin.eternalcrafting;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.FixedParticleEffect;
import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.styles.ParticleStyle;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrafting.managers.ConfigManager;

import java.util.List;

public class CraftingEvent implements Listener {

    private EternalCrafting plugin = EternalCrafting.getInstance();
    private PlayerParticlesAPI ppAPI = plugin.ppAPI;
    public Location particleLocation;

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack craftedItem = event.getRecipe().getResult();
        Location location = event.getInventory().getLocation();

        if (event.getInventory().getType() != InventoryType.WORKBENCH) return;

        List<String> disabledWorlds = ConfigManager.Setting.DISABLED_WORLDS.getStringList();

        if (!player.hasPermission("eternalcraft.use")
                || player.hasMetadata("vanished")
                || disabledWorlds.contains(player.getWorld().getName())
                || craftedItem.getType() == Material.AIR
                || location == null
                || player.getLocation() == location)
            return;

        if (ConfigManager.Setting.ITEM_BLACKLIST.getStringList().contains(craftedItem.getType().name().toUpperCase())) {
            if (!player.hasPermission("eternalcraft.bypass")) {
                event.setCancelled(true);
            }
        }

        if (location.getWorld() == null) return;
        Location holoLocation = new Location(location.getWorld(), location.getX() + 0.500, location.getY() + 1 + ConfigManager.Setting.HOLOGRAMS_OFFSET.getDouble(), location.getZ() + 0.500);

        if (ConfigManager.Setting.HOLOGRAMS_ENABLED.getBoolean() && Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {

            // Delete existing holograms in location | Prevent hologram stacking
            HologramsAPI.getHolograms(plugin).forEach(hologram -> {
                if (hologram.getLocation().equals(holoLocation) && hologram.size() > 1)
                    hologram.delete();
            });

            Hologram hologram = HologramsAPI.createHologram(plugin, holoLocation);

            if (location.getWorld().getBlockAt(location).getType() != Material.CRAFTING_TABLE)
                return;

            List<String> hologramFormat = ConfigManager.Setting.HOLOGRAMS_FORMAT.getStringList();

            int duration = ConfigManager.Setting.HOLOGRAMS_DURATION.getInt() * 20;

            if (ConfigManager.Setting.HOLOGRAMS_TEXT.getBoolean()) {
                hologramFormat.forEach(line -> hologram.appendTextLine(apply(player, line
                        .replace("%item%", craftedItem.getType().name().toLowerCase().replace("_", " "))
                        .replace("%amount%", String.valueOf(itemAmount(event))))));
            }

            if (ConfigManager.Setting.HOLOGRAMS_ITEMS.getBoolean()) {
                hologram.appendItemLine(craftedItem);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, hologram::delete, duration);
        }

        if (ConfigManager.Setting.PARTICLES_ENABLED.getBoolean() && Bukkit.getPluginManager().getPlugin("PlayerParticles") != null) {
            if (ppAPI == null) return;
            int duration = ConfigManager.Setting.PARTICLES_DURATION.getInt() * 20;

            particleLocation = new Location(holoLocation.getWorld(), holoLocation.getX(), location.getY() + ConfigManager.Setting.PARTICLES_OFFSET.getDouble(), holoLocation.getZ());

            FixedParticleEffect fixedParticle = ppAPI.createFixedParticleEffect(Bukkit.getConsoleSender(), particleLocation,
                    ParticleEffect.fromName(ConfigManager.Setting.PARTICLES_PARTICLE.getString()),
                    ParticleStyle.fromName(ConfigManager.Setting.PARTICLES_STYLE.getString()));

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (fixedParticle != null) {
                    ppAPI.removeFixedEffect(Bukkit.getConsoleSender(), fixedParticle.getId());
                }
            }, duration);
        }

        if (ConfigManager.Setting.SOUNDS_ENABLED.getBoolean()) {
            Sound sound = Sound.valueOf(ConfigManager.Setting.SOUNDS_SOUND.getString());
            int volume = ConfigManager.Setting.SOUNDS_VOLUME.getInt();

            if (ConfigManager.Setting.SOUNDS_PLAYER.getBoolean()) {
                player.playSound(player.getLocation(), sound, volume, 1);
            } else {
                location.getWorld().playSound(holoLocation, sound, volume, 1);
            }
        }

        if (ConfigManager.Setting.COMMANDS_ENABLED.getBoolean()) {
            ConfigManager.Setting.COMMANDS_LIST.getStringList().forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
        }

    }

    @EventHandler
    public void onTableBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();
        if (blockLocation.getWorld() == null) return;

        if (block.getType() != Material.CRAFTING_TABLE) return;

        if (ConfigManager.Setting.HOLOGRAMS_SYNC.getBoolean()) {
            if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null && ConfigManager.Setting.HOLOGRAMS_ENABLED.getBoolean()) {
                for (Hologram hologram : HologramsAPI.getHolograms(plugin)) {
                    Location holoLocation = new Location(blockLocation.getWorld(), blockLocation.getX() + 0.500, blockLocation.getY() + 1 + ConfigManager.Setting.HOLOGRAMS_OFFSET.getDouble(), block.getZ() + 0.500);
                    if (hologram.getLocation().equals(holoLocation))
                        hologram.delete();
                }
            }
        }
    }

    private int itemAmount(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        int possibleCreations;
        int amountCrafted;
        ItemStack result = event.getRecipe().getResult();
        Material resultType = result.getType();
        int resultMaxSize = resultType.getMaxStackSize();
        int resultAmount = result.getAmount();

        if (event.isShiftClick()) {
            possibleCreations = -1;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item == null || item.getType() == Material.AIR)
                    continue;

                if (possibleCreations == -1)
                    possibleCreations = item.getAmount();
                else
                    possibleCreations = Math.min(possibleCreations, item.getAmount());
            }

            int amountThatCanFit = 0;
            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getType() == resultType) {
                    int amount;
                    if (itemStack == null || itemStack.getType() == Material.AIR)
                        amount = resultMaxSize;
                    else
                        amount = resultMaxSize - itemStack.getAmount();

                    amountThatCanFit += amount;
                }
            }

            amountThatCanFit += resultMaxSize - amountThatCanFit % resultAmount;
            amountCrafted = Math.min(resultAmount * possibleCreations, amountThatCanFit);
        } else {
            amountCrafted = resultAmount;
        }

        return amountCrafted;
    }

    private String apply(Player player, String msg) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            return null;

        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, msg));
    }

}
