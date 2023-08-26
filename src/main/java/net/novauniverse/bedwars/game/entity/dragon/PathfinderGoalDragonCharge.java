package net.novauniverse.bedwars.game.entity.dragon;

import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.novauniverse.bedwars.NovaBedwars;
import net.zeeraa.novacore.commons.utils.RandomGenerator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathfinderGoalDragonCharge extends PathfinderGoal {

	private BedwarsDragon owner;
	private Player target;

	public PathfinderGoalDragonCharge(BedwarsDragon owner, Player target) {
		this.owner = owner;
		this.target = target;
	}

	public BedwarsDragon getOwner() {
		return owner;
	}

	public Player getTarget() {
		return target;
	}

	public void setTarget(Player target) {
		this.target = target;
	}

	@Override
	public boolean a() {
		return owner.isAlive() && owner.isCharging();
	}

	@Override
	public void e() {
		if (target == null) {
			int random = RandomGenerator.generate(0, 3);
			if (random == 0) {
				this.targetRandomPlayer();
			} else {
				this.targetClosestPlayer();
			}
		}

		if (owner.noDamageTicks == owner.maxNoDamageTicks && owner.lastDamager.getUniqueID().equals(target.getUniqueId())) {
			target = null;
			owner.setCharging(false);
			owner.resetCharge();
			return;
		}

		owner.target(target.getLocation());

		Location toLook = DragonMath.directionFromLocations(owner.getLocation(), target.getLocation());
		DragonMath.invertDirection(toLook);
		owner.lastYaw = owner.yaw = toLook.getYaw();
		owner.lastPitch = owner.pitch = toLook.getPitch();

		if (owner.world.getEntities(owner, owner.getBoundingBox()).stream().anyMatch(entity -> entity.getUniqueID().equals(target.getUniqueId()))) {
			target = null;
			owner.setCharging(false);
			owner.resetCharge();
		}

	}

	public void targetRandomPlayer() {
		List<Player> online = new ArrayList<>(NovaBedwars.getInstance().getGame().getOnlinePlayers());
		Collections.shuffle(online);
		if (!online.isEmpty()) {
			targetPlayer(online.get(0));
		}
	}

	public void targetClosestPlayer() {

		Player player = null;
		double distance = 0;

		for (Player playing : NovaBedwars.getInstance().getGame().getOnlinePlayers()) {
			if (player == null) {
				player = playing;
				distance = playing.getLocation().distance(owner.getLocation());
			} else {
				if (distance > playing.getLocation().distance(owner.getLocation())) {
					player = playing;
					distance = playing.getLocation().distance(owner.getLocation());
				}
			}
		}

		if (player != null) {
			targetPlayer(player);
		}

	}

	public void targetPlayer(Player player) {
		setTarget(player);
	}
}
