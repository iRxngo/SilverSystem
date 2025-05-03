package me.irxngo.silversystem.listeners;

import me.irxngo.silversystem.SilverSystem;
import me.irxngo.silversystem.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitListener implements Listener {
    private final Database database;

    public JoinQuitListener(SilverSystem plugin) {
        this.database = plugin.getSilverDatabase();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        database.getSilver(event.getPlayer().getUniqueId());
    }

}