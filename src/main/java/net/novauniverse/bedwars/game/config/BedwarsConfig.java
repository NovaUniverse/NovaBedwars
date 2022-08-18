package net.novauniverse.bedwars.game.config;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModule;
import net.zeeraa.novacore.spigot.utils.XYZLocation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BedwarsConfig extends MapModule {
	private int upgradeTime;
	private int generatorSpeed;

	private List<ConfiguredBaseData> bases;
	private List<XYZLocation> diamondGenerators;
	private List<XYZLocation> emeraldGenerators;

	public BedwarsConfig(JSONObject json) {
		super(json);

		this.upgradeTime = json.getInt("upgrade_time");
		this.generatorSpeed = json.getInt("generator_speed");

		this.bases = new ArrayList<>();
		this.diamondGenerators = new ArrayList<>();
		this.emeraldGenerators = new ArrayList<>();

		JSONArray basesJson = json.getJSONArray("bases");
		for (int i = 0; i < basesJson.length(); i++) {
			bases.add(new ConfiguredBaseData(basesJson.getJSONObject(i)));
		}

		JSONArray diamondGenJson = json.getJSONArray("diamond_generators");
		for (int i = 0; i < diamondGenJson.length(); i++) {
			diamondGenerators.add(XYZLocation.fromJSON(diamondGenJson.getJSONObject(i)));
		}

		JSONArray emeraldGenJson = json.getJSONArray("emerald_generators");
		for (int i = 0; i < emeraldGenJson.length(); i++) {
			emeraldGenerators.add(XYZLocation.fromJSON(emeraldGenJson.getJSONObject(i)));
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

	public List<XYZLocation> getDiamondGenerators() {
		return diamondGenerators;
	}

	public List<XYZLocation> getEmeraldGenerators() {
		return emeraldGenerators;
	}
}