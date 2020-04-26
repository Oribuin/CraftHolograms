package xyz.oribuin.eternalcrafting.managers;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.eternalcrafting.EternalCrafting;

import java.util.List;

public class ConfigManager extends Manager {

    public ConfigManager(EternalCrafting plugin) {
        super(plugin);
        this.reload();
    }

    public enum Setting {
        // Misc Settings
        DISABLED_WORLDS("disabled-worlds"),

        // Hologram Settings
        HOLOGRAMS_ENABLED("hologram-settings.enabled"),
        HOLOGRAMS_OFFSET("hologram-settings.y-offset"),
        HOLOGRAMS_DURATION("hologram-settings.duration"),
        HOLOGRAMS_SYNC("hologram-settings.sync-holograms"),
        HOLOGRAMS_TEXT("hologram-settings.text-display"),
        HOLOGRAMS_ITEMS("hologram-settings.item-display"),
        HOLOGRAMS_ITEM_TYPE("hologram-settings.item-settings.type"),
        HOLOGRAMS_FORMAT("hologram-settings.format"),

        // Sound Settings
        SOUNDS_ENABLED("sound-settings.enabled"),
        SOUNDS_PLAYER("sound-settings.player-only"),
        SOUNDS_SOUND("sound-settings.sound"),
        SOUNDS_VOLUME("sound-settings.volume"),

        // Particle Settings
        PARTICLES_ENABLED("particle-settings.enabled"),
        PARTICLES_PARTICLE("particle-settings.particle"),
        PARTICLES_DURATION("particle-settings.duration"),
        PARTICLES_STYLE("particle-settings.style"),
        PARTICLES_OFFSET("particle-settings.y-offset");


        private final String key;
        private Object value = null;

        Setting(String key) {
            this.key = key;
        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            return (boolean) this.value;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            return (String) this.value;
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            return (List<String>) this.value;
        }

        /**
         * Loads the value from the config and caches it
         */
        private void load(FileConfiguration config) {
            this.value = config.get(this.key);
        }

    }

    @Override
    public void reload() {
        this.plugin.reloadConfig();
        this.plugin.saveDefaultConfig();

        FileConfiguration config = this.plugin.getConfig();
        for (Setting value : Setting.values())
            value.load(config);
    }
}