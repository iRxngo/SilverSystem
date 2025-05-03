package me.irxngo.silversystem.commands;

import me.irxngo.silversystem.SilverSystem;
import me.irxngo.silversystem.database.PlayerData;
import me.irxngo.silversystem.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TopSilverCommand implements CommandExecutor {
    private final SilverSystem plugin;
    private final ConfigManager config;

    public TopSilverCommand(SilverSystem plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<PlayerData> topPlayers = plugin.getSilverDatabase().getCachedTopPlayers();

        sender.sendMessage(config.getMessage("silver-top-header"));

        for (int i = 0; i < topPlayers.size(); i++) {
            PlayerData data = topPlayers.get(i);
            sender.sendMessage(config.getMessage("silver-top-format")
                    .replace("{position}", String.valueOf(i + 1))
                    .replace("{player}", data.getName())
                    .replace("{amount}", String.valueOf(data.getSilver())));
        }

        return true;
    }
}