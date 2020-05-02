package xyz.oribuin.eternalcrafting.managers;

import dev.esophose.playerparticles.util.StringPlaceholders;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xyz.oribuin.eternalcrafting.EternalCrafting;
import xyz.oribuin.eternalcrafting.menus.MenuSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    private void onReloadCommand(CommandSender sender) {
        MessageManager messageManager = this.plugin.getMessageManager();
        if (!sender.hasPermission("eternalcraft.reload")) {
            messageManager.sendMessage(sender, "invalid-permission");
            return;
        }

        this.plugin.reload();
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.getDescription().getVersion()));
    }

    private void onSettingsCommand(CommandSender sender) {
        MessageManager messageManager = this.plugin.getMessageManager();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.getUniqueId().equals(UUID.fromString("9f7cf461-903f-4ef8-8a88-bdbc49806364"))) {
                sender.sendMessage("&eComing Soon");
                return;
            }

            if (!player.hasPermission("eternalcraft.settings")) {
                messageManager.sendMessage(sender, "invalid-permission");
                return;
            }

            new MenuSettings(player).open();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("eternalcraft"))
            return true;

        MessageManager messageManager = this.plugin.getMessageManager();
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    onReloadCommand(sender);
                    break;
                case "settings":
                    onSettingsCommand(sender);
                    break;
                default:
                    messageManager.sendMessage(sender, "invalid-command");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("eternalcraft"))
            return Collections.emptyList();

        List<String> suggestions = new ArrayList<>();

        if (args.length == 0 || args.length == 1) {
            String subCommandName = args.length == 0 ? "" : args[0];
            List<String> commands = new ArrayList<>();

            if (sender.hasPermission("eternalcraft.reload")) {
                commands.add("reload");
            }
            /*
            if (sender.hasPermission("eternalcraft.settings")) {
                commands.add("settings");
            }
             */

            StringUtil.copyPartialMatches(subCommandName, commands, suggestions);
        }

        return suggestions;
    }

}
