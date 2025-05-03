package me.irxngo.silversystem;

import me.irxngo.silversystem.commands.SilverCommand;
import me.irxngo.silversystem.commands.TopSilverCommand;
import me.irxngo.silversystem.database.Database;
import me.irxngo.silversystem.listeners.JoinQuitListener;
import me.irxngo.silversystem.listeners.ShopListener;
import me.irxngo.silversystem.placeholders.SilverExpansion;
import me.irxngo.silversystem.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SilverSystem extends JavaPlugin {

    private static SilverSystem instance;
    private Database silverDatabase;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        if (Bukkit.getPluginManager().getPlugin("BedWars2023") == null) {
            getLogger().severe("BedWars2023 not found! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.configManager = new ConfigManager(this);
        configManager.setupConfig();

        this.silverDatabase = new Database(this);
        silverDatabase.initialize();

        getCommand("silver").setExecutor(new SilverCommand(this));
        getCommand("topsilver").setExecutor(new TopSilverCommand(this));

        Bukkit.getPluginManager().registerEvents(new ShopListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SilverExpansion(this).register();
        }

        getLogger().info("SilverSystem has been enabled!");
    }

    @Override
    public void onDisable() {
        if (silverDatabase != null) {
            silverDatabase.close();
        }
        getLogger().info("SilverSystem has been disabled!");
    }

    public static SilverSystem getInstance() {
        return instance;
    }

    public Database getSilverDatabase() {
        return silverDatabase;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}