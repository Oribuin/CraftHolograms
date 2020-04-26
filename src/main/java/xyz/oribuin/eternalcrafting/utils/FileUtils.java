package xyz.oribuin.eternalcrafting.utils;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    public static void createFile(Plugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try (InputStream inputStream = plugin.getResource(fileName)) {
                if (inputStream == null) {
                    file.createNewFile();
                    return;
                }

                Files.copy(inputStream, Paths.get(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
