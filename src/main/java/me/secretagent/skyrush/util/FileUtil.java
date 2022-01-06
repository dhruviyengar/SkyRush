package me.secretagent.skyrush.util;

import me.secretagent.skyrush.SkyRush;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileUtil {

    private static final SkyRush plugin = SkyRush.getPlugin();

    public static void mkDirs() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        if (!getMapsFolder().exists()) getMapsFolder().mkdir();
    }

    public static File getMapsFolder() {
        return new File(plugin.getDataFolder(), "maps");
    }

    public static YamlConfiguration getMapConfig(String mapName) {
        return YamlConfiguration.loadConfiguration(new File(getMapsFolder(), mapName + ".yml"));
    }

}
