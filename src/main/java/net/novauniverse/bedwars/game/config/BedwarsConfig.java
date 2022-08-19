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

	private int initialIronTime;
	private int initialGoldTime;
	private int initialDiamondTime;
	private int initialEmeraldTime;
	
	private int bedDestructionTime;

	private List<GeneratorUpgrade> upgrades;

	public BedwarsConfig(JSONObject json) {
		super(json);

		this.upgradeTime = json.getInt("upgrade_time");
		this.generatorSpeed = json.getInt("generator_speed");

		this.upgrades = new ArrayList<>();

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

		initialIronTime = json.getInt("initial_iron_time");
		initialGoldTime = json.getInt("initial_gold_time");
		initialDiamondTime = json.getInt("initial_diamond_time");
		initialEmeraldTime = json.getInt("initial_emerald_time");

		bedDestructionTime = json.getInt("bed_destruction_timer");
		
		JSONArray upgrades = json.getJSONArray("upgrades");
		for (int i = 0; i < upgrades.length(); i++) {
			this.upgrades.add(GeneratorUpgrade.fromJSON(upgrades.getJSONObject(i)));
		}
	}

	public List<GeneratorUpgrade> getUpgrades() {
		return upgrades;
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

	public int getInitialIronTime() {
		return initialIronTime;
	}

	public int getInitialGoldTime() {
		return initialGoldTime;
	}

	public int getInitialDiamondTime() {
		return initialDiamondTime;
	}

	public int getInitialEmeraldTime() {
		return initialEmeraldTime;
	}
	
	public int getBedDestructionTime() {
		return bedDestructionTime;
	}
}