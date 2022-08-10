package net.novauniverse.bedwars;

import net.novauniverse.bedwars.game.holder.ItemShopHolder;
import net.novauniverse.bedwars.game.holder.UpgradeShopHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class NovaBedwars extends JavaPlugin implements Listener {
    private static NovaBedwars instance;

    public static NovaBedwars getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void ItemClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getHolder() instanceof ItemShopHolder || e.getClickedInventory().getHolder() instanceof UpgradeShopHolder) {
        }
    }
}
