package net.novauniverse.bedwars.game.config;

import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.XYZLocation;
import org.bukkit.World;
import org.json.JSONObject;

public class ConfiguredBaseData {
	private XYZLocation spawnLocation;
	private XYZLocation bedLocation;
	private XYZLocation itemShopLocation;
	private XYZLocation upgradeShopLocation;

	public ConfiguredBaseData(JSONObject json) {
		spawnLocation = new XYZLocation(json.getJSONObject("spawn_location"));
		bedLocation = new XYZLocation(json.getJSONObject("spawn_location"));
		itemShopLocation = new XYZLocation(json.getJSONObject("item_shop_location"));
		upgradeShopLocation = new XYZLocation(json.getJSONObject("upgrade_shop_location"));
	}

	public BaseData toBaseData(World world, Team owner) {
		return new BaseData(owner, spawnLocation.toBukkitLocation(world), bedLocation.toBukkitLocation(world), itemShopLocation.toBukkitLocation(world), upgradeShopLocation.toBukkitLocation(world));
	}
}