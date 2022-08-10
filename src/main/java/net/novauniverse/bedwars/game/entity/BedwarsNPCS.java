package net.novauniverse.bedwars.game.entity;

import net.novauniverse.bedwars.NovaBedwars;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class BedwarsNPCS {
	private final Location location;

	public BedwarsNPCS(Location location) {
		this.location = location;
	}

	public abstract void spawn();

	public void lookAtPlayerRunnable(Entity entity) {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				entity.getWorld().getNearbyEntities(entity.getLocation(), 3, 3, 3).stream().filter(e -> e.getType() == EntityType.PLAYER).findFirst().ifPresent(entity -> {
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
		};
		runnable.runTaskTimer(NovaBedwars.getInstance(), 0, 1);
	}

	public Location getLocation() {
		return location;
	}
}