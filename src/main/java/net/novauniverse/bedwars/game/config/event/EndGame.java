package net.novauniverse.bedwars.game.config.event;

import org.json.JSONObject;

public class EndGame extends BedwarsEvent {
	public EndGame(EventType eventType, int timeLeft, String name) {
		super(eventType, timeLeft, name);
	}

	public static EndGame fromJSON(JSONObject json) {
		String name = json.getString("name");
		EventType eventType = json.getEnum(EventType.class, "event");
		int timeLeft = json.getInt("time");
		return new EndGame(eventType, timeLeft, name);
	}
}