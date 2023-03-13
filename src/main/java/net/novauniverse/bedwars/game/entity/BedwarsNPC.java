package net.novauniverse.bedwars.game.entity;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.novauniverse.bedwars.NovaBedwars;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class BedwarsNPC {
	public static final double HOLOGRAM_OFFSET = 2.5D;

	private Location location;
	private NPCType type;

	private Villager villager;
	private Hologram hologram;

	public static String ITEM_SHOP_NAME = ChatColor.YELLOW + "" + ChatColor.BOLD + "Item Shop";
	public static String TEAM_UPGRADE_SHOP_NAME = ChatColor.AQUA + "" + ChatColor.BOLD + "Team Upgrades";

	private boolean spawned;

	public BedwarsNPC(Location location, NPCType type) {
		this.location = location;
		this.type = type;
		this.spawned = false;
	}

	public void spawn() {
		if (spawned) {
			return;
		}

		hologram = HologramsAPI.createHologram(NovaBedwars.getInstance(), location.clone().add(0D, HOLOGRAM_OFFSET, 0D));
		hologram.appendTextLine(getName());

		spawnVillager();

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

		// AI fucks up head rotations
		//VersionIndependentUtils.get().setAI(villager, false);

		VersionIndependentUtils.get().setSilent(villager, true);
	}

	public void dispose() {
		if (!spawned) {
			return;
		}
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
		// Log.trace("BedwarsNPC", "Villager seems to be dead, spawning a new one");
		spawnVillager();
	}

	public void lookAtPlayer() {
		villager.teleport(location);
		List<Entity> entities = villager.getWorld().getNearbyEntities(villager.getLocation(), 3, 3, 3).stream().filter(e -> e.getType() == EntityType.PLAYER).collect(Collectors.toList());
		Entity entity = getClosest(entities, villager.getLocation());
		if (entity != null) {
			Player player = (Player) entity;
			Vector vec = player.getEyeLocation().toVector().subtract(villager.getEyeLocation().toVector()).normalize();
			location.setDirection(vec);
			location.setX(villager.getLocation().getX());
			location.setY(villager.getLocation().getY());
			location.setZ(villager.getLocation().getZ());
			villager.teleport(location);
		}

	}

	private Entity getClosest(List<Entity> list, Location toGo) {
		final Entity[] closest = {null};
		list.forEach(entity -> {
			if (closest[0] == null) {
				closest[0] = entity;
			} else {
				if (toGo.distanceSquared(entity.getLocation()) < toGo.distanceSquared(closest[0].getLocation())) {
					closest[0] = entity;
				}
			}
		});
		return closest[0];
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