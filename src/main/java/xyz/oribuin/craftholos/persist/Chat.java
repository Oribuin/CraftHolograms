package xyz.oribuin.craftholos.persist;

import org.bukkit.ChatColor;

public class Chat {
    public static String cl(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
