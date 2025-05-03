package me.irxngo.silversystem.utils;

import me.irxngo.silversystem.SilverSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final SilverSystem plugin;
    private FileConfiguration config;
    private final Map<String, String> messages = new HashMap<>();

    public ConfigManager(SilverSystem plugin) {
        this.plugin = plugin;
    }

    public void setupConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        loadMessages();
    }

    private void loadMessages() {
        messages.put("silver-earned", "&a+{amount} silver &7(Spent {amount} {currency}) &8| &fTotal: &e{total}");
        messages.put("silver-check", "&7You have &e{amount} &7silver");
        messages.put("silver-top-header", "&6&lTop Silver Players");
        messages.put("silver-top-format", "&7{position}. &f{player} &8- &e{amount} silver");
        messages.put("no-permission", "&cYou don't have permission to use this command!");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "&cMessage not found: " + key)
                .replace('&', '§');
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        loadMessages();
    }
}