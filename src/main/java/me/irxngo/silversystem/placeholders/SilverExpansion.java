package me.irxngo.silversystem.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.irxngo.silversystem.SilverSystem;
import me.irxngo.silversystem.database.PlayerData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SilverExpansion extends PlaceholderExpansion {
    private final SilverSystem plugin;

    public SilverExpansion(SilverSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "silversystem";
    }

    @Override
    public @NotNull String getAuthor() {
        return "iRxngo";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        if (params.equalsIgnoreCase("silver")) {
            return String.valueOf(plugin.getSilverDatabase().getSilver(player.getUniqueId()).join());
        }

        String[] parts = params.split("_");
        if (parts.length >= 3 && parts[0].equalsIgnoreCase("top")) {
            try {
                int position = Integer.parseInt(parts[1]) - 1;
                String type = parts[2];

                List<PlayerData> top = plugin.getSilverDatabase().getCachedTopPlayers();
                if (position >= 0 && position < top.size()) {
                    PlayerData data = top.get(position);

                    if (type.equalsIgnoreCase("name")) {
                        return data.getName();
                    } else if (type.equalsIgnoreCase("silver")) {
                        return String.valueOf(data.getSilver());
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        return null;
    }
}