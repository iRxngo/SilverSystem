package me.irxngo.silversystem.commands;

import me.irxngo.silversystem.SilverSystem;
import me.irxngo.silversystem.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class SilverCommand implements CommandExecutor {
    private final SilverSystem plugin;
    private final ConfigManager config;

    public SilverCommand(SilverSystem plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        CompletableFuture<Integer> silverFuture = plugin.getSilverDatabase().getSilver(player.getUniqueId());
        silverFuture.thenAccept(silver -> {
            player.sendMessage(config.getMessage("silver-check")
                    .replace("{amount}", String.valueOf(silver)));
        });

        return true;
    }
}