package net.novauniverse.bedwars.game.entity;

import net.novauniverse.bedwars.NovaBedwars;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
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

		spawnVillager();

		Task.tryStartTask(task);
	}

	private void spawnVillager() {
		if (villager != null) {
			if (!villager.isDead()) {
				villager.remove();
			}
		}

		villager = (Villager) getLocation().getWorld().spawnEntity(getLocation(), EntityType.VILLAGER);
		villager.setNoDamageTicks(Integer.MAX_VALUE);

		villager.setRemoveWhenFarAway(false);

		VersionIndependentUtils.get().setAI(villager, false);
		VersionIndependentUtils.get().setSilent(villager, true);
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

	public void checkVillager() {
		if (villager != null) {
			if (!villager.isDead()) {
				return;
			}
		}
		spawnVillager();
	}

	public void lookAtPlayer() {
		/*
		 * villager.getWorld().getNearbyEntities(villager.getLocation(), 3, 3,
		 * 3).stream().filter(e -> e.getType() ==
		 * EntityType.PLAYER).findFirst().ifPresent(entity -> { if (entity instanceof
		 * Player) { Player player = (Player) entity; Vector vec =
		 * player.getLocation().toVector().subtract(villager.getLocation().toVector());
		 * Location location = villager.getLocation().clone();
		 * location.setDirection(vec); location.setX(villager.getLocation().getX());
		 * location.setY(villager.getLocation().getY());
		 * location.setZ(villager.getLocation().getZ()); villager.teleport(location); }
		 * });
		 */
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