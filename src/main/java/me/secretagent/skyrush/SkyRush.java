package me.secretagent.skyrush;

import co.aikar.commands.BukkitCommandManager;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import me.secretagent.skyrush.commands.SkyRushCommand;
import me.secretagent.skyrush.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyRush extends JavaPlugin {

    private static SkyRush plugin;
    private SlimePlugin slimeAPI;
    private SlimeLoader slimeLoader;

    @Override
    public void onEnable() {
        plugin = this;
        slimeAPI = (SlimePlugin) getServer().getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimeAPI.getLoader("file");
        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.registerCommand(new SkyRushCommand());
        FileUtil.mkDirs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SkyRush getPlugin() {
        return plugin;
    }

    public SlimePlugin getSlimeAPI() {
        return slimeAPI;
    }

    public SlimeLoader getSlimeLoader() {
        return slimeLoader;
    }

}
