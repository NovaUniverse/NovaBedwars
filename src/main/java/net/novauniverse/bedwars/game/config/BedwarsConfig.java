package net.novauniverse.bedwars.game.config;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BedwarsConfig extends MapModule {
	private int upgradeTime;
	private int generatorSpeed;
	private List<ConfiguredBaseData> bases;

	public BedwarsConfig(JSONObject json) {
		super(json);

		this.upgradeTime = json.getInt("upgrade_time");
		this.generatorSpeed = json.getInt("generator_speed");
		this.bases = new ArrayList<>();
		JSONArray basesJson = json.getJSONArray("bases");
		for (int i = 0; i < basesJson.length(); i++) {
			bases.add(new ConfiguredBaseData(basesJson.getJSONObject(i)));
		}
	}

	public int getUpgradeTime() {
		return upgradeTime;
	}

	public int getGeneratorSpeed() {
		return generatorSpeed;
	}

	public List<ConfiguredBaseData> getBases() {
		return bases;
	}
}