package me.irxngo.silversystem.database;

import me.irxngo.silversystem.SilverSystem;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Database {
    private final SilverSystem plugin;
    private Connection connection;
    private List<PlayerData> topPlayersCache;
    private long lastCacheUpdate;

    public Database(SilverSystem plugin) {
        this.plugin = plugin;
        this.topPlayersCache = new ArrayList<>();
    }

    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/silver.db");

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (" +
                        "uuid TEXT PRIMARY KEY, " +
                        "player_name TEXT, " +
                        "silver INTEGER DEFAULT 0, " +
                        "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            }
            updateTopPlayersCache();
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
        }
    }

    private void updateTopPlayersCache() {
        int cacheMinutes = plugin.getConfigManager().getConfig().getInt("top-players-cache", 5);
        if (System.currentTimeMillis() - lastCacheUpdate > cacheMinutes * 60 * 1000) {
            getTopPlayers(10).thenAccept(topPlayers -> {
                this.topPlayersCache = topPlayers;
                this.lastCacheUpdate = System.currentTimeMillis();
            });
        }
    }

    public CompletableFuture<Integer> getSilver(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT silver FROM player_data WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("silver");
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to get silver: " + e.getMessage());
            }
            return 0;
        });
    }

    public CompletableFuture<Void> setSilver(UUID uuid, int amount) {
        return CompletableFuture.runAsync(() -> {
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();

            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT OR REPLACE INTO player_data (uuid, player_name, silver) VALUES (?, ?, ?)")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, playerName);
                ps.setInt(3, amount);
                ps.executeUpdate();

                if (topPlayersCache.stream().anyMatch(p -> p.getSilver() < amount)) {
                    updateTopPlayersCache();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to set silver: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> addSilver(UUID uuid, int amount) {
        return getSilver(uuid).thenCompose(current -> setSilver(uuid, current + amount));
    }

    public CompletableFuture<List<PlayerData>> getTopPlayers(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerData> topPlayers = new ArrayList<>();

            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT uuid, player_name, silver FROM player_data ORDER BY silver DESC LIMIT ?")) {
                ps.setInt(1, limit);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    topPlayers.add(new PlayerData(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getString("player_name"),
                            rs.getInt("silver")
                    ));
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to get top players: " + e.getMessage());
            }

            return topPlayers;
        });
    }

    public List<PlayerData> getCachedTopPlayers() {
        updateTopPlayersCache();
        return topPlayersCache;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close database connection: " + e.getMessage());
        }
    }
}