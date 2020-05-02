package xyz.oribuin.eternalcrafting.menus;

import dev.rosewood.guiframework.GuiFactory;
import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.gui.ClickAction;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiIcon;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.FileUtil;
import org.graalvm.compiler.nodes.FieldLocationIdentity;
import xyz.oribuin.eternalcrafting.EternalCrafting;
import xyz.oribuin.eternalcrafting.managers.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuSettings {
    private EternalCrafting plugin = EternalCrafting.getInstance();
    private GuiFramework guiFramework;
    private GuiContainer guiContainer;
    private Player player;

    public MenuSettings(Player player) {
        this.guiFramework = GuiFramework.instantiate(plugin);
        this.guiContainer = null;
        this.player = player;
    }

    public void open() {
        if (this.isInvalid())
            this.buildGui();
        this.guiContainer.openFor(player);
    }

    public void buildGui() {

        this.guiContainer = GuiFactory.createContainer();
        List<Integer> guiSlots = new ArrayList<>();
        for (int i = 0; i <= 26; i++)
            guiSlots.add(i);

        ItemStack fillerItem = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            fillerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            fillerItem.setItemMeta(fillerMeta);
        }

        // Main Screen
        GuiScreen mainScreen = GuiFactory.createScreen(this.guiContainer, GuiSize.ROWS_THREE)
                .setTitle("EternalCrafting | Settings");

        // Hologram Screen
        GuiScreen hologramScreen = GuiFactory.createScreen(this.guiContainer, GuiSize.ROWS_THREE)
                .setTitle("Settings | Holograms");

        for (int slot : guiSlots) {
            mainScreen.addItemStackAt(slot, fillerItem);
            hologramScreen.addItemStackAt(slot, fillerItem);
        }
        // Main Screen | Items

        // Sound Settings
        mainScreen.addButtonAt(11, GuiFactory.createButton()
                .setName("&eSound Settings")
                .setGlowing(true)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER)
                .setIcon(Material.MUSIC_DISC_STRAD)
                .setLore(
                        "&bLeft Click » &fToggle Sound Enabled",
                        "&bRight Click » &fToggle Player Only")

                .setClickAction(event -> {

                    if (event.getClick().isLeftClick()) {
                        if (ConfigManager.Setting.SOUNDS_ENABLED.getBoolean())
                            this.plugin.getConfig().set("sound-settings.enabled", false);
                        else
                            this.plugin.getConfig().set("sound-settings.enabled", false);

                        this.save();
                        event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Sound Enabled: " + ChatColor.AQUA + ConfigManager.Setting.SOUNDS_ENABLED.getBoolean());
                    } else if (event.getClick().isRightClick()) {

                        if (ConfigManager.Setting.SOUNDS_ENABLED.getBoolean())
                            this.plugin.getConfig().set("sound-settings.player-only", false);
                        else
                            this.plugin.getConfig().set("sound-settings.player-only", false);

                        this.save();
                        event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Player Only: " + ChatColor.AQUA + ConfigManager.Setting.SOUNDS_PLAYER.getBoolean());
                    }

                    return ClickAction.CLOSE;
                }));

        // Hologram Settings
        mainScreen.addButtonAt(13, GuiFactory.createButton()
                .setName("&eHologram Settings")
                .setGlowing(true)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER)
                .setIcon(Material.OAK_SIGN)
                .setClickAction(event -> ClickAction.TRANSITION_FORWARDS));

        // Particle Settings
        mainScreen.addButtonAt(15, GuiFactory.createButton()
                .setName("&eParticle Settings")
                .setGlowing(true)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER)
                .setIcon(Material.BLAZE_POWDER)
                .setClickAction(event -> {
                    if (ConfigManager.Setting.PARTICLES_ENABLED.getBoolean())
                        this.plugin.getConfig().set("particle-settings.enabled", false);
                    else
                        this.plugin.getConfig().set("particle-settings.enabled", false);

                    this.save();
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Particles Enabled: " + ChatColor.AQUA + ConfigManager.Setting.SOUNDS_ENABLED.getBoolean());

                    return ClickAction.CLOSE;
                }));

        // Hologram Settings | Enabled
        hologramScreen.addButtonAt(11, GuiFactory.createButton()
                .setName("&eHolograms Enabled")
                .setIcon(Material.PAPER)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER)
                .setGlowing(ConfigManager.Setting.HOLOGRAMS_ENABLED.getBoolean())
                .setClickAction(event -> {
                    if (ConfigManager.Setting.HOLOGRAMS_ENABLED.getBoolean())
                        this.plugin.getConfig().set("hologram-settings.enabled", false);
                    else
                        this.plugin.getConfig().set("hologram-settings.enabled", false);

                    this.save();
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Holograms Enabled: " + ChatColor.AQUA + ConfigManager.Setting.HOLOGRAMS_ENABLED.getBoolean());

                    return ClickAction.CLOSE;
                }));

        // Hologram Settings | Sync
        hologramScreen.addButtonAt(12, GuiFactory.createButton()
                .setName("&eHolograms Synced")
                .setIcon(Material.PAPER)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER)
                .setGlowing(ConfigManager.Setting.HOLOGRAMS_SYNC.getBoolean())
                .setClickAction(event -> {
                    if (ConfigManager.Setting.HOLOGRAMS_SYNC.getBoolean())
                        this.plugin.getConfig().set("hologram-settings.sync-holograms", false);
                    else
                        this.plugin.getConfig().set("hologram-settings.sync-holograms", false);

                    this.save();
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Holograms Synced: " + ChatColor.AQUA + ConfigManager.Setting.HOLOGRAMS_SYNC.getBoolean());

                    return ClickAction.CLOSE;
                }));

        // Hologram Settings | Text Display
        hologramScreen.addButtonAt(14, GuiFactory.createButton()
                .setName("&eHolograms Text Display")
                .setIcon(Material.PAPER)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER)
                .setGlowing(ConfigManager.Setting.HOLOGRAMS_TEXT.getBoolean())
                .setClickAction(event -> {
                    if (ConfigManager.Setting.HOLOGRAMS_TEXT.getBoolean())
                        this.plugin.getConfig().set("hologram-settings.text-display", false);
                    else
                        this.plugin.getConfig().set("hologram-settings.text-display", false);

                    this.save();
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Holograms Text: " + ChatColor.AQUA + ConfigManager.Setting.HOLOGRAMS_TEXT.getBoolean());

                    return ClickAction.CLOSE;
                }));

        // Hologram Settings | Item Display
        hologramScreen.addButtonAt(15, GuiFactory.createButton()
                .setName("&eHolograms Item Display")
                .setIcon(Material.PAPER)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER)
                .setGlowing(ConfigManager.Setting.HOLOGRAMS_ITEMS.getBoolean())
                .setClickAction(event -> {
                    if (ConfigManager.Setting.HOLOGRAMS_ITEMS.getBoolean())
                        this.plugin.getConfig().set("hologram-settings.item-display", false);
                    else
                        this.plugin.getConfig().set("hologram-settings.item-display", false);

                    this.save();
                    event.getWhoClicked().sendMessage(ChatColor.YELLOW + "Holograms Items: " + ChatColor.AQUA + ConfigManager.Setting.HOLOGRAMS_ITEMS.getBoolean());

                    return ClickAction.CLOSE;
                }));

        hologramScreen.addButtonAt(13, GuiFactory.createButton()
                .setName("&cGo Back")
                .setIcon(Material.BARRIER)
                .setGlowing(true)
                .setClickAction(event -> ClickAction.TRANSITION_BACKWARDS)
                .setClickSound(Sound.ENTITY_ARROW_HIT_PLAYER));

        guiContainer.addScreen(mainScreen);
        guiContainer.addScreen(hologramScreen);
        this.guiFramework.getGuiManager().registerGui(this.guiContainer);
    }

    public boolean isInvalid() {
        return this.guiContainer == null || !this.guiFramework.getGuiManager().getActiveGuis().contains(this.guiContainer);
    }

    private void save() {
        this.plugin.saveDefaultConfig();
    }
}
