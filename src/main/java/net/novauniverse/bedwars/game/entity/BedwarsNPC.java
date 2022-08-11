package net.novauniverse.bedwars.game.entity;

import net.novauniverse.bedwars.NovaBedwars;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class BedwarsNPC {
	public static final double HOLOGRAM_OFFSET = 2.0D;

	private Location location;
	private NPCType type;

	private Villager villager;
	private Hologram hologram;

	public static String ITEM_SHOP_NAME = ChatColor.YELLOW + "" + ChatColor.BOLD + "Item Shop";
	public static String TEAM_UPGRADE_SHOP_NAME = ChatColor.AQUA + "" + ChatColor.BOLD + "Team Upgrades";

	private boolean spawned;

	private Task task;

	public BedwarsNPC(Location location, NPCType type) {
		this.location = location;
		this.type = type;
		this.spawned = false;

		this.task = new SimpleTask(NovaBedwars.getInstance(), this::lookAtPlayer, 1L);
	}

	public void spawn() {
		if (spawned) {
			return;
		}

		hologram = HologramsAPI.createHologram(NovaBedwars.getInstance(), location.clone().add(0D, HOLOGRAM_OFFSET, 0D));
		hologram.appendTextLine(getName());

		villager = (Villager) getLocation().getWorld().spawnEntity(getLocation(), EntityType.VILLAGER);
		villager.setNoDamageTicks(Integer.MAX_VALUE);
		VersionIndependentUtils.get().setAI(villager, false);

		Task.tryStartTask(task);
	}

	public void dispose() {
		if (!spawned) {
			return;
		}
		Task.tryStopTask(task);
		hologram.delete();
		villager.remove();
	}

	public String getName() {
		switch (type) {
		case ITEMS:
			return ITEM_SHOP_NAME;

		case UPGRADES:
			return TEAM_UPGRADE_SHOP_NAME;

		default:
			return "[INVALID ENUM TYPE]";
		}
	}

	public void lookAtPlayer() {
		villager.getWorld().getNearbyEntities(villager.getLocation(), 3, 3, 3).stream().filter(e -> e.getType() == EntityType.PLAYER).findFirst().ifPresent(entity -> {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				Vector vec = player.getLocation().toVector().subtract(entity.getLocation().toVector());
				Location location = entity.getLocation().setDirection(vec);
				location.setX(entity.getLocation().getX());
				location.setY(entity.getLocation().getY());
				location.setZ(entity.getLocation().getZ());
				entity.teleport(location);
			}
		});
	}

	public boolean isSpawned() {
		return spawned;
	}

	public Location getLocation() {
		return location;
	}

	public NPCType getType() {
		return type;
	}

	public Villager getVillager() {
		return villager;
	}
}