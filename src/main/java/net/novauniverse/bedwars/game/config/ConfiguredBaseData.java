package net.novauniverse.bedwars.game.config;

import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.LocationData;
import net.zeeraa.novacore.spigot.utils.XYZLocation;
import org.bukkit.World;
import org.json.JSONObject;

public class ConfiguredBaseData {
	private LocationData spawnLocation;
	private XYZLocation forgeLocation;
	private XYZLocation bedLocation;
	private LocationData itemShopLocation;
	private LocationData upgradeShopLocation;
	public ConfiguredBaseData(JSONObject json) {
		spawnLocation = LocationData.fromJSON(json.getJSONObject("spawn_location"));
		forgeLocation = XYZLocation.fromJSON(json.getJSONObject("forge_location"));
		bedLocation = new XYZLocation(json.getJSONObject("bed_location"));
		itemShopLocation = LocationData.fromJSON(json.getJSONObject("item_shop_location"));
		upgradeShopLocation = LocationData.fromJSON(json.getJSONObject("upgrade_shop_location"));
	}

	public BaseData toBaseData(World world, Team owner) {
		return new BaseData(owner, spawnLocation.toLocation(world), bedLocation.toBukkitLocation(world), forgeLocation.toBukkitLocation(world), itemShopLocation.toLocation(world), upgradeShopLocation.toLocation(world));
	}
}