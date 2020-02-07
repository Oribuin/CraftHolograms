package xyz.oribuin.craftholos;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oribuin.craftholos.cmds.CmdReload;
import xyz.oribuin.craftholos.hooks.Metrics;
import xyz.oribuin.craftholos.listeners.CraftingTable;
import xyz.oribuin.craftholos.listeners.JoinNotification;
import xyz.oribuin.craftholos.persist.Chat;

public class CraftHolograms extends JavaPlugin {

    public void onEnable() {
        /*
         * Variable Defining
         */

        PluginManager pm = Bukkit.getPluginManager();
        /*
         * Registering commands
         */

        getCommand("craftholo").setExecutor(new CmdReload(this));

        /**
         * Registering events
         */
        pm.registerEvents(new JoinNotification(this), this);
        pm.registerEvents(new CraftingTable(this), this);
        /*
         * PlaceholderAPI Stuff
         */

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.getServer().getConsoleSender().sendMessage(Chat.cl("&7[&bCraftHolograms&7] &cPlaceholderAPI is not installed, therefor PlaceholderAPI placeholders will not work."));
        }

        /*
         * BStats Metrics
         */

        int pluginId = 6358;
        Metrics metrics = new Metrics(this, pluginId);

        /*
         * Create the config.yml
         */

        this.saveDefaultConfig();

        /*
         * Startup Message
         */

        this.getServer().getConsoleSender().sendMessage(Chat.cl(
                "\n\n&e******************\n" +
                        "\n&6Plugin: &f" + this.getDescription().getName() +
                        "\n&6Author: &f" + this.getDescription().getAuthors().get(0) +
                        "\n&6Version: &f" + this.getDescription().getVersion() +
                        "\n&6Website: &f" + this.getDescription().getWebsite() +
                        "\n\n&e******************"
        ));
    }
}
