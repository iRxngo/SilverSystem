package me.irxngo.silversystem.listeners;

import com.tomkeuper.bedwars.api.events.shop.ShopBuyEvent;
import me.irxngo.silversystem.SilverSystem;
import me.irxngo.silversystem.utils.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopListener implements Listener {
    private final SilverSystem plugin;
    private final ConfigManager config;

    public ShopListener(SilverSystem plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();

    }
    private int countItems(Player player, Material mat) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == mat) {
                total += item.getAmount();
            }
        }
        return total;
    }
    private final Map<UUID, Map<Material, Integer>> inventorySnapshots = new HashMap<>();
    private final List<Material> trackedMaterials = List.of(
            Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD
    );


    @EventHandler
    public void onShopBuy(ShopBuyEvent event) {
        Player player = event.getBuyer();
        UUID uuid = player.getUniqueId();

        Map<Material, Integer> before = inventorySnapshots.getOrDefault(uuid, new HashMap<>());

        int totalSilver = 0;

        for (Material mat : trackedMaterials) {
            int beforeAmount = before.getOrDefault(mat, 0);
            int afterAmount = countItems(player, mat);
            int used = Math.max(0, beforeAmount - afterAmount);
            if (used <= 0) continue;

            int value = switch (mat) {
                case IRON_INGOT -> config.getConfig().getInt("silver-per-iron", 1);
                case GOLD_INGOT -> config.getConfig().getInt("silver-per-gold", 5);
                case DIAMOND -> config.getConfig().getInt("silver-per-diamond", 10);
                case EMERALD -> config.getConfig().getInt("silver-per-emerald", 20);
                default -> 0;
            };
            totalSilver += used * value;
        }

        if (totalSilver > 0) {
            int finalTotalSilver = totalSilver;
            plugin.getSilverDatabase().addSilver(uuid, totalSilver).thenRun(() -> {
                player.sendMessage(config.getMessage("silver-earned")
                        .replace("{amount}", String.valueOf(finalTotalSilver))
                        .replace("{total}", String.valueOf(plugin.getSilverDatabase().getSilver(uuid).join())));
            });
        }

        Map<Material, Integer> newSnapshot = new HashMap<>();
        for (Material mat : trackedMaterials) {
            newSnapshot.put(mat, countItems(player, mat));
        }
        inventorySnapshots.put(uuid, newSnapshot);
    }
}