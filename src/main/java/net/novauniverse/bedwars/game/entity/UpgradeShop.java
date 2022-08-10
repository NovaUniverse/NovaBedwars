package net.novauniverse.bedwars.game.entity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;

public class UpgradeShop extends BedwarsNPCS {
	public UpgradeShop(Location location) {
		super(location);
	}

	@Override
	public void spawn() {
		ArmorStand stand = (ArmorStand) getLocation().getWorld().spawnEntity(getLocation(), EntityType.ARMOR_STAND);
		Villager villager = (Villager) getLocation().getWorld().spawnEntity(getLocation(), EntityType.VILLAGER);
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setCustomNameVisible(true);
		stand.setNoDamageTicks(Integer.MAX_VALUE);
		stand.setMarker(true);
		stand.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + "TEAM UPGRADES");
		villager.setNoDamageTicks(Integer.MAX_VALUE);
		VersionIndependentUtils.get().setAI(villager, false);
		lookAtPlayerRunnable(villager);
	}
}