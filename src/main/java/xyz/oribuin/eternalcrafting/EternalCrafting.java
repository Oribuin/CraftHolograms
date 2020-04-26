package xyz.oribuin.eternalcrafting;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.FixedParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oribuin.eternalcrafting.managers.CommandManager;
import xyz.oribuin.eternalcrafting.managers.ConfigManager;
import xyz.oribuin.eternalcrafting.managers.MessageManager;

public class EternalCrafting extends JavaPlugin {

    private static EternalCrafting instance;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    public PlayerParticlesAPI ppAPI;

    public static EternalCrafting getInstance() {
        return instance;
    }

    public void onEnable() {
        PluginManager pm = Bukkit.getPluginManager();

        instance = this;
        this.commandManager = new CommandManager(this);
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);

        this.reload();

        if (pm.getPlugin("PlayerParticles") != null) {
            this.ppAPI = PlayerParticlesAPI.getInstance();
        } else {
            Bukkit.getLogger().warning("PlayerParticles Not Installed - Particles are disabled.");
        }

        if (pm.getPlugin("PlaceholderAPI") == null) {
            Bukkit.getLogger().warning("PlaceholderAPI Not Installed - Placeholders are disabled.");
        }

        if (pm.getPlugin("HolographicDisplays") == null) {
            Bukkit.getLogger().warning("HolographicDisplays Not Installed - Holograms are disabled.");
        }

        pm.registerEvents(new CraftingEvent(), this);
    }

    @Override
    public void onDisable() {
        this.reload();

        if (ppAPI != null) {
            ppAPI.removeFixedEffectsInRange(new CraftingEvent().particleLocation, 1);
        }
    }
    public void reload() {
        this.commandManager.reload();
        this.configManager.reload();
        this.messageManager.reload();
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }
}
