package net.brunogamer.bedwars.game.entity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;

public class ItemShop extends BedwarsNPCS{
    public ItemShop(Location location) {
        super(location);
    }

    public void spawn() {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomNameVisible(true);
        stand.setNoDamageTicks(Integer.MAX_VALUE);
        stand.setMarker(true);
        stand.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + "ITEM SHOP");
        villager.setNoDamageTicks(Integer.MAX_VALUE);
        noAI(villager);
        lookAtPlayerRunnable(villager);
    }



}
