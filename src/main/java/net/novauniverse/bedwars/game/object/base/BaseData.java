package net.novauniverse.bedwars.game.object.base;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import net.novauniverse.bedwars.game.object.Trap;
import net.zeeraa.novacore.spigot.teams.Team;

public class BaseData {
	private Team owner;

	private int protectionLevel = 0;
	private int hasteLevel = 0;
	private int forgeLevel = 0;

	private boolean sharpness = false;
	private boolean healPool = false;
	private boolean hasBed = true;

	private Location spawnLocation;
	private Location bedLocation;

	private Location itemShopLocation;
	private Location upgradeShopLocation;

	private List<Trap> traps;

	public BaseData(Team owner, Location spawnLocation, Location bedLocation, Location itemShopLocation, Location upgradeShopLocation) {
		this.owner = owner;
		this.spawnLocation = spawnLocation;
		this.bedLocation = bedLocation;
		this.itemShopLocation = itemShopLocation;
		this.upgradeShopLocation = upgradeShopLocation;
		this.traps = new ArrayList<Trap>();
	}

	public Team getOwner() {
		return owner;
	}

	public int getProtectionLevel() {
		return protectionLevel;
	}

	public int getHasteLevel() {
		return hasteLevel;
	}

	public int getForgeLevel() {
		return forgeLevel;
	}

	public boolean hasSharpness() {
		return sharpness;
	}

	public boolean hasHealPool() {
		return healPool;
	}

	public boolean hasBed() {
		return hasBed;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public Location getBedLocation() {
		return bedLocation;
	}

	public Location getItemShopLocation() {
		return itemShopLocation;
	}

	public Location getUpgradeShopLocation() {
		return upgradeShopLocation;
	}

	public void setOwner(Team owner) {
		this.owner = owner;
	}

	public void setProtectionLevel(int level) {
		this.protectionLevel = level;
	}

	public void setHasteLevel(int hasteLevel) {
		this.hasteLevel = hasteLevel;
	}

	public void setForgeLevel(int forgeLevel) {
		this.forgeLevel = forgeLevel;
	}

	public void setSharpness(boolean sharpness) {
		this.sharpness = sharpness;
	}

	public void setHealPool(boolean healPool) {
		this.healPool = healPool;
	}

	public void setBed(boolean bed) {
		this.hasBed = bed;
	}

	public void setTraps(List<Trap> traps) {
		this.traps = traps;
	}

	public void addTrap(Trap trap) {
		this.traps.add(trap);
	}

	public List<Trap> getTraps() {
		return traps;
	}
}