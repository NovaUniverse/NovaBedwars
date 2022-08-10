package net.novauniverse.bedwars.game.config;

import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.XYZLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONObject;

public class ConfiguredBaseData {
	private Location spawnLocation;
	private Location bedLocation;
	private XYZLocation itemShopLocation;
	private XYZLocation upgradeShopLocation;

	public ConfiguredBaseData(JSONObject json) {
		spawnLocation = LocationUtils.fromJSONObject(json.getJSONObject("spawn_location"), Bukkit.getServer().getWorlds().stream().findFirst().get());
		bedLocation = LocationUtils.fromJSONObject(json.getJSONObject("bed_location"), Bukkit.getServer().getWorlds().stream().findFirst().get());
		itemShopLocation = new XYZLocation(json.getJSONObject("item_shop_location"));
		upgradeShopLocation = new XYZLocation(json.getJSONObject("upgrade_shop_location"));
	}

	public BaseData toBaseData(World world, Team owner) {

		spawnLocation.setWorld(world);
		bedLocation.setWorld(world);

		return new BaseData(owner, spawnLocation, bedLocation, itemShopLocation, upgradeShopLocation);
	}
}