package net.novauniverse.bedwars.game.config;

import org.json.JSONObject;

import net.novauniverse.bedwars.game.generator.GeneratorType;

public class GeneratorUpgrade extends BedwarsEvent {
	private GeneratorType type;
	private int speedIncrement;
	public GeneratorUpgrade(EventType eventType, GeneratorType type, int timeLeft, int speedIncrement, String name) {
		super(eventType, timeLeft, name);
		this.type = type;
		this.speedIncrement = speedIncrement;
	}

	public static GeneratorUpgrade fromJSON(JSONObject json) {
		GeneratorType type = GeneratorType.valueOf(json.getString("type"));
		String name = json.getString("name");
		EventType eventType = json.getEnum(EventType.class, "event");
		int timeLeft = json.getInt("time");
		int speedIncrement = json.getInt("speed_increment");

		return new GeneratorUpgrade(eventType, type, timeLeft, speedIncrement, name);
	}

	public GeneratorType getType() {
		return type;
	}

	public int getSpeedIncrement() {
		return speedIncrement;
	}

}