package xyz.oribuin.craftholos.cmds;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.craftholos.CraftHolograms;
import xyz.oribuin.craftholos.persist.Chat;

public class CmdReload implements CommandExecutor {
    CraftHolograms plugin;

    public CmdReload(CraftHolograms instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            /*
             * If the user does not have permission to use the reload command.
             * Print "no-permission" message to the player.
             */
            if (!player.hasPermission("craftholo.reload")) {
                player.sendMessage(Chat.cl(config.getString("no-permission")));
                return true;
            }
        }

        // Reload the config.
        plugin.reloadConfig();
        if (HologramsAPI.getHolograms(plugin).size() > 0) {
            HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
        }

        sender.sendMessage(Chat.cl(config.getString("reload").replaceAll("\\{version}", plugin.getDescription().getVersion())));
        // Notify Console that the plugin was reloaded.
        Bukkit.getConsoleSender().sendMessage(Chat.cl("&bReloaded " + plugin.getDescription().getName() + " &f(&b" + plugin.getDescription().getVersion() + "&f)"));
        return true;
    }

}