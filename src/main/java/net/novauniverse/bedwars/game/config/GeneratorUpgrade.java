package net.novauniverse.bedwars.game.config;

import org.json.JSONObject;

import net.novauniverse.bedwars.game.generator.GeneratorType;

public class GeneratorUpgrade {
	private GeneratorType type;
	private int timeLeft;
	private int speedIncrement;
	private String name;

	public GeneratorUpgrade(GeneratorType type, int timeLeft, int speedIncrement, String name) {
		this.type = type;
		this.timeLeft = timeLeft;
		this.speedIncrement = speedIncrement;
		this.name = name;
	}

	public static GeneratorUpgrade fromJSON(JSONObject json) {
		GeneratorType type = GeneratorType.valueOf(json.getString("type"));
		String name = json.getString("name");

		int timeLeft = json.getInt("time");
		int speedIncrement = json.getInt("speed_increment");

		return new GeneratorUpgrade(type, timeLeft, speedIncrement, name);
	}

	public GeneratorType getType() {
		return type;
	}

	public int getSpeedIncrement() {
		return speedIncrement;
	}

	public int getTimeLeft() {
		return timeLeft;
	}
	
	public boolean isFinished() {
		return timeLeft <= 0;
	}

	public void decrement() {
		if (timeLeft > 0) {
			this.timeLeft--;
		}
	}

	public String getName() {
		return name;
	}
}