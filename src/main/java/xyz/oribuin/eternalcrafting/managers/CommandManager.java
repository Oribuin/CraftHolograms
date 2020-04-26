package xyz.oribuin.eternalcrafting.managers;

import dev.esophose.playerparticles.util.StringPlaceholders;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrafting.EternalCrafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager extends Manager implements TabExecutor {

    public CommandManager(EternalCrafting plugin) {
        super(plugin);

        PluginCommand eternalCommand = this.plugin.getCommand("eternalcraft");
        if (eternalCommand != null) {
            eternalCommand.setExecutor(this);
            eternalCommand.setTabCompleter(this);
        }
    }

    @Override
    public void reload() {
        // Unused
    }

    public void onReloadCommand(CommandSender sender) {
        MessageManager messageManager = this.plugin.getMessageManager();
        if (!sender.hasPermission("eternalcraft.reload")) {
            messageManager.sendMessage(sender, "invalid-permission");
            return;
        }

        this.plugin.reload();
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.getDescription().getVersion()));
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("eternalcraft"))
            return true;

        MessageManager messageManager = this.plugin.getMessageManager();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                onReloadCommand(sender);
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("trails"))
            return Collections.emptyList();

        List<String> commands = new ArrayList<>();

        if (sender.hasPermission("eternalcraft.reload") && args.length == 1) {
            commands.add("reload");
        }
        return commands;
    }

}
