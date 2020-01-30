package xyz.oribuin.craftholos.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.craftholos.CraftHolograms;

public class Placeholders {
    CraftHolograms plugin;

    private static Boolean enabled;

    public Placeholders(CraftHolograms plugin) {
        this.plugin = plugin;
    }

    public static boolean enabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static String apply(Player player, String text) {
        if (enabled())
            return PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }
}
