package net.novauniverse.bedwars.game.config;

import net.novauniverse.bedwars.game.config.event.BedBreak;
import net.novauniverse.bedwars.game.config.event.BedwarsEvent;
import net.novauniverse.bedwars.game.config.event.EndGame;
import net.novauniverse.bedwars.game.config.event.EventType;
import net.novauniverse.bedwars.game.config.event.GeneratorUpgrade;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModule;
import net.zeeraa.novacore.spigot.utils.XYZLocation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BedwarsConfig extends MapModule {

	private XYZLocation mapCenter;

	private List<ConfiguredBaseData> bases;
	private List<XYZLocation> diamondGenerators;
	private List<XYZLocation> emeraldGenerators;

	private int borderRadius;
	private double healPoolRadius;

	private int initialIronTime;
	private int initialGoldTime;
	private int initialDiamondTime;
	private int initialEmeraldTime;

	private double dragonRadius;

	private int emeraldForgeTime;

	private List<BedwarsEvent> events;

	public BedwarsConfig(JSONObject json) {
		super(json);

		this.mapCenter = XYZLocation.fromJSON(json.getJSONObject("center"));

		this.borderRadius = json.getInt("border_radius");
		this.healPoolRadius = json.getDouble("heal_pool_radius");

		this.events = new ArrayList<>();

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

		emeraldForgeTime = json.getInt("emerald_forge_time");

		dragonRadius = json.getDouble("dragon_radius");

		JSONArray upgrades = json.getJSONArray("events");
		for (int i = 0; i < upgrades.length(); i++) {
			EventType type = upgrades.getJSONObject(i).getEnum(EventType.class, "event");
			if (type == EventType.UPGRADE) {
				this.events.add(GeneratorUpgrade.fromJSON(upgrades.getJSONObject(i)));
			} else if (type == EventType.BED_BREAK) {
				this.events.add(BedBreak.fromJSON(upgrades.getJSONObject(i)));
			} else if (type == EventType.END_GAME) {
				this.events.add(EndGame.fromJSON(upgrades.getJSONObject(i)));
			}

		}
	}

	public int getBorderRadius() {
		return borderRadius;
	}

	public XYZLocation getMapCenter() {
		return mapCenter;
	}

	public List<BedwarsEvent> getEvents() {
		return events;
	}

	public List<ConfiguredBaseData> getBases() {
		return bases;
	}

	public double getHealPoolRadius() {
		return healPoolRadius;
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

	public int getEmeraldForgeTime() {
		return emeraldForgeTime;
	}

	public double getDragonRadius() {
		return dragonRadius;
	}
}